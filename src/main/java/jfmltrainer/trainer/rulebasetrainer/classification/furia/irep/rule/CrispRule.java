package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CrispRule {
    private List<CrispClause> antecedent;
    private String Consequent;
}
