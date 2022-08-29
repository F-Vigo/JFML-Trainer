package jfmltrainer.operator.or;

public class OrOperatorPROBOR extends OrOperator {

    private static OrOperatorPROBOR instance = new OrOperatorPROBOR();

    private OrOperatorPROBOR(){}

    public static OrOperatorPROBOR getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return x+y-x*y;
    }

    @Override
    public String getName() {
        return "PROBOR";
    }
}
