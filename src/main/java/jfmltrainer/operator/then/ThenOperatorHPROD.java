package jfmltrainer.operator.then;

public class ThenOperatorHPROD extends ThenOperator {

    private static ThenOperatorHPROD instance = new ThenOperatorHPROD();

    private ThenOperatorHPROD(){}

    public static ThenOperatorHPROD getInstance() {
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
