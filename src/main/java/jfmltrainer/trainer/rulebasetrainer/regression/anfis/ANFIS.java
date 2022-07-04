package jfmltrainer.trainer.rulebasetrainer.regression.anfis;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.regression.RegressionTrainer;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ANFIS extends RegressionTrainer {

    private AnfisNN anfisNN = new AnfisNN();

    @Override
    protected ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        return anfisNN.run(data, knowledgeBase, methodConfig);
    }

}
