package jfmltrainer.trainer.rulebasetrainer.regression.wm;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.trainer.rulebasetrainer.regression.RegressionTrainer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class WangMendel extends RegressionTrainer {

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<ImmutablePair<Instance, FuzzyRuleType>> candidateRuleList = WangMendelUtils.generateWMCandidateRuleList(data, knowledgeBase);
        List<ImmutablePair<Instance, FuzzyRuleType>> weightedRuleList = enrichRuleListWithWeight(candidateRuleList, methodConfig.getAndOperator().get(), methodConfig.getThenOperator().get());
        List<FuzzyRuleType> prunedRuleList = prune(weightedRuleList);
        RuleBaseType ruleBase = RuleBaseTrainerUtils.buildRuleBase(prunedRuleList);
        return new ImmutablePair<>(knowledgeBase, ruleBase);
    }


    private List<ImmutablePair<Instance, FuzzyRuleType>> enrichRuleListWithWeight(List<ImmutablePair<Instance, FuzzyRuleType>> candidateRuleList, AndOperator andOperator, ThenOperator implicationOperator) {
        List<ImmutablePair<Instance, FuzzyRuleType>> ruleList = new ArrayList<>();
        candidateRuleList
                .forEach(instanceAndRule -> {
                    Instance instance = instanceAndRule.getLeft();
                    FuzzyRuleType rule = instanceAndRule.getRight();
                    rule.setWeight(RuleBaseTrainerUtils.computeWeight(instance, rule, andOperator, implicationOperator));
                    ruleList.add(instanceAndRule);
                });
        return candidateRuleList;
    }

    private List<FuzzyRuleType> prune(List<ImmutablePair<Instance, FuzzyRuleType>> weightedRuleSet) {
        return weightedRuleSet.stream()
                .map(ImmutablePair::getRight)
                .collect(Collectors.groupingBy(RuleBaseTrainerUtils::getAntecedentClausesAsString))
                .values().stream()
                .map(this::getMaxWeightRule)
                .collect(Collectors.toList());
    }

    private FuzzyRuleType getMaxWeightRule(List<FuzzyRuleType> weightedRuleList) {
        return Utils.argMax(weightedRuleList, FuzzyRuleType::getWeight);
    }
}
