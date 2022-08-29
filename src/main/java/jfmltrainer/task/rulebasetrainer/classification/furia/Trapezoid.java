package jfmltrainer.task.rulebasetrainer.classification.furia;

import lombok.Value;

import java.util.Optional;

@Value
public class Trapezoid {
    Optional<Float> supportL;
    Optional<Float> coreL;
    Optional<Float> coreR;
    Optional<Float> supportR;

    public Float getMembershipValue(Float x) {

        if (supportL.isPresent() && x <= supportL.get()) {
            return 0F;

        } else if (coreL.isPresent() && x <= coreL.get()) {
            return (x - supportL.get()) / (coreL.get() - supportL.get());

        } else if (coreR.isPresent() && x <= coreR.get()) {
            return 1F;

        } else if (supportR.isPresent() && x <= supportR.get()) {
            return (coreR.get() - x) / (coreR.get() - supportR.get());

        } else if (supportR.isPresent()) {
            return 0F;

        } else {
            return 1F;
        }
    }
}
