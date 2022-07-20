package jfmltrainer.trainer.tuning.lateral;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.tuning.Tuner;
import jfmltrainer.trainer.tuning.lateral.approach.LateralApproach;
import jfmltrainer.trainer.tuning.lateral.selectionvariant.LateralSelectionVariant;
import org.apache.commons.lang3.tuple.ImmutablePair;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class LateralDisplacement extends Tuner {

    private JFMLRandom JFMLRandom = new JFMLRandom();

    protected static Integer BITSGENE = 0; // TODO
    protected static Integer MAX_ITER = 0; // TODO
    protected static Integer POPULATION_SIZE = 100; // TODO

    protected GrayCoder grayCoder = new GrayCoder(BITSGENE);
    protected LateralApproach lateralApproach;
    protected LateralSelectionVariant lateralSelectionVariant;

    public ImmutablePair<KnowledgeBaseType, RuleBaseType> tune(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        Chromosome chromosome = CHC(data, ruleBase, POPULATION_SIZE, methodConfig, lateralApproach.getNGenes(knowledgeBase, ruleBase));
        return buildApproximativeKnowledgeAndRuleBases(knowledgeBase, ruleBase, chromosome);
    }

    private Chromosome CHC(Data data, RuleBaseType ruleBase, Integer populationSize, MethodConfig methodConfig, int nGenes) {

        Function<Chromosome, Float> evaluation = chromosome -> (float) lateralApproach.getEvaluator().evaluate(data, ruleBase, chromosome, methodConfig);

        Integer nRules = ruleBase.getRules().size();
        List<Boolean> isSelectedList = new ArrayList<>(ruleBase.getRules().size());
        Collections.fill(isSelectedList, true);
        
        ImmutablePair<List<Chromosome>, Boolean> populationAndNewIndividuals = new ImmutablePair<>(
                restartPopulation(new Chromosome(new ArrayList<>(nGenes), isSelectedList), populationSize, nRules),
                false
        );

        Float L = (float) (nGenes * BITSGENE / 4);
        Boolean keepLooping = true;
        Integer i = 0;
        Chromosome bestSoFar = Utils.argMax(
                populationAndNewIndividuals.getLeft(),
                evaluation::apply
        );
        while(keepLooping) {

            i++;
            populationAndNewIndividuals = nextGenerationAndNewIndividuals(data, ruleBase, methodConfig, populationAndNewIndividuals.getLeft(), L, populationSize);

            if(evaluation.apply(populationAndNewIndividuals.getLeft().get(0)) < evaluation.apply(bestSoFar)) {
                L--;
                if (!populationAndNewIndividuals.getRight()) {
                    L--;
                }
            }
            if (L < 0) {
                populationAndNewIndividuals = new ImmutablePair<>(
                        restartPopulation(populationAndNewIndividuals.getLeft().get(0), populationSize, nRules),
                        false
                );
                bestSoFar = Utils.argMax(
                        populationAndNewIndividuals.getLeft(),
                        evaluation::apply
                );
            }
            if (i.equals(MAX_ITER)) {
                keepLooping = false;
            }
        }
        return bestSoFar;
    }

    private ImmutablePair<List<Chromosome>, Boolean> nextGenerationAndNewIndividuals(
            Data data,
            RuleBaseType ruleBase,
            MethodConfig methodConfig,
            List<Chromosome> population,
            Float L,
            Integer populationSize
    ) {
        List<Chromosome> shuffledPopulation = JFMLRandom.shuffle(population);
        List<ImmutablePair<Chromosome, Chromosome>> parentCandidateList = selectParentCandidates(shuffledPopulation);
        List<Chromosome> offspringList = breedOffspring(parentCandidateList, L, data, ruleBase, methodConfig);

        List<ImmutablePair<Chromosome, Boolean>> newPopulationAndIsOffspring = new ArrayList<>();
        newPopulationAndIsOffspring.addAll(
                shuffledPopulation.stream()
                        .map(parent -> new ImmutablePair<>(parent, false))
                        .collect(Collectors.toList())
        );
        newPopulationAndIsOffspring.addAll(
                offspringList.stream()
                        .map(offspring -> new ImmutablePair<>(offspring, true))
                        .collect(Collectors.toList())
        );
        Comparator<ImmutablePair<Chromosome, Boolean>> increasingComparator = Comparator.comparing(pair -> lateralApproach.getEvaluator().evaluate(data, ruleBase, pair.getLeft(), methodConfig));
        newPopulationAndIsOffspring = newPopulationAndIsOffspring.stream()
                .sorted(increasingComparator.reversed())
                .collect(Collectors.toList());

        Boolean thereWillBeNewIndividuals = newPopulationAndIsOffspring.stream()
                .map(ImmutablePair::getRight)
                .collect(Collectors.toList())
                .subList(0, populationSize)
                .stream().reduce(Boolean::logicalAnd).get();

        return new ImmutablePair<>(
                newPopulationAndIsOffspring.subList(0, populationSize).stream()
                        .map(ImmutablePair::getLeft)
                        .collect(Collectors.toList()),
                thereWillBeNewIndividuals
        );
    }

    private List<Chromosome> breedOffspring(List<ImmutablePair<Chromosome, Chromosome>> parentCandidateList, Float L, Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {
        List<ImmutablePair<Chromosome, Chromosome>> offspringPairList = parentCandidateList.stream()
                .map(parentPair -> breedChildren(parentPair.getLeft(), parentPair.getRight(), L, data, ruleBase, methodConfig))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        List<Chromosome> offspringList = new ArrayList<>();
        offspringPairList.forEach(pair -> {
            offspringList.add(pair.getLeft());
            offspringList.add(pair.getRight());
        });
        return offspringList;
    }

    private Optional<ImmutablePair<Chromosome, Chromosome>> breedChildren(Chromosome parent1, Chromosome parent2, Float L, Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {;
        List<Character> parent1Char = grayCoder.toGrayCode(parent1);
        List<Character> parent2Char = grayCoder.toGrayCode(parent2);
        Integer hammingDistance = IntStream.range(0, parent1.getGeneList().size()).boxed()
                .map(i -> parent1Char.get(i) == parent2Char.get(i))
                .map(equals -> equals ? 0 : 1)
                .reduce(Integer::sum).get();
        return hammingDistance / 2 >= L
                ? Optional.of(lateralSelectionVariant.crossover(parent1, parent2, data, ruleBase, methodConfig))
                : Optional.empty();
    }

    private List<ImmutablePair<Chromosome, Chromosome>> selectParentCandidates(List<Chromosome> population) {
        List<Integer> positions = IntStream.range(0, population.size()).boxed().collect(Collectors.toList());
        List<Integer> shuffledPositions = JFMLRandom.shuffle(positions);
        List<ImmutablePair<Chromosome, Chromosome>> parentCandidateList = new ArrayList<>();
        for (int i = 0; i < population.size(); i+=2) {
            parentCandidateList.add(new ImmutablePair<>(
                    population.get(shuffledPositions.get(i)),
                    population.get(shuffledPositions.get(i+1))
            ));
        }
        return parentCandidateList;
    }

    private List<Chromosome> restartPopulation(Chromosome initialChromosome, Integer populationSize, Integer nRules) {
        Integer nGenes = initialChromosome.getGeneList().size();
        List<Chromosome> population = new ArrayList<>();
        population.add(initialChromosome);
        for (int i = 1; i < populationSize; i++) {
            population.add(getRandomChromosome(nGenes, nRules));
        }
        return population;
    }

    private Chromosome getRandomChromosome(Integer nGenes, Integer nRules) {
        
        List<Float> geneList = new ArrayList<>();
        for (int i = 0; i < nGenes; i++) {
            geneList.add(getRandom());
        }
        
        List<Boolean> ruleSelectedList = new ArrayList<>(nRules);
        for (int i = 0; i < nRules; i++) {
            ruleSelectedList.add(lateralSelectionVariant.getSelected());
        }
        
        return new Chromosome(geneList, ruleSelectedList);
    }

    private Float getRandom() {
        return JFMLRandom.randReal(-0.5F, 0.5F);
    }


    protected ImmutablePair<KnowledgeBaseType, RuleBaseType> buildApproximativeKnowledgeAndRuleBases(KnowledgeBaseType oldKnowledgeBase, RuleBaseType oldRuleBase, Chromosome chromosome) {
        RuleBaseType newRuleBase = lateralApproach.buildRuleBase(oldKnowledgeBase, oldRuleBase, chromosome);
        KnowledgeBaseType newKnowledgeBase = Utils.buildApproximativeKnowledgeBase(oldKnowledgeBase, newRuleBase);
        return new ImmutablePair<>(newKnowledgeBase, newRuleBase);
    }
}
