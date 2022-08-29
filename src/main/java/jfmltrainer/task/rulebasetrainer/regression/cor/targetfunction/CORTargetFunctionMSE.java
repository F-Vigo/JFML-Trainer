package jfmltrainer.task.rulebasetrainer.regression.cor.targetfunction;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.term.FuzzyTerm;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CORTargetFunctionMSE implements CORTargetFunction {

    @Override
    public Float apply(FuzzyRuleType rule, Data data, KnowledgeBaseType knowledgeBase) {

        List<RegressionInstance> coveredInstanceList = RuleBaseTrainerUtils.getCoveredInstances(rule, data, knowledgeBase).stream()
                .map(instance -> (RegressionInstance) instance)
                .collect(Collectors.toList());

        List<List<Float>> realValueList = coveredInstanceList.stream()
                .map(RegressionInstance::getConsequentValueList)
                .collect(Collectors.toList());

        List<List<Float>> defuzzifiedPredictedValueList = coveredInstanceList.stream()
                .map(instance -> defuzzifyPrediction(rule))
                .collect(Collectors.toList());

        return Utils.computeMSE(realValueList, defuzzifiedPredictedValueList);
    }

    private List<Float> defuzzifyPrediction(FuzzyRuleType rule) {

        List<FuzzyTerm> termList = rule.getConsequent().getThen().getClause().stream()
                .map(clause -> (FuzzyTerm) clause.getTerm())
                .collect(Collectors.toList());

        return termList.stream()
                .map(Utils::defuzzify)
                .collect(Collectors.toList());
    }
}
