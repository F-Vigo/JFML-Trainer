package jfmltrainer.trainer.rulebasetrainer.regression.anfis;

import lombok.Value;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

@Value
public class Weights {
    List<List<ImmutablePair<Float, Float>>> secondLayerWeights;
    List<List<Float>> fifthLayerWeights;
}
