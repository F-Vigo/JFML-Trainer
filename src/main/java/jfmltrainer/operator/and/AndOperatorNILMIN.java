package jfmltrainer.operator.and;

public class AndOperatorNILMIN extends AndOperator {

    @Override
    public Float apply(Float x, Float y) {
        return (x+y)>1
                ? Math.min(x,y)
                : 0;
    }

    @Override
    public String getName() {
        return "NILMIN";
    }
}
