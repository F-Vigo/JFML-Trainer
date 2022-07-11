package jfmltrainer.trainer.rulebasetrainer.regression.thrift;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.trainer.rulebasetrainer.regression.RegressionTrainer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Thrift extends RegressionTrainer {

    private static Integer MAX_ITER = 100; // TODO
    private static Integer POPULATION_SIZE = 100; // TODO
    private static Float MUTATION_PROB = 0.8F; // TODO
    private static Evaluator evaluator = new Evaluator();

    @Override
    protected ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<FuzzyRuleType> ruleList = geneticAlgorithm(data, knowledgeBase, methodConfig);
        return new ImmutablePair<>(knowledgeBase, RuleBaseTrainerUtils.buildRuleBase(ruleList));
    }

    private List<FuzzyRuleType> geneticAlgorithm(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<Chromosome> population = getInitialPopulation(data, knowledgeBase, methodConfig);
        for (int i = 0; i < MAX_ITER; i++) {
            population = nextGeneration(population, data, knowledgeBase, methodConfig);
        }
        List<List<FuzzyTermType>> splitTermList = decodeChromosome(population.get(0), knowledgeBase); // The first element is the best
        List<FuzzyRuleType> ruleList = splitTermList.stream()
                .map(termList -> RuleBaseTrainerUtils.buildRuleFromTermList(termList, knowledgeBase))
                .collect(Collectors.toList());
        return ruleList;
    }



    // INITIAL POPULATION //

    private List<Chromosome> getInitialPopulation(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Optional<FuzzyTermType>> geneList = getRandomGeneList(getNGenes(knowledgeBase), knowledgeBase, methodConfig).stream()
                    .map(Optional::ofNullable)
                    .collect(Collectors.toList());
            Chromosome chromosome = new Chromosome(geneList, null);
            Float fitness = (float) evaluator.evaluate(data, decodeChromosome(chromosome, knowledgeBase), knowledgeBase, methodConfig);
            chromosome.setFitness(fitness);
            population.add(chromosome);
        }
        return population;
    }

    private Integer getNGenes(KnowledgeBaseType knowledgeBase) {
        return knowledgeBase.getKnowledgeBaseVariables().stream()
                .filter(KnowledgeBaseVariable::isInput)
                .map(var -> var.getTerms().size())
                .reduce((x, y) -> x * y)
                .get();
    }

    private List<FuzzyTermType> getRandomGeneList(Integer nGenes, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<FuzzyTermType> geneList = new ArrayList<>();
        Integer outputPos = IntStream.range(0, knowledgeBase.getKnowledgeBaseVariables().size()).boxed()
                .filter(i -> knowledgeBase.getKnowledgeBaseVariables().get(i).isOutput())
                .findFirst().get();
        for (int i = 0; i < nGenes; i++) {
            geneList.add(getRandomTerm(knowledgeBase.getKnowledgeBaseVariables().get(outputPos)));
        }
        return geneList;
    }

    private FuzzyTermType getRandomTerm(KnowledgeBaseVariable variable) {
        Integer termListSize = variable.getTerms().size();
        Integer termPos = (int) (Math.random() * termListSize);
        return (FuzzyTermType) (variable.getTerms().get(termPos));
    }



    // MAIN LOOP //

    private List<Chromosome> nextGeneration(List<Chromosome> population, Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<ImmutablePair<Chromosome, Chromosome>> parentList = getParentList(population);
        List<Chromosome> offspringList = getOffspring(parentList, data, knowledgeBase, methodConfig);
        List<Chromosome> nextGeneration = combineGenerations(population, offspringList);
        if (MUTATION_PROB > 0) {
            nextGeneration = mutate(nextGeneration, data, knowledgeBase, methodConfig);
        }
        return nextGeneration;
    }

    private List<ImmutablePair<Chromosome, Chromosome>> getParentList(List<Chromosome> population) {
        List<Float> fitnessList = population.stream()
                .map(Chromosome::getFitness)
                .collect(Collectors.toList());
        Float totalProb = fitnessList.stream()
                .reduce(Float::sum)
                .get();
        List<ImmutablePair<Chromosome, Chromosome>> parentList = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
            Integer pickedPosition1 = pickParentPosition(Math.random(), fitnessList, totalProb);
            Integer pickedPosition2 = pickParentPosition(Math.random(), fitnessList, totalProb);
            parentList.add(new ImmutablePair<>(population.get(pickedPosition1), population.get(pickedPosition2)));
        }
        return parentList;
    }

    private Integer pickParentPosition(double random, List<Float> fitnessList, Float totalProb) {
        return pickParentPositionAux(random, 0, fitnessList.get(0), fitnessList.subList(1, fitnessList.size()), totalProb);
    }

    private Integer pickParentPositionAux(double random, int i, Float accumSum, List<Float> remainingFitnessList, Float totalProb) {
        if ((accumSum / totalProb) >= random) {
            return i;
        }
        return pickParentPositionAux(random, i + 1, accumSum + remainingFitnessList.get(0), remainingFitnessList.subList(1, remainingFitnessList.size()), totalProb);
    }

    private List<Chromosome> getOffspring(
            List<ImmutablePair<Chromosome, Chromosome>> parentList,
            Data<RegressionInstance> data,
            KnowledgeBaseType knowledgeBase,
            MethodConfig methodConfig
    ) {
        List<Chromosome> offspringList = new ArrayList<>();
        for (ImmutablePair<Chromosome, Chromosome> parentPair : parentList) {
            ImmutablePair<List<Optional<FuzzyTermType>>, List<Optional<FuzzyTermType>>> offspring = Utils.twoPointCrossover(
                    parentPair.getLeft().getGeneList(),
                    parentPair.getRight().getGeneList()
            );

            Chromosome child1 = new Chromosome(
                    offspring.getLeft(),
                    null
            );
            Chromosome child2 = new Chromosome(
                    offspring.getRight(),
                    null
            );

            child1.setFitness((float) evaluator.evaluate(data, decodeChromosome(child1, knowledgeBase), knowledgeBase, methodConfig));
            child2.setFitness((float) evaluator.evaluate(data, decodeChromosome(child2, knowledgeBase), knowledgeBase, methodConfig));

            offspringList.add(child1);
            offspringList.add(child2);
        }
        return offspringList;
    }

    private List<Chromosome> combineGenerations(List<Chromosome> population, List<Chromosome> offspringList) {
        List<Chromosome> nextGeneration = new ArrayList<>();
        nextGeneration.addAll(population);
        nextGeneration.addAll(offspringList);

        Comparator<Chromosome> increasingComparator = Comparator.comparing(Chromosome::getFitness);
        return nextGeneration.stream()
                .sorted(increasingComparator.reversed())
                .collect(Collectors.toList())
                .subList(0, POPULATION_SIZE);
    }

    private List<Chromosome> mutate(List<Chromosome> nextGeneration, Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {

        int muNext = (int) Math.ceil(Math.log(Math.random()) / Math.log(1.0 - MUTATION_PROB));
        int nGenes = nextGeneration.get(0).getGeneList().size();
        int allPositions = POPULATION_SIZE * nGenes;

        while (muNext < allPositions) {
            int chromosomeIndex = muNext / nGenes;
            int geneIndex = muNext % nGenes;
            List<FuzzyTermType> outputTermList = (List<FuzzyTermType>) knowledgeBase.getKnowledgeBaseVariables().stream()
                    .filter(KnowledgeBaseVariable::isOutput)
                    .findFirst().get()
                    .getTerms();
            List<Optional<FuzzyTermType>> geneList = mutateChromosome(nextGeneration.get(chromosomeIndex), geneIndex, outputTermList);
            Chromosome newChromosome = new Chromosome(geneList, null);
            Float fitness = (float) evaluator.evaluate(data, decodeChromosome(newChromosome, knowledgeBase), knowledgeBase, methodConfig);
            newChromosome.setFitness(fitness);
            nextGeneration.set(chromosomeIndex, newChromosome);
            muNext += (int) Math.ceil(Math.log(Math.random()) / Math.log(1.0 - MUTATION_PROB));
        }
        return nextGeneration;
    }

    private List<Optional<FuzzyTermType>> mutateChromosome(Chromosome chromosome, int geneIndex, List<FuzzyTermType> outputTermList) {
        Optional<FuzzyTermType> oldGene = chromosome.getGeneList().get(geneIndex);
        Integer newGenePos = null;

        if (oldGene.isEmpty()) {
            newGenePos = (int) (Math.random() * (outputTermList.size()));
        } else {
            Integer termPos = IntStream.range(0, outputTermList.size()).boxed()
                    .filter(i -> outputTermList.get(i).getName().equals(oldGene.get().getName()))
                    .findFirst().get();
            if (termPos == 0) {
                newGenePos = 1;
            } else if (termPos == outputTermList.size() - 1) {
                newGenePos = outputTermList.size() - 2;
            } else if (Math.random() < 0.5) {
                newGenePos = termPos - 1;
            } else {
                newGenePos = termPos + 1;
            }
        }
        Optional<FuzzyTermType> newGene = Optional.ofNullable(outputTermList.get(newGenePos));
        List<Optional<FuzzyTermType>> geneList = chromosome.getGeneList();
        geneList.set(geneIndex, newGene);
        return geneList;
    }



    // AUXILIARY FUNCTIONS //

    private List<List<FuzzyTermType>> decodeChromosome(Chromosome chromosome, KnowledgeBaseType knowledgeBase) {
        List<List<Integer>> auxList = getAuxList(knowledgeBase);
        List<List<FuzzyTermType>> termList = new ArrayList<>();
        for (int i = 0; i < auxList.size(); i++) {
            termList.add(decodeRule(auxList.get(i), chromosome.getGeneList().get(i).get(), knowledgeBase));
        }
        return termList;
    }

    private List<FuzzyTermType> decodeRule(List<Integer> termIndexList, FuzzyTermType outputTerm, KnowledgeBaseType knowledgeBase) {
        List<FuzzyTermType> termList = IntStream.range(0, termIndexList.size()).boxed()
                .map(i -> (FuzzyTermType) knowledgeBase.getKnowledgeBaseVariables().get(i).getTerms().get(termIndexList.get(i)))
                .collect(Collectors.toList());
        termList.add(outputTerm);
        return termList;
    }

    private List<List<Integer>> getAuxList(KnowledgeBaseType knowledgeBase) {
        Integer antecedentSize = (int) knowledgeBase.getKnowledgeBaseVariables().stream().filter(KnowledgeBaseVariable::isInput).count();
        List<Integer> first = new ArrayList<>(antecedentSize);
        Collections.fill(first, 0);
        return getAuxListAux(Collections.singletonList(first), knowledgeBase);
    }

    private List<List<Integer>> getAuxListAux(List<List<Integer>> accum, KnowledgeBaseType knowledgeBase) {
        List<Integer> last = accum.get(accum.size() - 1);
        if (isFulfilled(last, knowledgeBase)) {
            return accum;
        }
        List<Integer> next = getNextList(last, knowledgeBase);
        accum.add(next);
        return getAuxListAux(accum, knowledgeBase);
    }

    private boolean isFulfilled(List<Integer> last, KnowledgeBaseType knowledgeBase) {
        Boolean soFarSoGood = true;
        for (int i = 0; i < last.size() && soFarSoGood; i++) {
            soFarSoGood = isLastTerm(last.get(i), i, knowledgeBase);
        }
        return soFarSoGood;
    }

    private List<Integer> getNextList(List<Integer> last, KnowledgeBaseType knowledgeBase) {
        List<Integer> next = new ArrayList<>();
        next.addAll(last);
        Integer i = (int) knowledgeBase.getKnowledgeBaseVariables().stream().filter(KnowledgeBaseVariable::isInput).count() - 1;
        Boolean keepLooping = true;
        while (keepLooping) {
            Integer termIndex = last.get(i);
            if (isLastTerm(termIndex, i, knowledgeBase)) {
                next.set(i, 0);
            } else {
                next.set(i, next.get(i) + 1);
                keepLooping = false;
            }
            i--;
        }
        return next;
    }

    private boolean isLastTerm(Integer termIndex, Integer varIndex, KnowledgeBaseType knowledgeBase) {
        return knowledgeBase.getKnowledgeBaseVariables().get(varIndex).getTerms().size() == (termIndex + 1);
    }
}
