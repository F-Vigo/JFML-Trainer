package jfmltrainer.operator.or;

import jfmltrainer.operator.Operator;

import java.util.Optional;

public abstract class OrOperator implements Operator {

    public abstract Float apply(Float x, Float y);

    @Override
    public Optional<OrOperator> fromString(String string) {
        switch (string) {
            case "MAX": // Maximum
                return Optional.of(new OrOperatorMAX());
            case "PROBOR": // Probabilistical sum
                return Optional.of(new OrOperatorPROBOR());
            case "BSUM": // Bounded sum
                return Optional.of(new OrOperatorBSUM());
            case "DRS": // Drastic sum, or drastic T-conorm
                return Optional.of(new OrOperatorDRS());
            case "ESUM": // Einstein sum
                return Optional.of(new OrOperatorESUM());
            case "HSUM": // Hamacher sum
                return Optional.of(new OrOperatorHSUM());
            case "NILMAX": // Nilpotent maximum
                return Optional.of(new OrOperatorNILMAX());
            default:
                return Optional.empty();
        }
    }
}
