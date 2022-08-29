package jfmltrainer.operator.or;

public class OrOperatorMAX extends OrOperator {

    private static OrOperatorMAX instance = new OrOperatorMAX();

    private OrOperatorMAX(){}

    public static OrOperatorMAX getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return Math.max(x,y);
    }

    @Override
    public String getName() {
        return "MAX";
    }
}
