package jfmltrainer.task.metrics.computer;

import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.term.FuzzyTermType;
import jfmltrainer.task.metrics.measure.ClassificationMeasureValue;
import jfmltrainer.task.metrics.measure.ClassificationMeasures;
import jfmltrainer.task.metrics.measure.Measures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariableMetricsComputerClassification extends VariableMetricsComputer<String> {

    @Override
    protected Measures computeMeasures(List<String> realValueList, List<String> predictedValueList, KnowledgeBaseVariable variable) {
        List<String> valueList = variable.getTerms().stream()
                .map(term -> ((FuzzyTermType) term).getName())
                .collect(Collectors.toList());
        return new ClassificationMeasures(
                variable.getName(),
                computeMeasureValueList(realValueList, predictedValueList, valueList, this::computeAccuracy),
                computeMeasureValueList(realValueList, predictedValueList, valueList, this::computePrecision),
                computeMeasureValueList(realValueList, predictedValueList, valueList, this::computeSensitivity),
                computeMeasureValueList(realValueList, predictedValueList, valueList, this::computeSpecificity),
                computeMeasureValueList(realValueList, predictedValueList, valueList, this::computeF1)
        );
    }


    private List<ClassificationMeasureValue> computeMeasureValueList(
            List<String> realValueList,
            List<String> predictedValueList,
            List<String> valueList,
            BiFunction<List<Boolean>, List<Boolean>, Float> computation
    ) {

        Integer n = realValueList.size();
        List<ClassificationMeasureValue> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            String positiveLabel = valueList.get(i);
            List<Boolean> realPositiveList = realValueList.stream()
                    .map(label -> label == positiveLabel)
                    .collect(Collectors.toList());
            List<Boolean> predictedPositiveList = predictedValueList.stream()
                    .map(label -> label == positiveLabel)
                    .collect(Collectors.toList());

            result.add(new ClassificationMeasureValue(
                    positiveLabel,
                    computation.apply(realPositiveList, predictedPositiveList)
            ));
        }

        return result;
    }


    private Float computeAccuracy(List<Boolean> realPositiveList, List<Boolean> predictedPositiveList) {
        Integer n = realPositiveList.size();
        List<Integer> matchList = IntStream.range(0, n).boxed()
                .map(i -> realPositiveList.get(i) == predictedPositiveList.get(i) ? 1 : 0)
                .collect(Collectors.toList());
        return (float) matchList.stream().reduce(Integer::sum).get() / n;
    }

    private Float computePrecision(List<Boolean> realPositiveList, List<Boolean> predictedPositiveList) {
        List<Integer> matchList = IntStream.range(0, realPositiveList.size()).boxed()
                .filter(predictedPositiveList::get)
                .map(i -> realPositiveList.get(i) ? 1 : 0)
                .collect(Collectors.toList());
        return (float) matchList.stream().reduce(Integer::sum).get() / matchList.size();
    }

    private Float computeSensitivity(List<Boolean> realPositiveList, List<Boolean> predictedPositiveList) {
        List<Integer> matchList = IntStream.range(0, realPositiveList.size()).boxed()
                .filter(realPositiveList::get)
                .map(i -> predictedPositiveList.get(i) ? 1 : 0)
                .collect(Collectors.toList());
        return (float) matchList.stream().reduce(Integer::sum).get() / matchList.size();
    }

    private Float computeSpecificity(List<Boolean> realPositiveList, List<Boolean> predictedPositiveList) {
        List<Integer> matchList = IntStream.range(0, realPositiveList.size()).boxed()
                .filter(i -> !realPositiveList.get(i))
                .map(i -> predictedPositiveList.get(i) ? 0 : 1)
                .collect(Collectors.toList());
        return (float) matchList.stream().reduce(Integer::sum).get() / matchList.size();
    }

    private Float computeF1(List<Boolean> realPositiveList, List<Boolean> predictedPositiveList) {
        Float precision = computePrecision(realPositiveList, predictedPositiveList);
        Float sensitivity = computeSensitivity(realPositiveList, predictedPositiveList);
        return 2 * precision * sensitivity / (precision + sensitivity);
    }



}
