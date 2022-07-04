package jfmltrainer.trainer.rulebasetrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.fileparser.KnowledgeBaseParser;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.DataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.TrainerSelector;
import jfmltrainer.trainer.rulebasetrainer.regression.ch.CordonHerrera;
import jfmltrainer.trainer.rulebasetrainer.regression.wm.WangMendel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RuleBaseTrainerSelector extends TrainerSelector {

    public void train(Args args) {
        if (args.getMethod().isPresent()) {
            doTrain(args);
        } else {
            System.out.println("ERROR: Wrong or missing method.");
        }
    }

    @Override
    protected void doTrain(Args args) {

        RuleBaseTrainer ruleBaseTrainer = selectMethod(args.getMethod().get());
        Data data = selectDataParser(args.getMethod().get()).read(args.getDataPath().get());
        KnowledgeBaseType knowledgeBase = getOrBuildKnowledgeBaseParser(args.getKnowledgeBasePath(), null);
        String outputFolder = args.getOutputFolder().get();
        String outputName = args.getOutputName().get();
        MethodConfig methodConfig = MethodConfig.fromArgs(args);

        ruleBaseTrainer.train(data, knowledgeBase, outputFolder, outputName, methodConfig);
    }

    private RuleBaseTrainer selectMethod(RuleBaseTrainerMethod method) {
        switch (method) {
            case WANG_MENDEL:
                return new WangMendel();
            case CORDON_HERRERA:
                return new CordonHerrera();
            default:
                return null;
        }
    }

    private DataParser selectDataParser(RuleBaseTrainerMethod method) {
        switch (method.getProblem()) {
            case REGRESSION:
                return new RegressionDataParser();
            case CLASSIFICATION:
                return new ClassificationDataParser();
            default:
                return null;
        }
    }

    private KnowledgeBaseType getOrBuildKnowledgeBaseParser(Optional<String> knowledgeBasePath, Optional<String> variableDefinitionFilePath) {
        return knowledgeBasePath.isPresent()
                ? new KnowledgeBaseParser().read(knowledgeBasePath.get())
                : buildDefaultKnowledgeBase(variableDefinitionFilePath);
    }

    private KnowledgeBaseType buildDefaultKnowledgeBase(Optional<String> variableDefinitionFilePath) {
        // show KB diagram
        return null; // TODO
    }
}
