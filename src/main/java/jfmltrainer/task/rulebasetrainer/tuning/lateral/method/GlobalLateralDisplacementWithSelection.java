package jfmltrainer.task.rulebasetrainer.tuning.lateral.method;

import jfmltrainer.task.rulebasetrainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.global.GlobalApproach;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant.LateralDisplacementWithSelection;

public class GlobalLateralDisplacementWithSelection extends LateralDisplacement {
    public GlobalLateralDisplacementWithSelection() {
        this.lateralApproach = new GlobalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithSelection(lateralApproach.getEvaluator());
    }
}
