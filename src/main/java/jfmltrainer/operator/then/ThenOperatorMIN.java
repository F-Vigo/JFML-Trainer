package jfmltrainer.operator.then;

public class ThenOperatorMIN extends ThenOperator {
    @Override
    public Float apply(Float x, Float y) {
        return Math.min(x,y);
    }

    @Override
    public String getName() {
        return "MIN";
    }
}
