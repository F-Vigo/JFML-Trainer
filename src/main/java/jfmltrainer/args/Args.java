package jfmltrainer.args;

import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.task.Task;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORSearchMethod;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class Args {

    Optional<Task> task;
    Optional<RuleBaseTrainerMethod> method;

    Optional<String> dataPath;
    Optional<String> knowledgeBasePath;
    Optional<String> ruleBasePath;
    Optional<String> newKnowledgeBasePath;
    Optional<String> newRuleBasePath;

    Optional<String> variableDefinitionDataPath;
    Optional<List<Integer>> granularityList;
    Optional<Integer> singleGranularity;

    Optional<String> frbsPath;
    Optional<String> newFrbsPath;

    Optional<AndOperator> andOperator;
    Optional<OrOperator> orOperator;
    Optional<ThenOperator> thenOperator;

    Optional<String> outputFolder;
    Optional<String> outputName;

    Optional<Integer> seed;

    Optional<RVFOperator> rvfOperator;
    Optional<CORSearchMethod> corSearchMethod;
    Optional<Integer> maxIter;
    Optional<Integer> populationSize;
    Optional<Float> mutationProb;
    Optional<Float> growProportion;
    Optional<Integer> surplus;
    Optional<Integer> bitsgene;
    Optional<Boolean> isGlobal;
    Optional<Boolean> isWithoutSelection;

}
