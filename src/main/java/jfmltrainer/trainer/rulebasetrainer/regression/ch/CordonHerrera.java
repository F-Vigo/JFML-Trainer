package jfmltrainer.trainer.rulebasetrainer.regression.ch;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.trainer.rulebasetrainer.regression.RegressionTrainer;
import lombok.Value;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.stream.Collectors;

public class CordonHerrera extends RegressionTrainer {

    @Value
    private static class RuleWithRVF {
        FuzzyRuleType rule;
        float RVF;
    }

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<ImmutablePair<List<Instance>, FuzzyRuleType>> candidateRuleList = CordonHerreraUtils.generateCHCandidateRuleList(data, knowledgeBase);
        List<RuleWithRVF> ruleWithRVFList = candidateRuleList.stream()
                .map(instanceListAndRule -> enrichWithRVF(instanceListAndRule, knowledgeBase, methodConfig.getAndOperator().get(), methodConfig.getThenOperator().get(), methodConfig.getRvfOperator().get()))
                .collect(Collectors.toList());
        List<FuzzyRuleType> prunedRuleList = prune(ruleWithRVFList);
        RuleBaseType ruleBase = RuleBaseTrainerUtils.buildRuleBase(prunedRuleList);
        return new ImmutablePair<>(knowledgeBase, ruleBase);
    }

    private RuleWithRVF enrichWithRVF(ImmutablePair<List<Instance>, FuzzyRuleType> instanceListAndRule, KnowledgeBaseType knowledgeBase, AndOperator andOperator, ThenOperator thenOperator, RVFOperator rvfOperator) {
        List<Instance> instanceList = instanceListAndRule.getLeft();
        FuzzyRuleType rule = instanceListAndRule.getRight();
        List<Float> weightList = instanceList.stream()
                .map(instance -> RuleBaseTrainerUtils.computeWeight(instance, rule, andOperator, thenOperator))
                .collect(Collectors.toList());
        return new RuleWithRVF(rule, rvfOperator.apply(weightList));
    }

    private List<FuzzyRuleType> prune(List<RuleWithRVF> ruleWithRVFList) {
        return ruleWithRVFList.stream()
                .collect(Collectors.groupingBy(weightedRule -> RuleBaseTrainerUtils.getAntecedentClausesAsString(weightedRule.getRule())))
                .values().stream()
                .map(this::getMaxRVFRule)
                .collect(Collectors.toList());
    }

    private FuzzyRuleType getMaxRVFRule(List<RuleWithRVF> ruleWithRVFList) {
        return Utils.argMax(ruleWithRVFList, RuleWithRVF::getRVF)
                .getRule();
    }
}
