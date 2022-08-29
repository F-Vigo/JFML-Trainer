package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorMAX extends RVFOperator {

    private static RVFOperatorMAX instance = new RVFOperatorMAX();

    private RVFOperatorMAX(){}

    public static RVFOperatorMAX getInstance() {
        return instance;
    }

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMaxRVF(values);
    }

    @Override
    public String getName() {
        return "MAX";
    }
}
