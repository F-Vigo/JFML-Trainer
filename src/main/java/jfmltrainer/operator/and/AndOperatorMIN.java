package jfmltrainer.operator.and;

public class AndOperatorMIN extends AndOperator {

    private static AndOperatorMIN instance = new AndOperatorMIN();

    private AndOperatorMIN(){}

    public static AndOperatorMIN getInstance() {
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
