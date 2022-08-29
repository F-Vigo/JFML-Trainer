package jfmltrainer.operator.then;

public class ThenOperatorPROD extends ThenOperator {

    private static ThenOperatorPROD instance = new ThenOperatorPROD();

    private ThenOperatorPROD(){}

    public static ThenOperatorPROD getInstance() {
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
