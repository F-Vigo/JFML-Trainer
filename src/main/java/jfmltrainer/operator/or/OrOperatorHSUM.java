package jfmltrainer.operator.or;

public class OrOperatorHSUM extends OrOperator {

    private static OrOperatorHSUM instance = new OrOperatorHSUM();

    private OrOperatorHSUM(){}

    public static OrOperatorHSUM getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return (x+y-2*x*y)/(1-x*y);
    }

    @Override
    public String getName() {
        return "HSUM";
    }
}
