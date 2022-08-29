package jfmltrainer.task.rulebasetrainer.classification.chi;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.classification.ClassificationTrainer;
import jfmltrainer.task.rulebasetrainer.regression.ch.CordonHerrera;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ChiCHBased extends ClassificationTrainer {
    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        return new CordonHerrera().trainRuleBase(data, knowledgeBase, methodConfig);
    }
}
