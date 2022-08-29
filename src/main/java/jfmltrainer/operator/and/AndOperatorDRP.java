package jfmltrainer.operator.and;

public class AndOperatorDRP extends AndOperator {

    private static AndOperatorDRP instance = new AndOperatorDRP();

    private AndOperatorDRP(){}

    public static AndOperatorDRP getInstance() {
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
