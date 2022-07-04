package jfmltrainer.operator;

import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorMIN;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.or.OrOperatorMAX;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.rvf.RVFOperatorMAX;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorMIN;

import java.util.Optional;

public class OperatorParser {

    public static AndOperator getAndOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getDefaultAndOperator(), new AndOperatorMIN(), "AND");
    }

    public static OrOperator getOrOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getDefaultOrOperator(), new OrOperatorMAX(), "OR");
    }

    public static ThenOperator getThenOperator(Optional<String> name, RuleBaseTrainerMethod method) {
        return getOperator(name, method, method.getThenOperator(), new ThenOperatorMIN(), "THEN");
    }

    public static RVFOperator getRVFOperator(Optional<String> name) {
        return getOperator(name, RuleBaseTrainerMethod.CORDON_HERRERA, Optional.of(new RVFOperatorMAX()), null, "RVF");
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
}
