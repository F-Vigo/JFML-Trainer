package jfmltrainer.method;

import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorMIN;
import jfmltrainer.operator.and.AndOperatorPROD;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorMIN;
import jfmltrainer.operator.then.ThenOperatorPROD;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainer;
import jfmltrainer.task.rulebasetrainer.classification.chi.ChiCHBased;
import jfmltrainer.task.rulebasetrainer.classification.chi.ChiWMBased;
import jfmltrainer.task.rulebasetrainer.classification.furia.FURIA;
import jfmltrainer.task.rulebasetrainer.regression.anfis.ANFIS;
import jfmltrainer.task.rulebasetrainer.regression.ch.CordonHerrera;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORWM;
import jfmltrainer.task.rulebasetrainer.regression.thrift.Thrift;
import jfmltrainer.task.rulebasetrainer.regression.wm.WangMendel;
import jfmltrainer.task.rulebasetrainer.tuning.Tuner;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.DeltaJump;
import jfmltrainer.task.rulebasetrainer.tuning.lateral.LateralDisplacementTuner;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum RuleBaseTrainerMethod {

    // REGRESSION
    WANG_MENDEL("WM", Problem.REGRESSION, new WangMendel(), null, AndOperatorPROD.getInstance(), null, ThenOperatorPROD.getInstance()),
    CORDON_HERRERA("CH", Problem.REGRESSION, new CordonHerrera(), null, AndOperatorMIN.getInstance(), null, ThenOperatorMIN.getInstance()),
    COR_WEIGHTS("COR", Problem.REGRESSION, new CORWM(), null, null, null, ThenOperatorMIN.getInstance()),
    THRIFT("Thrift", Problem.REGRESSION, new Thrift(), null, null, null, null),
    ANFIS("ANFIS", Problem.REGRESSION, new ANFIS(), null,null, null, null),

    // CLASSIFICATION
    CHI_WM("CHI_WM", Problem.CLASSIFICATION, new ChiWMBased(), null, null, null, null),
    CHI_CH("CHI_CH", Problem.CLASSIFICATION, new ChiCHBased(), null, null, null, null),
    FURIA("FURIA", Problem.CLASSIFICATION, new FURIA(), null, AndOperatorPROD.getInstance(), null, ThenOperatorPROD.getInstance()),

    // TUNING
    // GA_THREE_POINTS("GTP", Problem.TUNING, null, null, null, null, null), // TODO - Remove
    LAT("LAT", Problem.TUNING, null, new LateralDisplacementTuner(), AndOperatorMIN.getInstance(), null, ThenOperatorMIN.getInstance()),
    DELTA_JUMP("DJ", Problem.TUNING, null, new DeltaJump(), null, null, null);

    private String name;
    private Problem problem;
    private RuleBaseTrainer trainer;
    private Tuner tuner;
    private Optional<AndOperator> defaultAndOperator;
    private Optional<OrOperator> defaultOrOperator;
    private Optional<ThenOperator> defaultThenOperator;

    RuleBaseTrainerMethod(
            String name,
            Problem problem,
            RuleBaseTrainer trainer,
            Tuner tuner,
            AndOperator andOperator,
            OrOperator orOperator,
            ThenOperator thenOperator
    ) {
        this.name = name;
        this.problem = problem;
        this.trainer = trainer;
        this.tuner = tuner;
        this.defaultAndOperator = Optional.ofNullable(andOperator);
        this.defaultOrOperator = Optional.ofNullable(orOperator);
        this.defaultThenOperator = Optional.ofNullable(thenOperator);
    }

    public static Optional<RuleBaseTrainerMethod> fromString(String name) {
        return Stream.of(RuleBaseTrainerMethod.values())
                .filter(method -> method.getName().equals(name))
                .findAny();
    }
}
