package jfmltrainer.task.rulebasetrainer.tuning.lateral.method;

import jfmltrainer.task.rulebasetrainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.global.GlobalApproach;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant.LateralDisplacementWithoutSelection;

public class GlobalLateralDisplacementWithoutSelection extends LateralDisplacement {
    public GlobalLateralDisplacementWithoutSelection() {
        this.lateralApproach = new GlobalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithoutSelection(lateralApproach.getEvaluator());
    }



}
