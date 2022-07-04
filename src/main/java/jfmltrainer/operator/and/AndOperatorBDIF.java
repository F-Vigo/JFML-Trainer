package jfmltrainer.operator.and;

public class AndOperatorBDIF extends AndOperator {

    @Override
    public Float apply(Float x, Float y) {
        return Math.max(0, x+y-1);
    }

    @Override
    public String getName() {
        return "BDIF";
    }
}
