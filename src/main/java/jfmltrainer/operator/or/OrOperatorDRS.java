package jfmltrainer.operator.or;

public class OrOperatorDRS extends OrOperator {

    private static OrOperatorDRS instance = new OrOperatorDRS();

    private OrOperatorDRS(){}

    public static OrOperatorDRS getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return x==0
                ? y
                : (y==0 ? x : 1);
    }

    @Override
    public String getName() {
        return "DRS";
    }
}
