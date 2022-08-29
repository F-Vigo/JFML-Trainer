package jfmltrainer.task.knowledgebasebuilder.variabledefinition;

import lombok.Value;

@Value
public class QuantitativeVariableDefinitionData extends VariableDefinitionData {
    Float minValue;
    Float maxValue;
    Integer granularity;

    public QuantitativeVariableDefinitionData(String name, Boolean isInput, Float minValue, Float maxValue, Integer granularity) {
        this.name = name;
        this.isQuantitative = true;
        this.isInput = isInput;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.granularity = granularity;
    }
}
