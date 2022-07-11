package jfmltrainer.trainer.tuning.deltajump.parameter.checker;

import java.util.List;

public interface ParameterCompatibilityChecker {

    Boolean checkCompatibility(List<Float> termParamList, Float value, Integer paramIndex);
}
