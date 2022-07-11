package jfmltrainer.trainer.tuning.lateral.approach.global;

import jfmltrainer.trainer.tuning.lateral.approach.Evaluator;

public class GlobalEvaluator extends Evaluator {

    @Override
    protected Integer getInputGenePosition(Integer nVar, Integer rulePos, Integer varPos) {
        return varPos;
    }

    @Override
    protected Integer getOutputGenePosition(Integer nVar, Integer bestRulePos, Integer antecedentSize, Integer consequentVarPos) {
        return antecedentSize + consequentVarPos;
    }


}
