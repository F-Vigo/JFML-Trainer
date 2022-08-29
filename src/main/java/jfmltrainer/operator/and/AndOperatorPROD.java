package jfmltrainer.operator.and;

public class AndOperatorPROD extends AndOperator {

    private static AndOperatorPROD instance = new AndOperatorPROD();

    private AndOperatorPROD(){}

    public static AndOperatorPROD getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return x*y;
    }

    @Override
    public String getName() {
        return "PROD";
    }
}
