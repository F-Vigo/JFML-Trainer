package jfmltrainer.operator.rvf;

import jfmltrainer.operator.Operator;

import java.util.List;
import java.util.Optional;

public abstract class RVFOperator implements Operator {

    public abstract Float apply(List<Float> values);

    @Override
    public Optional<RVFOperator> fromString(String string) {
        switch (string) {
            case "MAX": // Maximum
                return Optional.of(new RVFOperatorMAX());
            case "MEAN": // Mean
                return Optional.of(new RVFOperatorMEAN());
            case "PROD": // Product
                return Optional.of(new RVFOperatorPROD());
            default:
                return Optional.empty();
        }
    }
}
