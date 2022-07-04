package jfmltrainer.trainer.rulebasetrainer.classification.chi;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.classification.ClassificationTrainer;
import jfmltrainer.trainer.rulebasetrainer.regression.wm.WangMendel;
import org.apache.commons.lang3.tuple.ImmutablePair;


public class ChiWMBased extends ClassificationTrainer {
    @Override
    protected ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        return new WangMendel().trainRuleBase(data, knowledgeBase, methodConfig);
    }
}
