package jfmltrainer.args;

import jfmltrainer.Task;
import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.trainer.rulebasetrainer.regression.cor.CORSearchMethod;
import lombok.Value;

import java.util.Optional;

@Value
public class Args {

    Optional<Task> task;
    Optional<RuleBaseTrainerMethod> method;

    Optional<String> dataPath;
    Optional<String> knowledgeBasePath;

    Optional<AndOperator> andOperator;
    Optional<OrOperator> orOperator;
    Optional<ThenOperator> thenOperator;

    Optional<RVFOperator> rvfOperator;
    Optional<CORSearchMethod> corSearchMethod;

    Optional<String> outputFolder;
    Optional<String> outputName;

}
