package jfmltrainer.args;

import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.OperatorParser;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorParser;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.or.OrOperatorParser;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.rvf.RVFOperatorParser;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorParser;
import jfmltrainer.task.Task;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORSearchMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgsParser {

    public static Args parse(String[] args) {

        List<ImmutablePair<String, String>> argPairList = splitArgsInPairs(args);


        Optional<Task> task = Optional.empty();
        Optional<RuleBaseTrainerMethod> method = Optional.empty();

        Optional<String> dataPath = Optional.empty();
        Optional<String> knowledgeBasePath = Optional.empty();
        Optional<String> ruleBasePath = Optional.empty();
        Optional<String> newKnowledgeBasePath = Optional.empty();
        Optional<String> newRuleBasePath = Optional.empty();

        Optional<String> variableDefinitionDataPath = Optional.empty();
        Optional<List<Integer>> granularityList = Optional.empty();
        Optional<Integer> singleGranularity = Optional.empty();

        Optional<String> frbsPath = Optional.empty();
        Optional<String> newFrbsPath = Optional.empty();

        Optional<AndOperator> andOperator = Optional.empty();
        Optional<OrOperator> orOperator = Optional.empty();
        Optional<ThenOperator> thenOperator = Optional.empty();

        Optional<String> outputFolder = Optional.empty();
        Optional<String> outputName = Optional.empty();

        Optional<Integer> seed = Optional.empty();;

        Optional<RVFOperator> rvfOperator = Optional.empty();
        Optional<CORSearchMethod> corSearchMethod = Optional.empty();
        Optional<Integer> maxIter = Optional.empty();
        Optional<Integer> populationSize = Optional.empty();
        Optional<Float> mutationProb = Optional.empty();
        Optional<Float> growProportion = Optional.empty();
        Optional<Integer> surplus = Optional.empty();
        Optional<Integer> bitsgene = Optional.empty();
        Optional<Boolean> isGlobal = Optional.empty();
        Optional<Boolean> isWithoutSelection = Optional.empty();


        for(ImmutablePair<String, String> tagAndValue: argPairList) {
            String argLabel = tagAndValue.getLeft().substring(1);
            String argVal = tagAndValue.getRight();
            switch (argLabel) {
                case "t":
                    task = Task.fromString(argVal);
                    break;
                case "m":
                    method = RuleBaseTrainerMethod.fromString(argVal);
                    break;
                case "d":
                    dataPath = Optional.of(argVal);
                    break;
                case "kb":
                    knowledgeBasePath = Optional.of(argVal);
                    break;
                case "rb":
                    ruleBasePath = Optional.of(argVal);
                    break;
                case "nkb":
                    newKnowledgeBasePath = Optional.of(argVal);
                    break;
                case "nrb":
                    newRuleBasePath = Optional.of(argVal);
                    break;
                case "vdd":
                    variableDefinitionDataPath = Optional.of(argVal);
                    break;
                case "gl":
                    granularityList = buildGranularityList(argVal);
                    break;
                case "sg":
                    singleGranularity = Optional.of(Integer.parseInt(argVal));
                    break;
                case "frbs":
                    frbsPath = Optional.of(argVal);
                    break;
                case "nfrbs":
                    newFrbsPath = Optional.of(argVal);
                    break;
                case "ao":
                    andOperator = AndOperatorParser.getInstance().fromString(argVal);
                    break;
                case "oo":
                    orOperator = OrOperatorParser.getInstance().fromString(argVal);
                    break;
                case "to":
                    thenOperator = ThenOperatorParser.getInstance().fromString(argVal);
                    break;
                case "of":
                    outputFolder = Optional.of(argVal);
                    break;
                case "on":
                    outputName = Optional.of(argVal);
                    break;
                case "s":
                    seed = Optional.of(Integer.parseInt(argVal));
                case "rvf":
                    rvfOperator = RVFOperatorParser.getInstance().fromString(argVal);
                    break;
                case "cs":
                    corSearchMethod = CORSearchMethod.fromString(argVal);
                    break;
                case "mi":
                    maxIter = Optional.of(Integer.parseInt(argVal));
                    break;
                case "ps":
                    populationSize = Optional.of(Integer.parseInt(argVal));
                    break;
                case "mp":
                    mutationProb = Optional.of(Float.parseFloat(argVal));
                    break;
                case "gp":
                    growProportion = Optional.of(Float.parseFloat(argVal));
                    break;
                case "sp":
                    surplus = Optional.of(Integer.parseInt(argVal));
                    break;
                case "bi":
                    bitsgene = Optional.of(Integer.parseInt(argVal));
                    break;
                case "glo":
                    isGlobal = Optional.of(Integer.parseInt(argVal) == 1);
                    break;
                case "wou":
                    isWithoutSelection = Optional.of(Integer.parseInt(argVal) == 1);
                    break;
                default:
                    break;
            }
        }


        return new Args(

                task,
                method,

                dataPath,
                knowledgeBasePath,
                ruleBasePath,
                newKnowledgeBasePath,
                newRuleBasePath,

                variableDefinitionDataPath,
                granularityList,
                singleGranularity,

                frbsPath,
                newFrbsPath,

                andOperator,
                orOperator,
                thenOperator,

                outputFolder,
                outputName,

                seed,

                rvfOperator,
                corSearchMethod,
                maxIter,
                populationSize,
                mutationProb,
                growProportion,
                surplus,
                bitsgene,
                isGlobal,
                isWithoutSelection
        );
    }



    private static List<ImmutablePair<String, String>> splitArgsInPairs(String[] args) {

        String lastTag = null;
        ImmutablePair<String, String> lastPair;
        List<ImmutablePair<String, String>> list = new ArrayList<>();

        int i = 0;

        for (String word : args) {
            i++;
            if (isOdd(i)) {
                lastTag = word;
            } else {
                lastPair = new ImmutablePair<>(lastTag, word);
                list.add(lastPair);
            }
        }
        return list;
    }

    private static boolean isOdd(int x) {
        return x % 2 == 1;
    }


    private static Optional<List<Integer>> buildGranularityList(String argVal) {
        return Optional.of(
                Stream.of(argVal.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())
        );
    }
}
