package jfmltrainer.task.rulebasetrainer.tuning.lateral;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.tuning.Tuner;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.method.GlobalLateralDisplacementWithSelection;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.method.GlobalLateralDisplacementWithoutSelection;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.method.LocalLateralDisplacementWithSelection;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.method.LocalLateralDisplacementWithoutSelection;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class LateralDisplacementTuner extends Tuner {

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> tuneFRBS(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        LateralDisplacement lateralDisplacement = null;



        if (methodConfig.getIsGlobal()) {
            if (methodConfig.getIsWithoutSelection()) {
                lateralDisplacement = new GlobalLateralDisplacementWithoutSelection();
            } else {
                lateralDisplacement = new GlobalLateralDisplacementWithSelection();
            }
        } else {
            if (methodConfig.getIsWithoutSelection()) {
                lateralDisplacement = new LocalLateralDisplacementWithoutSelection();
            } else {
                lateralDisplacement = new LocalLateralDisplacementWithSelection();
            }
        }
        return lateralDisplacement.tune(data, knowledgeBase, ruleBase, methodConfig);
    }
}
