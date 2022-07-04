package jfmltrainer.operator.and;

import jfmltrainer.operator.Operator;

import java.util.Optional;


public abstract class AndOperator implements Operator {

    public abstract Float apply(Float x, Float y);

    @Override
    public Optional<jfmltrainer.operator.and.AndOperator> fromString(String string) {
        switch (string) {
            case "MIN": // Minimum
                return Optional.of(new AndOperatorMIN());
            case "PROD": // Product
                return Optional.of(new AndOperatorPROD());
            case "BDIF": // Bounded difference, or Lukasiewicz T-norm
                return Optional.of(new AndOperatorBDIF());
            case "DRP": // Drastic product, or drastic T-norm
                return Optional.of(new AndOperatorDRP());
            case "EPROD": // Einstein product
                return Optional.of(new AndOperatorEPROD());
            case "HPROD": // Hamacher product
                return Optional.of(new AndOperatorHPROD());
            case "NILMIN": // Nilpotent minimum
                return Optional.of(new AndOperatorNILMIN());
            default:
                return Optional.empty();
        }
    }
}
