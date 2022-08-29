package jfmltrainer.operator.and;

public class AndOperatorHPROD extends AndOperator {

    private static AndOperatorHPROD instance = new AndOperatorHPROD();

    private AndOperatorHPROD(){}

    public static AndOperatorHPROD getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return (x+y)/(x+y-x*y);
    }

    @Override
    public String getName() {
        return "HPROD";
    }
}
