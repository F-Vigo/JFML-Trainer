package jfmltrainer.task.rulebasetrainer.regression.thrift;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Evaluator {

    public double evaluate(Data data, List<List<FuzzyTermType>> splitTermList, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {

        List<FuzzyRuleType> ruleList = splitTermList.stream() // TODO
                .map(termList -> RuleBaseTrainerUtils.buildRuleFromTermList(termList, knowledgeBase))
                .collect(Collectors.toList());

        List<List<Float>> realValueList = (List<List<Float>>) data.getInstanceList().stream()
                .map(instance -> ((RegressionInstance) instance).getConsequentValueList())
                .collect(Collectors.toList());
        List<List<Float>> predictedValueList = getPredictedValueList(data, RuleBaseTrainerUtils.buildRuleBase(ruleList), methodConfig);

        // MSE is error and must be minimised, but fitness must be maximised;
        return -Utils.computeMSE(realValueList, predictedValueList);
    }

    private List<List<Float>> getPredictedValueList(Data data, RuleBaseType ruleBase, MethodConfig methodConfig) {
        return (List<List<Float>>) data.getInstanceList().stream()
                .map(instance -> predictInstance((RegressionInstance) instance, ruleBase, methodConfig))
                .collect(Collectors.toList());
    }

    private List<Float> predictInstance(RegressionInstance instance, RuleBaseType ruleBase, MethodConfig methodConfig) {
        Integer bestRulePos = Utils.argMax(
                IntStream.range(0, ruleBase.getRules().size()).boxed().collect(Collectors.toList()),
                i -> getMatchingDegree(instance, i, ruleBase, methodConfig)
        );
        return this.defuzzify(bestRulePos, ruleBase);
    }

    private Float getMatchingDegree(RegressionInstance instance, Integer rulePos, RuleBaseType ruleBase, MethodConfig methodConfig) {
        int antecedentSize = instance.getAntecedentValueList().size();
        List<Float> MFValueList = IntStream.range(0, antecedentSize).boxed()
                .map(i -> getMFTerm(instance, rulePos, ruleBase, i))
                .collect(Collectors.toList());
        AndOperator andOperator = methodConfig.getAndOperator();
        return MFValueList.stream()
                .reduce(andOperator::apply).get();
    }

    private Float getMFTerm(RegressionInstance instance, Integer rulePos, RuleBaseType ruleBase, Integer antecedentVarPos) {
        Float instanceValue = instance.getAntecedentValueList().get(antecedentVarPos);
        FuzzyTermType term = (FuzzyTermType) ruleBase.getRules().get(rulePos).getAntecedent().getClauses().get(antecedentVarPos).getTerm();
        return term.getMembershipValue(instanceValue);
    }

    private List<Float> defuzzify(Integer bestRulePos, RuleBaseType ruleBase) {
        FuzzyRuleType rule = ruleBase.getRules().get(bestRulePos);
        int consequentSize = rule.getConsequent().getThen().getClause().size();
        List<Float> defuzzifiedValueList = IntStream.range(0, consequentSize).boxed()
                .map(i -> defuzzifyValue(bestRulePos, ruleBase, i))
                .collect(Collectors.toList());
        return defuzzifiedValueList;
    }

    private Float defuzzifyValue(Integer bestRulePos, RuleBaseType ruleBase, Integer consequentVarPos) {
        FuzzyRuleType rule = ruleBase.getRules().get(bestRulePos);
        FuzzyTermType term = (FuzzyTermType) rule.getConsequent().getThen().getClause().get(consequentVarPos).getTerm();
        return Utils.defuzzify(term, term.getXValuesDefuzzifier());
    }
}
