package jfmltrainer.task.rulebasetrainer.tuning;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;

public abstract class Tuner {

    public void tune(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig, String outputFolder, String outputName) {
        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = tuneFRBS(data, knowledgeBase, ruleBase, methodConfig);
        fuzzySystem.getRight().setAndMethod(methodConfig.getAndOperator().getName());
        fuzzySystem.getRight().setOrMethod(methodConfig.getOrOperator().getName());
        fuzzySystem.getRight().setActivationMethod(methodConfig.getThenOperator().getName());
        Utils.exportFuzzySystem(fuzzySystem, outputFolder, outputName);
    }

    public abstract ImmutablePair<KnowledgeBaseType, RuleBaseType> tuneFRBS(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig);
}
