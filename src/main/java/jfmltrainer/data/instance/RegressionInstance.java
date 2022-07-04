package jfmltrainer.data.instance;

import jfmltrainer.method.Problem;
import lombok.Value;

import java.util.List;

@Value
public class RegressionInstance extends Instance<Float> {

    public RegressionInstance(List<Float> antecedentValueList, List<Float> consequentValueList) {
        this.problem = Problem.REGRESSION;
        this.antecedentValueList = antecedentValueList;
        this.consequentValueList = consequentValueList;
    }
}
