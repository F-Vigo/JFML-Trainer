package jfmltrainer.operator.or;

public class OrOperatorESUM extends OrOperator{

    @Override
    public Float apply(Float x, Float y) {
        return (x+y)/(1+x*y);
    }

    @Override
    public String getName() {
        return "ESUM";
    }
}
