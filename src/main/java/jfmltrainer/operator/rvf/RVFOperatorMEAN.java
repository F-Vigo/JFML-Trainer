package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorMEAN extends RVFOperator {

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMeanRVF(values);
    }

    @Override
    public String getName() {
        return "MEAN";
    }
}
