package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorMEAN extends RVFOperator {

    private static RVFOperatorMEAN instance = new RVFOperatorMEAN();

    private RVFOperatorMEAN(){}

    public static RVFOperatorMEAN getInstance() {
        return instance;
    }

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMeanRVF(values);
    }

    @Override
    public String getName() {
        return "MEAN";
    }
}
