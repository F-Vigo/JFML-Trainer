package jfmltrainer.task.knowledgebasebuilder.variabledefinition;

import lombok.Getter;

@Getter
public abstract class VariableDefinitionData {
    protected String name;
    protected Boolean isInput;
    protected Boolean isQuantitative;
}
