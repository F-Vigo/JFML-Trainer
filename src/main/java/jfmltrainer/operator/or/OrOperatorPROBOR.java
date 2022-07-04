package jfmltrainer.operator.or;

public class OrOperatorPROBOR extends OrOperator {

    @Override
    public Float apply(Float x, Float y) {
        return x+y-x*y;
    }

    @Override
    public String getName() {
        return "PROBOR";
    }
}
