package jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter;

import jfml.knowledgebase.KnowledgeBaseType;
import lombok.Data;

import java.util.List;

@Data
public abstract class Parameter {

    protected static Integer UNIVERSE_TO_EPSILON_RATIO = 1000;

    protected Float epsilon;
    protected Float value;
    protected Float minPossibleValue;
    protected Float maxPossibleValue;

    public abstract Boolean tryToSetValue(Float value);
    public abstract Boolean tryToSetValue(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase);

    public abstract Boolean isCompatibleWithOtherParams(Float value, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase);
}
