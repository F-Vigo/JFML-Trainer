package jfmltrainer.operator.rvf;

import jfmltrainer.operator.OperatorParser;

import java.util.Optional;

public class RVFOperatorParser extends OperatorParser<RVFOperator> {

    private static RVFOperatorParser instance = new RVFOperatorParser();

    private RVFOperatorParser(){}

    public static RVFOperatorParser getInstance() {
        return instance;
    }

    @Override
    public Optional<RVFOperator> fromString(String name) {
        switch (name) {
            case "MAX": // Maximum
                return Optional.of(RVFOperatorMAX.getInstance());
            case "MEAN": // Mean
                return Optional.of(RVFOperatorMEAN.getInstance());
            case "PROD": // Product
                return Optional.of(RVFOperatorPROD.getInstance());
            default:
                return Optional.empty();
        }
    }
}
