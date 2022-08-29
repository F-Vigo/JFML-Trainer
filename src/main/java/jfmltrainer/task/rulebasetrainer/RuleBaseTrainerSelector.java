package jfmltrainer.task.rulebasetrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.fileparser.VariableDefinitionDataParser;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.DataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.fileparser.frbs.KnowledgeBaseParser;
import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.task.knowledgebasebuilder.KnowledgeBaseBuilder;
import jfmltrainer.task.rulebasetrainer.regression.ch.CordonHerrera;
import jfmltrainer.task.rulebasetrainer.regression.wm.WangMendel;
import jfmltrainer.task.rulebasetrainer.tuning.TrainerSelector;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RuleBaseTrainerSelector extends TrainerSelector {

    private static RuleBaseTrainerSelector instance = new RuleBaseTrainerSelector();

    private RuleBaseTrainerSelector() {}


    public static RuleBaseTrainerSelector getInstance() {
        return instance;
    }

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
        KnowledgeBaseType knowledgeBase = getOrBuildKnowledgeBaseParser(args.getKnowledgeBasePath(), args.getVariableDefinitionDataPath(), data, args.getGranularityList(), args.getSingleGranularity());
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
                return RegressionDataParser.getInstance();
            case CLASSIFICATION:
                return ClassificationDataParser.getInstance();
            default:
                return null;
        }
    }

    private KnowledgeBaseType getOrBuildKnowledgeBaseParser(Optional<String> knowledgeBasePath, Optional<String> variableDefinitionFilePath, Data data, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity) {
        return knowledgeBasePath.isPresent()
                ? KnowledgeBaseParser.getInstance().read(knowledgeBasePath.get())
                : buildDefaultKnowledgeBase(variableDefinitionFilePath, data, granularityList, singleGranularity);
    }

    private KnowledgeBaseType buildDefaultKnowledgeBase(Optional<String> variableDefinitionFilePath, Data data, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity) {
        KnowledgeBaseBuilder.build(
                variableDefinitionFilePath.map(x -> VariableDefinitionDataParser.getInstance().read(x)),
                Optional.of(data),
                granularityList,
                singleGranularity);
        return null; // TODO
    }
}
