package jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.local;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.Chromosome;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.LateralApproach;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalApproach extends LateralApproach {

    public LocalApproach() {
        super();
        this.evaluator = new LocalEvaluator();
    }

    @Override
    public Integer getNGenes(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        int nVar = knowledgeBase.getKnowledgeBaseVariables().size();
        int nRules = ruleBase.getRules().size();
        return nVar * nRules;
    }

    @Override
    public RuleBaseType buildRuleBase(KnowledgeBaseType oldKnowledgeBase, RuleBaseType oldRuleBase, Chromosome chromosome) {
        Integer nVar = oldKnowledgeBase.getKnowledgeBaseVariables().size();

        List<FuzzyRuleType> ruleList = IntStream.range(0, oldRuleBase.getRules().size()).boxed()
                .filter(i -> chromosome.getSelectedRuleList().get(i))
                .map(i -> {
                    // This method needs a sliding window to go along the chromosome
                    List<Float> geneList = chromosome.getGeneList().subList(i*nVar, (i+1)*nVar);
                    return updateRule(oldKnowledgeBase, oldRuleBase.getRules().get(i), geneList);
                })
                .collect(Collectors.toList());

        return RuleBaseTrainerUtils.buildRuleBase(ruleList);
    }
}
