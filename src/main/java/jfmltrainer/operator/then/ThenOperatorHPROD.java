package jfmltrainer.operator.then;

public class ThenOperatorHPROD extends ThenOperator {
    @Override
    public Float apply(Float x, Float y) {
        return (x+y)/(x+y-x*y);
    }

    @Override
    public String getName() {
        return "HPROD";
    }
}
