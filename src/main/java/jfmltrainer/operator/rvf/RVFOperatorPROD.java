package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorPROD extends RVFOperator {

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMaxRVF(values) * RVFOperatorUtils.getMeanRVF(values);
    }

    @Override
    public String getName() {
        return "PROD";
    }
}
