package jfmltrainer.task.rulebasetrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.AntecedentType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.method.Problem;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.then.ThenOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RuleBaseTrainerUtils {

    public static FuzzyRuleType getBestRule(Instance instance, KnowledgeBaseType knowledgeBase) {
        List<Float> instanceValueList = getValueList(instance);
        List<FuzzyTermType> bestTermList = IntStream.range(0, instanceValueList.size())
                .mapToObj(i -> getBestFuzzyTerm(i, instanceValueList.get(i), knowledgeBase))
                .collect(Collectors.toList());
        if (instance.getProblem() == Problem.CLASSIFICATION) {
            bestTermList.addAll(getBestNominalTerms(instance, knowledgeBase));
        }
        return buildRuleFromTermList(bestTermList, knowledgeBase);
    }

    public static List<Float> getValueList(Instance<Float> instance) {
        List<Float> result = new ArrayList<>(instance.getAntecedentValueList());
        if (instance.getProblem() == Problem.REGRESSION) {
            result.addAll(instance.getConsequentValueList());
        }
        return result;
    }

    private static FuzzyTermType getBestFuzzyTerm(int i, Float value, KnowledgeBaseType knowledgeBase) {
        KnowledgeBaseVariable variable = knowledgeBase.getKnowledgeBaseVariables().get(i);
        List<FuzzyTermType> termList = (List<FuzzyTermType>) variable.getTerms();
        return Utils.argMax(termList, term -> term.getMembershipValue(value));
    }

    private static List<FuzzyTermType> getBestNominalTerms(Instance instance, KnowledgeBaseType knowledgeBase) {
        int nAntecedentVariables = instance.getAntecedentValueList().size();
        List<KnowledgeBaseVariable> outputVariables = knowledgeBase.getKnowledgeBaseVariables().subList(nAntecedentVariables, knowledgeBase.getKnowledgeBaseVariables().size());
        List<FuzzyTermType> termList = IntStream.range(0, outputVariables.size())
                .mapToObj(i -> getBestNominalSingleTerm(instance, i, outputVariables.get(i)))
                .collect(Collectors.toList());
        return termList;
    }

    private static FuzzyTermType getBestNominalSingleTerm(Instance instance, int outputVariableIndex, KnowledgeBaseVariable variable) {
        return ((List<FuzzyTermType>) variable.getTerms()).stream()
                .filter(term -> term.getName().equals(instance.getConsequentValueList().get(outputVariableIndex)))
                .findAny()
                .get();
    }

    public static FuzzyRuleType buildRuleFromTermList(List<FuzzyTermType> bestTermList, KnowledgeBaseType knowledgeBase) {

        List<KnowledgeBaseVariable> variableList = knowledgeBase.getKnowledgeBaseVariables();
        int nVariables = variableList.size();

        AntecedentType antecedent = new AntecedentType();
        IntStream.range(0, nVariables-1)
                .forEach(i -> antecedent.addClause(variableList.get(i), bestTermList.get(i)));

        ConsequentType consequent = new ConsequentType();
        consequent.addThenClause(variableList.get(nVariables-1), bestTermList.get(nVariables-1));

        return new FuzzyRuleType("", antecedent, consequent); // TODO (rule name)
    }

    public static Float computeWeight(Instance instance, FuzzyRuleType rule, AndOperator andOperator, ThenOperator thenOperator) {

        int antecedentSize = instance.getAntecedentValueList().size();

        List<FuzzyTermType> antecedentTermList = rule
                .getAntecedent()
                .getClauses().stream()
                .map(clause -> (FuzzyTermType) clause.getTerm())
                .collect(Collectors.toList());

        List<Float> antecedentDegrees = IntStream.range(0, antecedentSize)
                .mapToObj(i -> antecedentTermList.get(i).getMembershipValue((Float) instance.getAntecedentValueList().get(i)))
                .collect(Collectors.toList());

        Float antecedentWeight = antecedentDegrees.stream()
                .reduce(andOperator::apply)
                .get();

        Float weight = antecedentWeight;

        if (instance.getProblem() == Problem.REGRESSION) {

            Float consequentWeight = IntStream.range(0, instance.getConsequentValueList().size())
                    .mapToObj(i -> {
                        FuzzyTermType term = (FuzzyTermType) rule.getConsequent().getThen().getClause().get(i).getTerm();
                        Float value = ((RegressionInstance) instance).getConsequentValueList().get(i);
                        return term.getMembershipValue(value);
                    })
                    .reduce(andOperator::apply)
                    .get();

            weight = thenOperator.apply(antecedentWeight, consequentWeight);
        }
        return weight;
    }

    public static RuleBaseType buildRuleBase(List<FuzzyRuleType> prunedRuleList) {
        RuleBaseType ruleBase = new RuleBaseType();
        prunedRuleList.forEach(ruleBase::addRule);
        return ruleBase;
    }

    public static String getAntecedentClausesAsString(FuzzyRuleType rule) {
        return rule.getAntecedent().getClauses().toString();
    }

    private static Boolean equalsRule(FuzzyRuleType x, FuzzyRuleType y) {
        Function<FuzzyRuleType, String> getConsequentClauseAsString = rule -> rule.getConsequent().getThen().getClause().toString();
        Boolean sameAntecedent = getAntecedentClausesAsString(x).equals(getAntecedentClausesAsString(y));
        Boolean sameConsequent = getConsequentClauseAsString.apply(x).equals(getConsequentClauseAsString.apply(y));
        return sameAntecedent && sameConsequent;
    }

    public static List<Instance> getCoveredInstances(FuzzyRuleType rule, Data data, KnowledgeBaseType knowledgeBase) {
        return ((List<Instance>) data.getInstanceList()).stream()
                .filter(instance -> RuleBaseTrainerUtils.equalsRule(RuleBaseTrainerUtils.getBestRule(instance, knowledgeBase), rule))
                .collect(Collectors.toList());
    }
}
