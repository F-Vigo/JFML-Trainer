package jfmltrainer.task.rulebasetrainer.tuning.lateral.method;

import jfmltrainer.task.rulebasetrainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.approach.local.LocalApproach;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.selectionvariant.LateralDisplacementWithSelection;

public class LocalLateralDisplacementWithSelection extends LateralDisplacement {
    public LocalLateralDisplacementWithSelection() {
        this.lateralApproach = new LocalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithSelection(lateralApproach.getEvaluator());
    }
}
