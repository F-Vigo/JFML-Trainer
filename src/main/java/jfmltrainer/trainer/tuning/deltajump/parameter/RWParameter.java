package jfmltrainer.trainer.tuning.deltajump.parameter;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;

import java.util.List;

public class RWParameter extends Parameter {

    public RWParameter(Float ruleWeight) {
        this.epsilon = 0.01F;
        this.value = ruleWeight;
        this.minPossibleValue = 0F;
        this.maxPossibleValue = 1F;
    }

    @Override
    public Boolean tryToSetValue(Float value) {
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
        return null;
    }

    @Override
    public Boolean isCompatibleWithOtherParams(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase) {
        return true;
    }
}
