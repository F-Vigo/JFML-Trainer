package jfmltrainer.args;

import jfmltrainer.Task;
import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.trainer.rulebasetrainer.regression.cor.CORSearchMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArgsParser {

    public static Args parse(String[] args) {

        List<ImmutablePair<String, String>> argPairList = splitArgsInPairs(args);

        Optional<Task> task = Optional.empty();
        Optional<RuleBaseTrainerMethod> method = Optional.empty();

        Optional<String> dataPath = Optional.empty();
        Optional<String> knowledgeBasePath = Optional.empty();

        Optional<AndOperator> andOperator = Optional.empty();
        Optional<OrOperator> orOperator = Optional.empty();
        Optional<ThenOperator> thenOperator = Optional.empty();

        Optional<RVFOperator> rvfOperator = Optional.empty();
        Optional<CORSearchMethod> corSearchMethod = Optional.empty();

        Optional<String> outputFolder = Optional.empty();
        Optional<String> outputName = Optional.empty();

        for(ImmutablePair<String, String> tagAndValue: argPairList) {
            switch (tagAndValue.getLeft()) {
                case "-t":
                    task = Task.fromString(tagAndValue.getRight());
                    break;
                // TODO - Expand
                default:
                    break;
            }
        }

        return new Args(
                task,
                method,
                dataPath,
                knowledgeBasePath,
                andOperator,
                orOperator,
                thenOperator,
                rvfOperator,
                corSearchMethod,
                outputFolder,
                outputName
        );
    }

    private static List<ImmutablePair<String, String>> splitArgsInPairs(String[] args) {

        String lastTag = null;
        ImmutablePair<String, String> lastPair = null;
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
}
