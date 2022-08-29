package jfmltrainer.operator.then;

public class ThenOperatorBDIF extends ThenOperator {

    private static ThenOperatorBDIF instance = new ThenOperatorBDIF();

    private ThenOperatorBDIF(){}

    public static ThenOperatorBDIF getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return Math.max(0, x+y-1);
    }

    @Override
    public String getName() {
        return "BDIF";
    }
}
