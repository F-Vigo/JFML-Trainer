package jfmltrainer.operator.then;

public class ThenOperatorMIN extends ThenOperator {

    private static ThenOperatorMIN instance = new ThenOperatorMIN();

    private ThenOperatorMIN(){}

    public static ThenOperatorMIN getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return Math.min(x,y);
    }

    @Override
    public String getName() {
        return "MIN";
    }
}
