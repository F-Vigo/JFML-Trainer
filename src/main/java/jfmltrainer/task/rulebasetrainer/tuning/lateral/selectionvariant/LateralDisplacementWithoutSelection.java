package jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant;

import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.Chromosome;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.Evaluator;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LateralDisplacementWithoutSelection extends LateralSelectionVariant {

    public LateralDisplacementWithoutSelection(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Boolean getSelected() {
        return true;
    }

    @Override
    public ImmutablePair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2, Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {
        Chromosome firstChild = crossoverChild(parent1, parent2, true);
        Chromosome secondChild = crossoverChild(parent1, parent2, false);
        return new ImmutablePair<>(firstChild, secondChild);
    }

    @Override
    protected ImmutablePair<List<Boolean>, List<Boolean>> crossoverSelectedRuleList(Chromosome parent1, Chromosome parent2) {
        Integer n = parent1.getSelectedRuleList().size();
        List<Boolean> selectedRuleList1 = new ArrayList<>(n);
        List<Boolean> selectedRuleList2 = new ArrayList<>(n);
        Collections.fill(selectedRuleList1, true);
        Collections.fill(selectedRuleList2, true);
        return new ImmutablePair<>(selectedRuleList1, selectedRuleList2);
    }


    private Chromosome crossoverChild(Chromosome parent1, Chromosome parent2, boolean isFirstChild) {
        List<Float> displacementList = crossoverDisplacement(parent1, parent2, isFirstChild);
        ImmutablePair<List<Boolean>, List<Boolean>> newSelectedRuleList = crossoverSelectedRuleList(parent1, parent2);
        return new Chromosome(displacementList, newSelectedRuleList.getLeft());
    }
}
