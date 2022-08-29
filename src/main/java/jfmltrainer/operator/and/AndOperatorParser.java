package jfmltrainer.operator.and;

import jfmltrainer.operator.OperatorParser;

import java.util.Optional;

public class AndOperatorParser extends OperatorParser<AndOperator> {

    private static AndOperatorParser instance = new AndOperatorParser();

    private AndOperatorParser(){}

    public static AndOperatorParser getInstance() {
        return instance;
    }

    @Override
    public Optional<AndOperator> fromString(String name) {
        switch (name) {
            case "MIN": // Minimum
                return Optional.of(AndOperatorMIN.getInstance());
            case "PROD": // Product
                return Optional.of(AndOperatorPROD.getInstance());
            case "BDIF": // Bounded difference, or Lukasiewicz T-norm
                return Optional.of(AndOperatorBDIF.getInstance());
            case "DRP": // Drastic product, or drastic T-norm
                return Optional.of(AndOperatorDRP.getInstance());
            case "EPROD": // Einstein product
                return Optional.of(AndOperatorEPROD.getInstance());
            case "HPROD": // Hamacher product
                return Optional.of(AndOperatorHPROD.getInstance());
            case "NILMIN": // Nilpotent minimum
                return Optional.of(AndOperatorNILMIN.getInstance());
            default:
                return Optional.empty();
        }
    }

}
