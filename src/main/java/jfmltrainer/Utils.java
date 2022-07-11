package jfmltrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTerm;
import jfml.term.FuzzyTermType;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.method.Problem;
import jfmltrainer.operator.OperatorParser;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

    public static Float computeAccuracy(List<List<String>> realValueList, List<List<String>> predictedValueList) {
        int n = realValueList.size();
        return IntStream.range(0, n).boxed()
                .map(i -> realValueList.get(i).equals(predictedValueList.get(i)))
                .map(equals -> equals ? 1 : 0)
                .reduce(Integer::sum)
                .get() / (float) n;
    }

    public interface TriFunction<A,B,C,D> {
        D apply(A a, B b, C c);
    }

    public static <T> T argMax(List<T> list, ToDoubleFunction<T> toDouble) {

        double maxValue = list.stream()
                .mapToDouble(toDouble)
                .max()
                .getAsDouble();

        return list.stream()
                .filter(x -> toDouble.applyAsDouble(x) == maxValue)
                .findAny()
                .get();
    }

    public static List<Float> getVariableByIndex(int variableIndex, List<List<Float>> instanceRealValueList) {
        return instanceRealValueList.stream()
                .map(instanceValueList -> instanceValueList.get(variableIndex))
                .collect(Collectors.toList());
    }

    public static Float computeMSE(List<List<Float>> realValueList, List<List<Float>> predictedValueList) {

        TriFunction<List<Float>, Float, Float, List<Float>> normalize = (valueList, mean, std) -> valueList.stream()
                .map(value -> (value - mean) / std)
                .collect(Collectors.toList());

        Function<
                Function<List<Float>, Float>,
                List<Float>
                > reduceData = f -> IntStream.range(0, realValueList.get(0).size())
                .mapToObj(variableIndex -> Utils.getVariableByIndex(variableIndex, realValueList))
                .map(f)
                .collect(Collectors.toList());

        List<Float> meanList = reduceData.apply(Utils::computeMean);
        List<Float> stdList = reduceData.apply(Utils::computeSTD);


        Function<List<List<Float>>, List<List<Float>>> normalizeValueList = valueList -> IntStream.range(0, valueList.get(0).size())
                .mapToObj(variableIndex -> normalize.apply(
                        Utils.getVariableByIndex(variableIndex, valueList), meanList.get(variableIndex), stdList.get(variableIndex))
                )
                .collect(Collectors.toList());


        List<List<Float>> normalizedRealValueList = normalizeValueList.apply(realValueList);
        List<List<Float>> normalizedPredictedValueList = normalizeValueList.apply(predictedValueList);

        List<Float> individualMSEList = IntStream.range(0, normalizedRealValueList.get(0).size())
                .mapToObj(variableIndex -> computeIndividualMSE(
                        getVariableByIndex(variableIndex, normalizedRealValueList),
                        getVariableByIndex(variableIndex, normalizedPredictedValueList)
                ))
                .collect(Collectors.toList());

        // return computeMean(individualMSEList); // TODO - Discuss
        return computeSquaredNorm(individualMSEList);
    }

    public static KnowledgeBaseType buildApproximativeKnowledgeBase(KnowledgeBaseType oldKnowledgeBase, RuleBaseType ruleBase) {
        oldKnowledgeBase.getKnowledgeBaseVariables().forEach(variable -> updateTerms(variable, ruleBase));
        return oldKnowledgeBase;
    }

    public static <T> ImmutablePair<List<T>, List<T>> twoPointCrossover(List<T> list1, List<T> list2) {
        Integer n = list1.size();
        List<T> child1 = new ArrayList<>(n);
        List<T> child2 = new ArrayList<>(n);

        Integer pos1 = (int) (Math.random() * n);
        Integer pos2 = (int) (Math.random() * n);

        Integer posL = Math.min(pos1, pos2);
        Integer posR = Math.max(pos1, pos2);

        for (int i = 0; i < posL; i++) {
            child1.set(i, list1.get(i));
            child2.set(i, list2.get(i));
        }
        for (int i = posL; i < posR; i++) {
            list1.set(i, child2.get(i));
            list2.set(i, child1.get(i));
        }
        for (int i = posR; i < n; i++) {
            child1.set(i, list1.get(i));
            child2.set(i, list2.get(i));
        }

        return new ImmutablePair<>(child1, child2);
    }

    private static void updateTerms(KnowledgeBaseVariable variable, RuleBaseType ruleBase) {

        List<FuzzyTermType> termList = (List<FuzzyTermType>) variable.getTerms();
        termList.clear();

        List<FuzzyTermType> newTermList = ruleBase.getRules().stream()
                .map(rule -> rule.getAntecedent().getClauses().stream()
                        .filter(clause -> ((FuzzyVariableType) clause.getVariable()).getName().equals(variable.getName()))
                        .findFirst().get()
                )
                .map(clause -> (FuzzyTermType) clause.getTerm()).collect(Collectors.toList());

        termList.addAll(newTermList);
    }

    private static Float computeSquaredNorm(List<Float> valueList) {
        return valueList.stream().map(x -> x*x).reduce(Float::sum).get();
    }

    public static Float computeMean(List<Float> valueList) {
        return valueList.stream().reduce(Float::sum).get() / valueList.size();
    }

    public static Float computeSTD(List<Float> valueList) {
        return computeSTD(valueList, computeMean(valueList));
    }

    public static Float computeSTD(List<Float> valueList, Float mean) {
        float variance = valueList.stream()
                .map(x -> x*x)
                .reduce(Float::sum).get() / valueList.size() - mean*mean;
        return (float) Math.sqrt(variance);
    }

    public static Float defuzzify(FuzzyTerm term) {
        return defuzzify(term, term.getXValuesDefuzzifier());
    }

    public static Float defuzzify(FuzzyTerm term, List<Float> membershipFunctionXValues) { // TODO - Check

        Float x0 = membershipFunctionXValues.get(0);
        Float xN = membershipFunctionXValues.get(2);
        List<Float> discretization = evenlySpacedDiscretization(x0, xN, 1999); // 1999 intervals for 2000 points

        float sum = 0, weightedSum = 0;
        for (Float x : discretization){
            Float y = term.getMembershipValue(x);
            sum += y;
            weightedSum += x * y;
        }
        return weightedSum / sum;
    }

    private static List<Float> evenlySpacedDiscretization(Float x0, Float xN, int N) {
        Float h = xN-x0/N;
        return IntStream.range(0, N+1)
                .mapToObj(i -> x0+i*h)
                .collect(Collectors.toList());
    }

    private static Float computeIndividualMSE(List<Float> realValueList, List<Float> predictedValueList) {
        return IntStream.range(0, realValueList.size())
                .mapToObj(i -> {
                    Float dif = realValueList.get(i) - predictedValueList.get(i);
                    return dif*dif;
                })
                .reduce(Float::sum).get() / realValueList.size();
    }

    public static <T> List<T> predict(Instance<T> instance, RuleBaseType ruleBase) {
        FuzzyRuleType bestRule = argMax(
                ruleBase.getRules(),
                rule -> getMatchingDegree(instance, rule)*rule.getWeight()
        );
        if (instance.getProblem().equals(Problem.CLASSIFICATION)) {
            return bestRule.getConsequent().getThen().getClause().stream()
                    .map(clause -> (T) ((FuzzyTermType) clause.getTerm()).getName())
                    .collect(Collectors.toList());
        }
        else {
            return bestRule.getConsequent().getThen().getClause().stream()
                    .map(clause -> (T) defuzzify((FuzzyTermType) clause.getTerm()))
                    .collect(Collectors.toList());
        }
    }

    private static Float getMatchingDegree(Instance instance, FuzzyRuleType rule) {
        int antecedentSize = instance.getAntecedentValueList().size();
        List<Float> mfValueList = IntStream.range(0, antecedentSize).boxed()
                .map(i -> ((FuzzyTermType) rule.getAntecedent().getClauses().get(i).getTerm()).getMembershipValue((Float) instance.getAntecedentValueList().get(i)))
                .collect(Collectors.toList());

        return mfValueList.stream()
                .reduce(OperatorParser.getAndOperator(Optional.ofNullable(rule.getAndMethod()), null)::apply)
                .get();
    }

}
