package jfmltrainer.task.rulebasetrainer.regression.anfis.layers;

import jfmltrainer.data.instance.RegressionInstance;

import java.util.List;

public class AnfisFirstLayer { // Data values retrieving

    public List<Float> run(RegressionInstance instance) {
        return instance.getAntecedentValueList();
    }
}
