package jfmltrainer.task.rulebasetrainer.tuning.lateral.method;

import jfmltrainer.task.rulebasetrainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.local.LocalApproach;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant.LateralDisplacementWithoutSelection;

public class LocalLateralDisplacementWithoutSelection extends LateralDisplacement {
    public LocalLateralDisplacementWithoutSelection() {
        this.lateralApproach = new LocalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithoutSelection(lateralApproach.getEvaluator());
    }
}
