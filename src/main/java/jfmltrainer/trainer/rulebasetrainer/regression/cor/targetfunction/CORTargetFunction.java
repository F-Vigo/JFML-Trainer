package jfmltrainer.trainer.rulebasetrainer.regression.cor.targetfunction;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfmltrainer.data.Data;

public interface CORTargetFunction {
    Float apply(FuzzyRuleType rule, Data data, KnowledgeBaseType knowledgeBase);
}
