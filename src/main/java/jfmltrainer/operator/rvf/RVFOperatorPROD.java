package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorPROD extends RVFOperator {

    private static RVFOperatorPROD instance = new RVFOperatorPROD();

    private RVFOperatorPROD(){}

    public static RVFOperatorPROD getInstance() {
        return instance;
    }

    @Override
    public Float apply(List<Float> values) {
        return RVFOperatorUtils.getMaxRVF(values) * RVFOperatorUtils.getMeanRVF(values);
    }

    @Override
    public String getName() {
        return "PROD";
    }
}
