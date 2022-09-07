package jfmltrainer;

import jfmltrainer.args.Args;
import jfmltrainer.args.ArgsParser;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.task.Task;
import jfmltrainer.task.graphics.JFMLTrainerGraphics;
import jfmltrainer.task.knowledgebasebuilder.KnowledgeBaseBuilder;
import jfmltrainer.task.metrics.Metrics;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerSelector;

public class Main {

    public static void main(String[] args) {

        //args = new String[]{"-t", "RB", "-m", "WM", "-d", "./data.txt", "-kb", "./kb.xml", "-of", "./out", "-on", "fs"}; // Use case 1
        //args = new String[]{"-t", "G", "-frbs", "./out/fs.xml"};

        //args = new String[]{"-t", "RB", "-m", "DJ", "-d", "./data.txt", "-kb", "./kb.xml", "-rb", "./rb.xml", "-of", "./out", "-on", "fs2", "-ao", "PROD", "-mi", "150"}; // Use case 2
        //args = new String[]{"-t", "G", "-kb", "./kb.xml", "-rb", "./rb.xml", "-nfrbs", "./out/fs2.xml"};

        //args = new String[]{"-t", "M", "-frbs", "./fs2.xml", "-d", "./data.txt"}; // Metrics

        //args = new String[]{"-t", "G", "-kb", "null1", "-nkb", "null2", "-rb", "null3", "-nrb", "null4"};

        //args = new String[]{"-t", "RB", "-m", "LAT", "-d", "./data.txt", "-kb", "./kb.xml", "-rb", "./rb.xml", "-of", ".", "-on", "fs2"};

        //args = new String[]{"-t", "RB", "-m", "ANFIS", "-d", "./data.txt", "-kb", "./kb.xml", "-of", "./out", "-on", "fs0"};

        args = new String[]{"-t", "RB", "-m", "Thrift", "-d", "./data.txt", "-kb", "./kb.xml", "-of", "./out", "-on", "fs0"};

        Args argsObj = ArgsParser.parse(args);

        JFMLRandom.createObject(argsObj.getSeed().orElse(42));

        if(argsObj.getTask().isPresent()) {
            doTask(argsObj.getTask().get(), argsObj);
        } else {
            System.out.println("ERROR: Wrong or missing process option.");
        }
    }

    private static void doTask(Task task, Args args) {
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
