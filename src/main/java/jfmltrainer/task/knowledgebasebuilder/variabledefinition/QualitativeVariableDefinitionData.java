package jfmltrainer.task.knowledgebasebuilder.variabledefinition;

import lombok.Value;

import java.util.List;

@Value
public class QualitativeVariableDefinitionData extends VariableDefinitionData {
    List<String> labelList;

    public QualitativeVariableDefinitionData(String name, Boolean isInput, List<String> labelList) {
        this.name = name;
        this.isInput = isInput;
        this.isQuantitative = false;
        this.labelList = labelList;
    }
}
