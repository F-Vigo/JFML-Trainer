package jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers;

import java.util.List;
import java.util.stream.Collectors;

public class AnfisFourthLayer { // Normalization

    public List<Float> run(List<Float> input) {
        return input.stream()
                .map(matchingDegree -> normalize(matchingDegree, input))
                .collect(Collectors.toList());
    }

    private Float normalize(Float matchingDegree, List<Float> input) {
        return matchingDegree / (input.stream().reduce(Float::sum)).get();
    }
}
