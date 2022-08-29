package jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.term.FuzzyTermType;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter.checker.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MFParameter extends Parameter {

    Integer varIndex;
    Integer termIndex;
    Integer paramIndex;

    public MFParameter(KnowledgeBaseType knowledgeBase, Integer varIndex, Integer termIndex, Integer paramIndex) {

        this.varIndex = varIndex;
        this.termIndex = termIndex;
        this.paramIndex = paramIndex;

        this.minPossibleValue = ((FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(varIndex)).getDomainleft();
        this.maxPossibleValue = ((FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(varIndex)).getDomainright();
        this.value = ((FuzzyTermType) knowledgeBase.getKnowledgeBaseVariables().get(varIndex).getTerms().get(termIndex)).getParam()[paramIndex];
        this.epsilon = (getMaxPossibleValue() - getMinPossibleValue()) / UNIVERSE_TO_EPSILON_RATIO;
    }


    @Override
    public Boolean tryToSetValue(Float value) {
        return null;
    }

    @Override
    public Boolean tryToSetValue(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase) {

        if (value < minPossibleValue) {
            return false;
        } else if (value > maxPossibleValue) {
            return false;
        }

        Boolean canBeSet = isCompatibleWithOtherParams(value, parameterList, knowledgeBase);
        if (canBeSet) {
            setValue(value);
        }
        return canBeSet;
    }

    @Override
    public Boolean isCompatibleWithOtherParams(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase) {
        List<Float> parametersOfSameTermList = parameterList.stream()
                .filter(parameter -> parameter instanceof MFParameter)
                .map(parameter -> (MFParameter) parameter)
                .filter(this::isSameTerm)
                .map(mfParameter -> mfParameter.getValue())
                .collect(Collectors.toList());
        return isCompatibleWithOtherTermParams(value, parametersOfSameTermList, knowledgeBase);
    }

    private boolean isSameTerm(MFParameter that) {
        return (getVarIndex() == that.getVarIndex()) && (getTermIndex() == that.getTermIndex());
    }


    private Boolean isCompatibleWithOtherTermParams(Float value, List<Float> parameterTermList, KnowledgeBaseType knowledgeBase) {

        FuzzyTermType term = (FuzzyTermType) knowledgeBase.getKnowledgeBaseVariables().get(varIndex).getTerms().get(termIndex);

        if (term.getRightLinearShape() != null) {
            return new RightLinearShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getLeftGaussianShape() != null) {
            return new LeftLinearShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getPiShape() != null) {
            return new PiShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getTriangularShape() != null) {
            return new TriangularShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getGaussianShape() != null) {
            return new GaussianShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getRightGaussianShape() != null) {
            return new RightGaussianShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getLeftGaussianShape() != null) {
            return new LeftGaussianShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getTrapezoidShape() != null) {
            return new TrapezoidShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getSingletonShape() != null) {
            return new SingletonShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getRectangularShape() != null) {
            return new RectangularShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getZShape() != null) {
            return new ZShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getSShape() != null) {
            return new SShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getPointSetShape() != null) {
            return new PointSetShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else if (term.getCircularDefinition() != null) {
            return new CircularShapeCompatibilityChecker().checkCompatibility(parameterTermList, value, paramIndex);
        } else {
            return true;
        }
    }
}
