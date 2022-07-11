package jfmltrainer.trainer.tuning.lateral.selectionvariant;

import jfml.rulebase.RuleBaseType;
import jfmltrainer.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.tuning.lateral.Chromosome;
import jfmltrainer.trainer.tuning.lateral.approach.Evaluator;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LateralDisplacementWithSelection extends LateralSelectionVariant {

    public LateralDisplacementWithSelection(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Boolean getSelected() {
        return Math.random() <= 0.5;
    }

    @Override
    public ImmutablePair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2, Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {

        ImmutablePair<Chromosome, Chromosome> firstChildren = crossoverChildren(parent1, parent2, true);
        ImmutablePair<Chromosome, Chromosome> secondChildren = crossoverChildren(parent1, parent2, false);

        List<Chromosome> chromosomeList = new ArrayList<>();
        chromosomeList.add(firstChildren.getLeft());
        chromosomeList.add(firstChildren.getRight());
        chromosomeList.add(secondChildren.getLeft());
        chromosomeList.add(secondChildren.getRight());

        return getTwoBest(chromosomeList, data, ruleBase, methodConfig);
    }

    @Override
    protected ImmutablePair<List<Boolean>, List<Boolean>> crossoverSelectedRuleList(Chromosome parent1, Chromosome parent2) {
        return Utils.twoPointCrossover(parent1.getSelectedRuleList(), parent2.getSelectedRuleList());
    }


    private ImmutablePair<Chromosome, Chromosome> getTwoBest(List<Chromosome> chromosomeList, Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {

        Comparator<Chromosome> increasingComparator = Comparator.comparing(chromosome -> evaluator.evaluate(data, ruleBase, chromosome, methodConfig));

        List<Chromosome> newList = new ArrayList<>();
        newList.addAll(chromosomeList);
        Collections.sort(newList, increasingComparator.reversed());
        return new ImmutablePair<>(
                newList.get(0),
                newList.get(1)
        );
    }

    private ImmutablePair<Chromosome, Chromosome> crossoverChildren(Chromosome parent1, Chromosome parent2, boolean isFirstChild) {
        List<Float> displacementList = crossoverDisplacement(parent1, parent2, isFirstChild);
        ImmutablePair<List<Boolean>, List<Boolean>> newSelectedRuleList = crossoverSelectedRuleList(parent1, parent2);
        return new ImmutablePair<>(
                new Chromosome(displacementList, newSelectedRuleList.getLeft()),
                new Chromosome(displacementList, newSelectedRuleList.getRight())
        );
    }
}
