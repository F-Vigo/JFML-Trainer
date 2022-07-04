package jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnfisSecondLayer {
    public List<List<Float>> run(List<Float> input, List<List<ImmutablePair<Float, Float>>> secondLayerWeights) {
        return IntStream.range(0, input.size()).boxed()
                .map(i -> runByVariable(input.get(i), secondLayerWeights.get(i)))
                .collect(Collectors.toList());
    }

    private List<Float> runByVariable(Float input, List<ImmutablePair<Float, Float>> weightList) {
        return IntStream.range(0, weightList.size()).boxed()
                .map(j -> runByTerm(input, weightList.get(j)))
                .collect(Collectors.toList());
    }

    private Float runByTerm(Float input, ImmutablePair<Float, Float> weightPair) {
        Float num = (input - weightPair.getRight()) * (input - weightPair.getRight());
        Float denom = 2 * weightPair.getLeft() * weightPair.getLeft();
        return (float) Math.exp(-(num / denom));
    }
}
