package jfmltrainer.operator.then;

import jfmltrainer.operator.Operator;

import java.util.Optional;

public abstract class ThenOperator implements Operator {

    public abstract Float apply(Float x, Float y);

    @Override
    public Optional<ThenOperator> fromString(String string) {
        switch (string) {
            case "MIN": // Minimum
                return Optional.of(new ThenOperatorMIN());
            case "PROD": // Product
                return Optional.of(new ThenOperatorPROD());
            case "BDIF": // Bounded difference, or Lukasiewicz T-norm
                return Optional.of(new ThenOperatorBDIF());
            case "DRP": // Drastic product, or drastic T-norm
                return Optional.of(new ThenOperatorDRP());
            case "EPROD": // Einstein product
                return Optional.of(new ThenOperatorEPROD());
            case "HPROD": // Hamacher product
                return Optional.of(new ThenOperatorHPROD());
            case "NILMIN": // Nilpotent minimum
                return Optional.of(new ThenOperatorNILMIN());
            default:
                return Optional.empty();
        }
    }
}
