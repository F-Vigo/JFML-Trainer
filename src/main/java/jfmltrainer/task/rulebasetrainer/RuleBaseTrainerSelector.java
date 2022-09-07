package jfmltrainer.task.rulebasetrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.*;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.fileparser.VariableDefinitionDataParser;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.DataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.fileparser.frbs.KnowledgeBaseParser;
import jfmltrainer.fileparser.frbs.RuleBaseParser;
import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.task.knowledgebasebuilder.KnowledgeBaseBuilder;
import jfmltrainer.task.rulebasetrainer.tuning.Tuner;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RuleBaseTrainerSelector {

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

    protected void doTrain(Args args) {

        RuleBaseTrainerMethod method = args.getMethod().get();

        RuleBaseTrainer ruleBaseTrainer = method.getTrainer();
        Tuner ruleBaseTuner = method.getTuner();

        Data data = selectDataParser(args.getMethod().get(), args.getDataPath().get()).read(args.getDataPath().get());
        KnowledgeBaseType knowledgeBase = getOrBuildKnowledgeBaseParser(args.getKnowledgeBasePath(), args.getVariableDefinitionDataPath(), data, args.getGranularityList(), args.getSingleGranularity());

        //Optional<RuleBaseType> ruleBase = args.getRuleBasePath().map(RuleBaseParser.getInstance()::read);
        Optional<RuleBaseType> ruleBase = Optional.of(getRB(knowledgeBase));

        String outputFolder = args.getOutputFolder().get();
        String outputName = args.getOutputName().get();
        MethodConfig methodConfig = MethodConfig.fromArgs(args);

        if (ruleBaseTuner == null) {
            ruleBaseTrainer.train(data, knowledgeBase, outputFolder, outputName, methodConfig);
        } else {
            ruleBaseTuner.tune(data, knowledgeBase, ruleBase.get(), methodConfig, outputFolder, outputName);
        }
    }

    private DataParser selectDataParser(RuleBaseTrainerMethod method, String dataPath) {
        switch (method.getProblem()) {
            case REGRESSION:
                return RegressionDataParser.getInstance();
            case CLASSIFICATION:
                return ClassificationDataParser.getInstance();
            case TUNING:
                return inferDataParser(dataPath);
            default:
                return null;
        }
    }

    private DataParser inferDataParser(String dataPath) {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(dataPath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<String> valueList = List.of(scanner.nextLine().split(";")); // Length equals 2 (input, output).
        List<String> consequentValueListAsString = List.of(valueList.get(1).split(","));
        String firstConsequent = consequentValueListAsString.get(0);

        return isInteger(firstConsequent)
                ? RegressionDataParser.getInstance()
                : ClassificationDataParser.getInstance();
    }

    private boolean isInteger(String firstConsequent) {
        try {
            Integer.parseInt(firstConsequent);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private KnowledgeBaseType getOrBuildKnowledgeBaseParser(Optional<String> knowledgeBasePath, Optional<String> variableDefinitionFilePath, Data data, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity) {
        return knowledgeBasePath.isPresent()
                //? KnowledgeBaseParser.getInstance().read(knowledgeBasePath.get()) // TODO - Fix!
        ? getKB()
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









    private KnowledgeBaseType getKB() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType food = new FuzzyVariableType("food", 0, 10);
        food.setType("input");
        food.addFuzzyTerm("rancid", 3, new float[]{0F,0F,5F});
        food.addFuzzyTerm("good", 3, new float[]{0F,5F,10F});
        food.addFuzzyTerm("delicious", 3, new float[]{5F,10F,10F});

        FuzzyVariableType service = new FuzzyVariableType("service", 0, 10);
        service.setType("input");
        service.addFuzzyTerm("poor", 3, new float[]{0F,0F,5F});
        service.addFuzzyTerm("good", 3, new float[]{0F,5F,10F});
        service.addFuzzyTerm("excellent", 3, new float[]{5F,10F,10F});

        FuzzyVariableType tip = new FuzzyVariableType("tip", 0, 10);
        tip.setType("output");
        tip.addFuzzyTerm("cheap", 3, new float[]{0F,0F,5F});
        tip.addFuzzyTerm("average", 3, new float[]{0F,5F,10F});
        tip.addFuzzyTerm("generous", 3, new float[]{5F,10F,10F});

        knowledgeBase.addVariable(food);
        knowledgeBase.addVariable(service);
        knowledgeBase.addVariable(tip);

        return knowledgeBase;
    }

    private RuleBaseType getRB(KnowledgeBaseType knowledgeBase) {

        RuleBaseType ruleBase = new RuleBaseType();

        FuzzyVariableType food = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(0);
        FuzzyVariableType service = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(1);
        FuzzyVariableType tip = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(2);

        AntecedentType antecedent1 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("rancid")),
                new ClauseType(service, service.getTerm("good"))
        ));
        ConsequentType consequent1 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("average")))),
                null
        );
        FuzzyRuleType rule1 = new FuzzyRuleType("", antecedent1, consequent1);

        AntecedentType antecedent2 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("rancid")),
                new ClauseType(service, service.getTerm("poor"))
        ));
        ConsequentType consequent2 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("cheap")))),
                null
        );
        FuzzyRuleType rule2 = new FuzzyRuleType("", antecedent2, consequent2);

        ruleBase.addRule(rule1);
        ruleBase.addRule(rule2);

        return ruleBase;
    }

}
