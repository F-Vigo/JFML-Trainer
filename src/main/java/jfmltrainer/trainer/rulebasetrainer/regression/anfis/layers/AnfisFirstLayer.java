package jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers;

import jfmltrainer.data.instance.RegressionInstance;

import java.util.List;

public class AnfisFirstLayer {

    public List<Float> run(RegressionInstance instance) {
        return instance.getAntecedentValueList();
    }
}
