package jfmltrainer.operator.or;

public class OrOperatorBSUM extends OrOperator {

    private static OrOperatorBSUM instance = new OrOperatorBSUM();

    private OrOperatorBSUM(){}

    public static OrOperatorBSUM getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return Math.min(1, x+y);
    }

    @Override
    public String getName() {
        return "BSUM";
    }
}
