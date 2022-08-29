package jfmltrainer.task.rulebasetrainer.regression.anfis.layers;

import jfmltrainer.data.instance.RegressionInstance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnfisFifthLayer { // Affine function (times the normalized matching degree)

    public List<Float> run(List<Float> output, RegressionInstance instance, List<List<Float>> fifthLayerWeights) {
        return IntStream.range(0, output.size()).boxed()
                .map(i -> output.get(i) * applyTSKFunction(instance, fifthLayerWeights.get(i)))
                .collect(Collectors.toList());
    }

    private Float applyTSKFunction(RegressionInstance instance, List<Float> weightList) {
        Float dotProduct = IntStream.range(0, instance.getAntecedentValueList().size()).boxed()
                .map(i -> weightList.get(i+1) * instance.getAntecedentValueList().get(i))
                .reduce(Float::sum)
                .get();
        Float intercept = weightList.get(0);
        return dotProduct+intercept;
    }
}
