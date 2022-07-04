package jfmltrainer.operator.and;

public class AndOperatorHPROD extends AndOperator {

    @Override
    public Float apply(Float x, Float y) {
        return (x+y)/(x+y-x*y);
    }

    @Override
    public String getName() {
        return "HPROD";
    }
}
