package jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter;

import jfml.knowledgebase.KnowledgeBaseType;

import java.util.List;

public class RWParameter extends Parameter {

    public RWParameter(Float ruleWeight) {
        this.epsilon = 0.01F;
        this.value = ruleWeight;
        this.minPossibleValue = 0F;
        this.maxPossibleValue = 1F;
    }

    //@Override
    private Boolean tryToSetValue(Float value) {
        Boolean hasFallenIn = false;

        if (value < minPossibleValue) {
            this.value = minPossibleValue;
        } else if (value > maxPossibleValue) {
            this.value = maxPossibleValue;
        } else {
            hasFallenIn = true;
        }
        return hasFallenIn;
    }

    @Override
    public Boolean tryToSetValue(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase) {
        return tryToSetValue(value);
    }

    @Override
    public Boolean isCompatibleWithOtherParams(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase) {
        return true;
    }
}
