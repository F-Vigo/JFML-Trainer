package jfmltrainer.operator.rvf;

import jfmltrainer.operator.Operator;

import java.util.List;

public abstract class RVFOperator implements Operator {

    public abstract Float apply(List<Float> values);
}
