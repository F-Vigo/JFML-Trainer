package jfmltrainer.trainer.tuning.lateral.approach;

import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.tuning.lateral.Chromosome;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Evaluator {

    public double evaluate(Data data, RuleBaseType ruleBase, Chromosome chromosome, MethodConfig methodConfig) {
        List<List<Float>> realValueList = (List<List<Float>>) data.getInstanceList().stream()
                .map(instance -> ((RegressionInstance) instance).getConsequentValueList())
                .collect(Collectors.toList());
        List<List<Float>> predictedValueList = getPredictedValueList(data, ruleBase, chromosome, methodConfig);

        // MSE is error and must be minimised, but fitness must be maximised;
        return -Utils.computeMSE(realValueList, predictedValueList);
    }

    protected List<List<Float>> getPredictedValueList(Data data, RuleBaseType ruleBase, Chromosome chromosome, MethodConfig methodConfig) {
        return (List<List<Float>>) data.getInstanceList().stream()
                .map(instance -> predictInstance((RegressionInstance) instance, ruleBase, chromosome, methodConfig))
                .collect(Collectors.toList());
    }

    private List<Float> predictInstance(RegressionInstance instance, RuleBaseType ruleBase, Chromosome chromosome, MethodConfig methodConfig) {
        List<FuzzyRuleType> actualRuleList = IntStream.range(0, ruleBase.getRules().size()).boxed()
                .filter(i -> chromosome.getSelectedRuleList().get(i))
                .map(i -> ruleBase.getRules().get(i))
                .collect(Collectors.toList());
        Integer bestRulePos = Utils.argMax(
                IntStream.range(0,actualRuleList.size()).boxed().collect(Collectors.toList()),
                i -> chromosome.getSelectedRuleList().get(i) ? getMatchingDegree(instance, i, ruleBase, chromosome, methodConfig) : -1
        );
        return defuzzify(bestRulePos, ruleBase, chromosome);
    }

    private Float getMatchingDegree(RegressionInstance instance, Integer rulePos, RuleBaseType ruleBase, Chromosome chromosome, MethodConfig methodConfig) {
        int antecedentSize = instance.getAntecedentValueList().size();
        List<Float> MFValueList = IntStream.range(0, antecedentSize)
                .boxed()
                .map(i -> getMFTerm(instance, rulePos, ruleBase, chromosome, i))
                .collect(Collectors.toList());
        AndOperator andOperator = methodConfig.getAndOperator().get();
        return MFValueList.stream()
                .reduce(andOperator::apply).get();
    }

    private Float getMFTerm(RegressionInstance instance, Integer rulePos, RuleBaseType ruleBase, Chromosome chromosome, Integer antecedentVarPos) {
        Integer nVar = instance.getAntecedentValueList().size() + instance.getConsequentValueList().size();
        Float instanceValue = instance.getAntecedentValueList().get(antecedentVarPos);
        FuzzyTermType term = (FuzzyTermType) ruleBase.getRules().get(rulePos).getAntecedent().getClauses().get(antecedentVarPos).getTerm();
        Float displacement = chromosome.getGeneList().get(getInputGenePosition(nVar, rulePos, antecedentVarPos));
        return term.getMembershipValue(instanceValue-displacement);
    }

    private List<Float> defuzzify(Integer bestRulePos, RuleBaseType ruleBase, Chromosome chromosome) {
        FuzzyRuleType rule = ruleBase.getRules().get(bestRulePos);
        int antecedentSize = rule.getAntecedent().getClauses().size();
        int consequentSize = rule.getConsequent().getThen().getClause().size();
        List<Float> defuzzifiedValueList = IntStream.range(0, consequentSize).boxed()
                .map(i -> defuzzifyValue(bestRulePos, ruleBase, chromosome, i, antecedentSize))
                .collect(Collectors.toList());
        return defuzzifiedValueList;
    }

    private Float defuzzifyValue(Integer bestRulePos, RuleBaseType ruleBase, Chromosome chromosome, Integer consequentVarPos, Integer antecedentSize) {
        FuzzyRuleType rule = ruleBase.getRules().get(bestRulePos);
        Integer nVar = antecedentSize + rule.getConsequent().getThen().getClause().size();
        Float displacement = chromosome.getGeneList().get(getOutputGenePosition(nVar, bestRulePos, antecedentSize, consequentVarPos));
        FuzzyTermType term = (FuzzyTermType) rule.getConsequent().getThen().getClause().get(consequentVarPos).getTerm();

        List<Float> membershipFunctionXValues = term.getXValuesDefuzzifier().stream()
                .map(x -> x-displacement)
                .collect(Collectors.toList());

        return Utils.defuzzify(term, membershipFunctionXValues);
    }



    protected abstract Integer getInputGenePosition(Integer nVar, Integer rulePos, Integer varPos);
    protected abstract Integer getOutputGenePosition(Integer nVar, Integer bestRulePos, Integer antecedentSize, Integer consequentVarPos);
}
