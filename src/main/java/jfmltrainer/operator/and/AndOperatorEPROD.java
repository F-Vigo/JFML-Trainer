package jfmltrainer.operator.and;

public class AndOperatorEPROD extends AndOperator {

    private static AndOperatorEPROD instance = new AndOperatorEPROD();

    private AndOperatorEPROD(){}

    public static AndOperatorEPROD getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return x*y/(2-(x+y-x*y));
    }

    @Override
    public String getName() {
        return "EPROD";
    }
}
