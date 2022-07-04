package jfmltrainer.trainer;

import jfmltrainer.args.Args;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.trainer.rulebasetrainer.regression.cor.CORSearchMethod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class MethodConfig {

    Optional<AndOperator> andOperator;
    Optional<OrOperator> orOperator;
    Optional<ThenOperator> thenOperator;
    Optional<RVFOperator> rvfOperator;

    Optional<CORSearchMethod> corSearchMethod;

    public static MethodConfig fromArgs(Args args) {
        return new MethodConfig(
                args.getAndOperator(),
                args.getOrOperator(),
                args.getThenOperator(),
                args.getRvfOperator(),
                args.getCorSearchMethod()
        );
    }
}
