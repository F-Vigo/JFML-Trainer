package jfmltrainer.trainer.tuning.lateral.approach.global;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.trainer.tuning.lateral.Chromosome;
import jfmltrainer.trainer.tuning.lateral.approach.LateralApproach;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlobalApproach extends LateralApproach {

    @Override
    public Integer getNGenes(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        return knowledgeBase.getKnowledgeBaseVariables().stream()
                .map(variable -> variable.getTerms().size())
                .reduce(Integer::sum).get();
    }

    @Override
    public RuleBaseType buildRuleBase(KnowledgeBaseType oldKnowledgeBase, RuleBaseType oldRuleBase, Chromosome chromosome) {
        List<FuzzyRuleType> ruleList = IntStream.range(0, oldRuleBase.getRules().size()).boxed()
                .filter(i -> chromosome.getSelectedRuleList().get(i))
                .map(i -> updateRule(oldKnowledgeBase, oldRuleBase.getRules().get(i), chromosome.getGeneList()))
                .collect(Collectors.toList());
        return RuleBaseTrainerUtils.buildRuleBase(ruleList);
    }
}
