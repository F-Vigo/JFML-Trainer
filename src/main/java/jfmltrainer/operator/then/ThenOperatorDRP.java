package jfmltrainer.operator.then;

public class ThenOperatorDRP extends ThenOperator {

    private static ThenOperatorDRP instance = new ThenOperatorDRP();

    private ThenOperatorDRP(){}

    public static ThenOperatorDRP getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return x==1
                ? y
                : (y==1 ? x : 0);
    }

    @Override
    public String getName() {
        return "DRP";
    }
}
