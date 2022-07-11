package jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers;

import java.util.List;

public class AnfisSixthLayer { // Summation
    public Float run(List<Float> output) { // TODO
        return output.stream().reduce(Float::sum).get();
    }
}
