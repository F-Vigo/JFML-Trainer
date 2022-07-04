package jfmltrainer.trainer.tuning.lateral.selectionvariant;

import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.tuning.lateral.Chromosome;
import jfmltrainer.trainer.tuning.lateral.Evaluator;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public abstract class LateralSelectionVariant {

    protected Evaluator evaluator;


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
            Float gene = l + (u-l)*(float)Math.random();
            geneList.add(gene);
        }
        return geneList;
    }
}
