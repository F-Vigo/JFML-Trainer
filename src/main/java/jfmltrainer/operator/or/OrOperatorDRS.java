package jfmltrainer.operator.or;

public class OrOperatorDRS extends OrOperator {

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
