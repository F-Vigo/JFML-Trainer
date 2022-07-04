package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule;

import lombok.Value;

@Value
public class CrispClause {
    String varName;
    CrispClauseCateg type;
    Float value;
}
