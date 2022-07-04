package jfmltrainer.operator.or;

public class OrOperatorMAX extends OrOperator {

    @Override
    public Float apply(Float x, Float y) {
        return Math.max(x,y);
    }

    @Override
    public String getName() {
        return "MAX";
    }
}
