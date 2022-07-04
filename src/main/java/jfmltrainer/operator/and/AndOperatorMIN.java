package jfmltrainer.operator.and;

public class AndOperatorMIN extends AndOperator{

    @Override
    public Float apply(Float x, Float y) {
        return Math.min(x,y);
    }

    @Override
    public String getName() {
        return "MIN";
    }
}
