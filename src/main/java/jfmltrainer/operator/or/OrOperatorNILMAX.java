package jfmltrainer.operator.or;

public class OrOperatorNILMAX extends OrOperator {

    private static OrOperatorNILMAX instance = new OrOperatorNILMAX();

    private OrOperatorNILMAX(){}

    public static OrOperatorNILMAX getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return (x+y<1)
                ? Math.max(x,y)
                : 1;
    }

    @Override
    public String getName() {
        return "NILMAX";
    }
}
