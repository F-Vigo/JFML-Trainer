package jfmltrainer.operator;

import java.util.Optional;

public abstract class OperatorParser<T> {

    public abstract <T extends Operator> Optional<T> fromString(String name);

    /*
    public static AndOperator getAndOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getDefaultAndOperator(), AndOperatorMIN.getInstance(), "AND");
    }

    public static OrOperator getOrOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getDefaultOrOperator(), OrOperatorMAX.getInstance(), "OR");
    }

    public static ThenOperator getThenOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getThenOperator(), ThenOperatorMIN.getInstance(), "THEN");
    }

    public static RVFOperator getRVFOperator(Optional<String> name) {
        return getOperator(name, RuleBaseTrainerMethod.CORDON_HERRERA, Optional.of(RVFOperatorMAX.getInstance()), null, "RVF");
    }

    private static <T extends Operator> T getOperator(Optional<String> name, RuleBaseTrainerMethod method, Optional<T> methodDefaultOperator, T defaultOperator, String operatorType) {
        if (name.isEmpty()) {
            System.out.println(String.format("%s operator not provided.", operatorType));
            if (methodDefaultOperator.isPresent()) {
                System.out.println(String.format("Using %s as %s default.", method.getDefaultAndOperator().get(), method.toString()));
                return methodDefaultOperator.get();
            } else {
                System.out.println(String.format("Using %s as default.", defaultOperator.toString()));
                return defaultOperator;
            }
        } else {
            Optional<T> operator = defaultOperator.fromString(name.get());
            if (operator.isPresent()) {
                return operator.get();
            } else {
                System.out.println(String.format("ERROR: Wrong %s operator provided.", operatorType));
                return null;
            }
        }
    }

     */
}
