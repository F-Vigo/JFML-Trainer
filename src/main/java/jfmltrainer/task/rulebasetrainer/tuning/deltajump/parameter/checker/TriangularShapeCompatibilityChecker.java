package jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter.checker;

import java.util.List;

public class TriangularShapeCompatibilityChecker implements ParameterCompatibilityChecker {
    @Override
    public Boolean checkCompatibility(List<Float> termParamList, Float value, Integer paramIndex) {
        switch (paramIndex) {
            case 0:
                return value <= termParamList.get(0) && termParamList.get(0) <= termParamList.get(1);
            case 1:
                return termParamList.get(0) <= value && value <= termParamList.get(1);
            case 2:
                return termParamList.get(0) <= termParamList.get(1) && termParamList.get(1) <= value;
            default:
                return null;
        }
    }
}
