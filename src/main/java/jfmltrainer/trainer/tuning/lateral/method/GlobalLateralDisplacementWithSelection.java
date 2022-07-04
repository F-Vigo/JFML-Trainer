package jfmltrainer.trainer.tuning.lateral.method;

import jfmltrainer.trainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.trainer.tuning.lateral.approach.global.GlobalApproach;
import jfmltrainer.trainer.tuning.lateral.selectionvariant.LateralDisplacementWithSelection;

public class GlobalLateralDisplacementWithSelection extends LateralDisplacement {
    public GlobalLateralDisplacementWithSelection() {
        this.lateralApproach = new GlobalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithSelection(lateralApproach.getEvaluator());
    }
}
