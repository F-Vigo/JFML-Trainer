package jfmltrainer.task.metrics.computer;

import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfmltrainer.aux.Utils;
import jfmltrainer.task.metrics.measure.Measures;
import jfmltrainer.task.metrics.measure.RegressionMeasures;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariableMetricsComputerRegression extends VariableMetricsComputer<Float> {

    @Override
    protected Measures computeMeasures(List<Float> realValueList, List<Float> predictedValueList, KnowledgeBaseVariable variable) {
        Float mse = Utils.computeMSE(
                realValueList.stream()
                        .map(Collections::singletonList)
                        .collect(Collectors.toList()),
                predictedValueList.stream()
                        .map(Collections::singletonList)
                        .collect(Collectors.toList())
        );
        Float rmse = (float) Math.sqrt(mse);
        Float mae = computeMAE(realValueList, predictedValueList);
        Float gmse = computeGMSE(realValueList, predictedValueList);
        return new RegressionMeasures(variable.getName(), mse, rmse, mae, gmse);
    }

    private Float computeMAE(List<Float> realValueList, List<Float> predictedValueList) {
        Integer n = realValueList.size();
        List<Float> absErrorList = IntStream.range(0, n).boxed()
                .map(i -> Math.abs(realValueList.get(i) - predictedValueList.get(i)))
                .collect(Collectors.toList());
        return absErrorList.stream().reduce(Float::sum).get() / n;
    }

    private Float computeGMSE(List<Float> realValueList, List<Float> predictedValueList) {
        Integer n = realValueList.size();
        List<Float> squaredList = IntStream.range(0, n).boxed()
                .map(i -> Math.abs(realValueList.get(i) - predictedValueList.get(i)))
                .map(error -> error*error)
                .collect(Collectors.toList());
        Float reduced = squaredList.stream().reduce((x,y) -> x*y).get();
        return (float) Math.pow(reduced, 1 / (float) n);
    }
}
