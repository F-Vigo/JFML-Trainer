package jfmltrainer.task.rulebasetrainer;

import jfmltrainer.args.Args;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorMIN;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.or.OrOperatorMAX;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.rvf.RVFOperatorMAX;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorMIN;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORSearchMethod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class MethodConfig {

    AndOperator andOperator;
    OrOperator orOperator;
    ThenOperator thenOperator;

    RVFOperator rvfOperator;

    CORSearchMethod corSearchMethod;

    Integer maxIter;
    Integer populationSize;
    Float mutationProb;

    Float growProportion;
    Integer surplus;

    Integer bitsgene;

    Boolean isGlobal;
    Boolean isWithoutSelection;

   // private static Integer MAX_ITER = 100; // TODO
   // private static Integer POPULATION_SIZE = 100; // TODO
   // private static Float MUTATION_PROB = 0.8F; // TODO

   // private static Float GROW_PROPORTION = 0.8F; // TODO
   // private static Integer SURPLUS = 64;

   // protected static Integer BITSGENE = 0; // TODO


    private static int DEFAULT_MAX_LINE_SEARCH_ITERATIONS = 20;


    public static MethodConfig fromArgs(Args args) {
        return new MethodConfig(
                args.getAndOperator().orElse(args.getMethod().get().getDefaultAndOperator().orElse(AndOperatorMIN.getInstance())),
                args.getOrOperator().orElse(args.getMethod().get().getDefaultOrOperator().orElse(OrOperatorMAX.getInstance())),
                args.getThenOperator().orElse(args.getMethod().get().getDefaultThenOperator().orElse(ThenOperatorMIN.getInstance())),
                args.getRvfOperator().orElse(RVFOperatorMAX.getInstance()),
                args.getCorSearchMethod().orElse(CORSearchMethod.EXPLICIT_ENUMERATION),
                args.getMaxIter().orElse(100),
                args.getPopulationSize().orElse(100),
                args.getMutationProb().orElse(0.8F),
                args.getGrowProportion().orElse(0.8F),
                args.getSurplus().orElse(64),
                args.getBitsgene().orElse(10),
                args.getIsGlobal().orElse(true),
                args.getIsWithoutSelection().orElse(true)
        );
    }
}
