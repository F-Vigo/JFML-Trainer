package jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant;

import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.Chromosome;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.Evaluator;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public abstract class LateralSelectionVariant {

    protected Evaluator evaluator;
    protected JFMLRandom JFMLRandom = jfmltrainer.aux.JFMLRandom.getInstance();


    public abstract Boolean getSelected();

    public abstract ImmutablePair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2, Data data, RuleBaseType ruleBase, MethodConfig methodConfig);

    protected abstract ImmutablePair<List<Boolean>, List<Boolean>> crossoverSelectedRuleList(Chromosome parent1, Chromosome parent2);




    protected List<Float> crossoverDisplacement(Chromosome parent1, Chromosome parent2, Boolean isFirstChild) {
        List<Float> geneList = new ArrayList<>();
        Float a = -0.5F;
        Float b = 0.5F;

        for (int i = 0; i < parent1.getGeneList().size(); i++) {
            Float x = parent1.getGeneList().get(i);
            Float y = parent2.getGeneList().get(i);
            Float I = Math.abs(x-y);
            Float pivot = isFirstChild ? x : y;
            Float l = Math.max(a, pivot-I);
            Float u = Math.min(b, pivot-I);
            Float gene = JFMLRandom.randReal(l, u);
            geneList.add(gene);
        }
        return geneList;
    }
}
