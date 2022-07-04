package jfmltrainer.operator.then;

public class ThenOperatorEPROD extends ThenOperator {
    @Override
    public Float apply(Float x, Float y) {
        return x*y/(2-(x+y-x*y));
    }

    @Override
    public String getName() {
        return "EPROD";
    }
}
