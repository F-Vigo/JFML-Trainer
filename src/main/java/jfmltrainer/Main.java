package jfmltrainer;

import jfmltrainer.args.Args;
import jfmltrainer.args.ArgsParser;
import jfmltrainer.task.Task;
import jfmltrainer.task.graphics.JFMLTrainerGraphics;
import jfmltrainer.task.knowledgebasebuilder.KnowledgeBaseBuilder;
import jfmltrainer.task.metrics.Metrics;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerSelector;

public class Main {

    public static void main(String[] args) {
        Args argsObj = ArgsParser.parse(args);

        if(argsObj.getTask().isPresent()) {
            doTask(argsObj.getTask().get(), argsObj);
        } else {
            System.out.println("ERROR: Wrong or missing process option.");
        }
    }

    private static void doTask(Task task, Args args) { // TODO - Define errors this may throw.

        switch (task) {
            case RULE_BASE_TRAINER:
                RuleBaseTrainerSelector.getInstance().train(args);
                break;
            case KNOWLEDGE_BASE_BUILDER:
                KnowledgeBaseBuilder.build(args);
                break;
            case GRAPHICS:
                JFMLTrainerGraphics.drawAndSaveImage(args);
                break;
            case METRICS:
                Metrics.computeMetrics(args);
                break;
        }
    }
}
