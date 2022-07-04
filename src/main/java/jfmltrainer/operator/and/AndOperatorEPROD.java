package jfmltrainer.operator.and;

public class AndOperatorEPROD extends AndOperator  {

    @Override
    public Float apply(Float x, Float y) {
        return x*y/(2-(x+y-x*y));
    }

    @Override
    public String getName() {
        return "EPROD";
    }
}
