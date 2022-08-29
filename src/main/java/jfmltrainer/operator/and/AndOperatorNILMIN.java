package jfmltrainer.operator.and;

public class AndOperatorNILMIN extends AndOperator {

    private static AndOperatorNILMIN instance = new AndOperatorNILMIN();

    private AndOperatorNILMIN(){}

    public static AndOperatorNILMIN getInstance() {
        return instance;
    }

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
