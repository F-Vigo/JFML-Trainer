package jfmltrainer.operator.then;

public class ThenOperatorDRP extends ThenOperator {
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
