package jfmltrainer.task.rulebasetrainer.regression.wm;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.stream.Collectors;

public class WangMendelUtils {
    public static List<ImmutablePair<Instance, FuzzyRuleType>> generateWMCandidateRuleList(Data data, KnowledgeBaseType knowledgeBase) {
        return ((List<Instance>) data.getInstanceList())
                .stream()
                .map(instance -> new ImmutablePair<>(instance, RuleBaseTrainerUtils.getBestRule(instance, knowledgeBase)))
                .collect(Collectors.toList());
    }
}
