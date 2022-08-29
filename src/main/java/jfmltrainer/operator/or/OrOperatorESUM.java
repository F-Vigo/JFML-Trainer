package jfmltrainer.operator.or;

public class OrOperatorESUM extends OrOperator{

    private static OrOperatorESUM instance = new OrOperatorESUM();

    private OrOperatorESUM(){}

    public static OrOperatorESUM getInstance() {
        return instance;
    }

    @Override
    public Float apply(Float x, Float y) {
        return (x+y)/(1+x*y);
    }

    @Override
    public String getName() {
        return "ESUM";
    }
}
