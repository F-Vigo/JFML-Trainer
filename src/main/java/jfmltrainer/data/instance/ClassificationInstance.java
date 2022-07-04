package jfmltrainer.data.instance;

import jfmltrainer.method.Problem;

import java.util.List;

public class ClassificationInstance extends Instance<String> {

    public ClassificationInstance(List<Float> antecedentValueList, List<String> consequentValueList) {
        this.problem = Problem.CLASSIFICATION;
        this.antecedentValueList = antecedentValueList;
        this.consequentValueList = consequentValueList;
    }
}
