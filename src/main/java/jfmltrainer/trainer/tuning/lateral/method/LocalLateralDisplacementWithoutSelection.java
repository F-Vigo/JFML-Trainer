package jfmltrainer.trainer.tuning.lateral.method;

import jfmltrainer.trainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.trainer.tuning.lateral.approach.local.LocalApproach;
import jfmltrainer.trainer.tuning.lateral.selectionvariant.LateralDisplacementWithoutSelection;

public class LocalLateralDisplacementWithoutSelection extends LateralDisplacement {
    public LocalLateralDisplacementWithoutSelection() {
        this.lateralApproach = new LocalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithoutSelection(lateralApproach.getEvaluator());
    }
}
