package jfmltrainer.operator.or;

import jfmltrainer.operator.OperatorParser;

import java.util.Optional;

public class OrOperatorParser extends OperatorParser<OrOperator> {

    private static OrOperatorParser instance = new OrOperatorParser();

    private OrOperatorParser(){}

    public static OrOperatorParser getInstance() {
        return instance;
    }

    @Override
    public Optional<OrOperator> fromString(String name) {
        switch (name) {
            case "MAX": // Maximum
                return Optional.of(OrOperatorMAX.getInstance());
            case "PROBOR": // Probabilistical sum
                return Optional.of(OrOperatorPROBOR.getInstance());
            case "BSUM": // Bounded sum
                return Optional.of(OrOperatorBSUM.getInstance());
            case "DRS": // Drastic sum, or drastic T-conorm
                return Optional.of(OrOperatorDRS.getInstance());
            case "ESUM": // Einstein sum
                return Optional.of(OrOperatorESUM.getInstance());
            case "HSUM": // Hamacher sum
                return Optional.of(OrOperatorHSUM.getInstance());
            case "NILMAX": // Nilpotent maximum
                return Optional.of(OrOperatorNILMAX.getInstance());
            default:
                return Optional.empty();
        }
    }
}
