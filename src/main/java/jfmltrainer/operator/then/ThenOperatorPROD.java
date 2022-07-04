package jfmltrainer.operator.then;

public class ThenOperatorPROD extends ThenOperator {

    @Override
    public Float apply(Float x, Float y) {
        return x*y;
    }

    @Override
    public String getName() {
        return "PROD";
    }
}
