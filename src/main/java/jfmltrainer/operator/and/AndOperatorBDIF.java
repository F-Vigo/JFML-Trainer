package jfmltrainer.operator.and;

public class AndOperatorBDIF extends AndOperator {

    private static AndOperatorBDIF instance = new AndOperatorBDIF();

    private AndOperatorBDIF(){}

    public static AndOperatorBDIF getInstance() {
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
