package jfmltrainer;

import jfmltrainer.args.Args;
import jfmltrainer.args.ArgsParser;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerSelector;

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

        RuleBaseTrainerSelector ruleBaseTrainerSelector = new RuleBaseTrainerSelector();

        switch (task) {
            case RULE_BASE_TRAINER:
                ruleBaseTrainerSelector.train(args);
                break;
            case KNOWLEDGE_BASE_TRAINER:
                // TODO;
                break;
        }
    }
}
