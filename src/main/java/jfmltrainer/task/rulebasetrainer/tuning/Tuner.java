package jfmltrainer.task.rulebasetrainer.tuning;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;

public abstract class Tuner {

    public abstract ImmutablePair<KnowledgeBaseType, RuleBaseType> tune(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig);
}
