package jfmltrainer.operator.then;

import jfmltrainer.operator.OperatorParser;

import java.util.Optional;

public class ThenOperatorParser extends OperatorParser<ThenOperator> {

    private static ThenOperatorParser instance = new ThenOperatorParser();

    private ThenOperatorParser(){}

    public static ThenOperatorParser getInstance() {
        return instance;
    }

    @Override
    public Optional<ThenOperator> fromString(String name) {
        switch (name) {
            case "MIN": // Minimum
                return Optional.of(ThenOperatorMIN.getInstance());
            case "PROD": // Product
                return Optional.of(ThenOperatorPROD.getInstance());
            case "BDIF": // Bounded difference, or Lukasiewicz T-norm
                return Optional.of(ThenOperatorBDIF.getInstance());
            case "DRP": // Drastic product, or drastic T-norm
                return Optional.of(ThenOperatorDRP.getInstance());
            case "EPROD": // Einstein product
                return Optional.of( ThenOperatorEPROD.getInstance());
            case "HPROD": // Hamacher product
                return Optional.of(ThenOperatorHPROD.getInstance());
            case "NILMIN": // Nilpotent minimum
                return Optional.of(ThenOperatorNILMIN.getInstance());
            default:
                return Optional.empty();
        }
    }
}
