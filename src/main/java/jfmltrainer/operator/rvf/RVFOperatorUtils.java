package jfmltrainer.operator.rvf;

import java.util.List;

public class RVFOperatorUtils {

    static Float getMaxRVF(List<Float> values) {
        return values.stream().reduce(Math::max).get();
    }

    static Float getMeanRVF(List<Float> values) {
        Float num = values.stream().reduce(Float::sum).get();
        Float den = ((Integer) values.size()).floatValue();
        return num/den;
    }
}
