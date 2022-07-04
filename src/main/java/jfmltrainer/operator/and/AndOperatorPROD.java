package jfmltrainer.operator.and;

public class AndOperatorPROD extends AndOperator {

    @Override
    public Float apply(Float x, Float y) {
        return x*y;
    }

    @Override
    public String getName() {
        return "PROD";
    }
}
