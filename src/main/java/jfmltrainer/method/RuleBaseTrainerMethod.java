package jfmltrainer.method;

import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorPROD;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorPROD;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum RuleBaseTrainerMethod { // TODO - This may need extension (like for RVF).

    // REGRESSION
    WANG_MENDEL(Problem.REGRESSION, new AndOperatorPROD(), null, new ThenOperatorPROD()),
    CORDON_HERRERA(Problem.REGRESSION, new AndOperatorPROD(), null, new ThenOperatorPROD()),
    COR_WEIGHTS(Problem.REGRESSION, null, null, null),
    THRIFT(Problem.REGRESSION, null, null, null),
    ANFIS(Problem.REGRESSION, null, null, null),

    // CLASSIFICATION
    CHI_WM(Problem.CLASSIFICATION, null, null, null),
    CHI_CH(Problem.CLASSIFICATION, null, null, null),
    FURIA(Problem.CLASSIFICATION, null, null, null),

    // TUNING
    GA_THREE_POINTS(Problem.TUNING, null, null, null),
    LAT(Problem.TUNING, null, null, null),
    DELTA_JUMP(Problem.TUNING, null, null, null);

    private Problem problem;
    private Optional<AndOperator> defaultAndOperator;
    private Optional<OrOperator> defaultOrOperator;
    private Optional<ThenOperator> thenOperator;

    RuleBaseTrainerMethod(
            Problem problem,
            AndOperator andOperator,
            OrOperator orOperator,
            ThenOperator thenOperator
    ) {
        this.problem = problem;
        this.defaultAndOperator = Optional.ofNullable(andOperator);
        this.defaultOrOperator = Optional.ofNullable(orOperator);
        this.thenOperator = Optional.ofNullable(thenOperator);
    }
}
