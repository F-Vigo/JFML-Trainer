package jfmltrainer.operator.then;

public class ThenOperatorNILMIN extends ThenOperator {

    private static ThenOperatorNILMIN instance = new ThenOperatorNILMIN();

    private ThenOperatorNILMIN(){}

    public static ThenOperatorNILMIN getInstance() {
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
