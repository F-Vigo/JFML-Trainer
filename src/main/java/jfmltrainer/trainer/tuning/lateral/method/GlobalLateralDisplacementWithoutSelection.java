package jfmltrainer.trainer.tuning.lateral.method;

import jfmltrainer.trainer.tuning.lateral.LateralDisplacement;
import jfmltrainer.trainer.tuning.lateral.approach.global.GlobalApproach;
import jfmltrainer.trainer.tuning.lateral.selectionvariant.LateralDisplacementWithoutSelection;

public class GlobalLateralDisplacementWithoutSelection extends LateralDisplacement {

    public GlobalLateralDisplacementWithoutSelection() {
        this.lateralApproach = new GlobalApproach();
        this.lateralSelectionVariant = new LateralDisplacementWithoutSelection(lateralApproach.getEvaluator());
    }



}
