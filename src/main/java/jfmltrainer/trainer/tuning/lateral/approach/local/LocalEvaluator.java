package jfmltrainer.trainer.tuning.lateral.approach.local;


import jfmltrainer.trainer.tuning.lateral.Evaluator;

public class LocalEvaluator extends Evaluator {

    @Override
    protected Integer getInputGenePosition(Integer nVar, Integer rulePos, Integer varPos) {
        return nVar*rulePos + varPos;
    }

    @Override
    protected Integer getOutputGenePosition(Integer nVar, Integer bestRulePos, Integer antecedentSize, Integer consequentVarPos) {
        return nVar*bestRulePos + antecedentSize + consequentVarPos;
    }
}
