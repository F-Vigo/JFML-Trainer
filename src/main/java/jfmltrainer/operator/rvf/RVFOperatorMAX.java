package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorMAX extends RVFOperator {

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMaxRVF(values);
    }

    @Override
    public String getName() {
        return "MAX";
    }
}
