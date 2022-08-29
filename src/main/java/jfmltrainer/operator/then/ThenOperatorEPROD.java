package jfmltrainer.operator.then;

public class ThenOperatorEPROD extends ThenOperator {

    private static ThenOperatorEPROD instance = new ThenOperatorEPROD();

    private ThenOperatorEPROD(){}

    public static ThenOperatorEPROD getInstance() {
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
