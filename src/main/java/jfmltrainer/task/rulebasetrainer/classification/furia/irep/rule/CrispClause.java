package jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule;

import lombok.Value;

@Value
public class CrispClause {
    String varName;
    CrispClauseCateg type;
    Float value;
}
