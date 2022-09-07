package jfmltrainer.task.metrics;

import jfml.FuzzyInferenceSystem;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.*;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.fileparser.frbs.FISParser;
import jfmltrainer.task.metrics.computer.VariableMetricsComputerClassification;
import jfmltrainer.task.metrics.computer.VariableMetricsComputerRegression;
import jfmltrainer.task.metrics.measure.ClassificationMeasures;
import jfmltrainer.task.metrics.measure.Measures;
import jfmltrainer.task.metrics.measure.RegressionMeasures;
import jfmltrainer.task.metrics.writer.MetricsWriterClassification;
import jfmltrainer.task.metrics.writer.MetricsWriterRegression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Metrics {

    public static void computeMetrics(Args args) {

        //FuzzyInferenceSystem frbs = FISParser.getInstance().read(args.getFrbsPath().get()); // TODO - Fix!
        FuzzyInferenceSystem frbs = getFRBS();
        Data testSet = null;
        try {
            testSet = RegressionDataParser.getInstance().read(args.getDataPath().get());
        } catch (Exception e) {
            testSet = ClassificationDataParser.getInstance().read(args.getDataPath().get());
        }

        try {
            computeMetrics(frbs, testSet);
        } catch (Exception e) {}
    }

    public static void computeMetrics(FuzzyInferenceSystem frbs, Data testSet) throws IOException {

        List<Measures> measuresList = new ArrayList<>();
        Boolean isRegression = isRegression(testSet);

        measuresList.addAll(
                isRegression
                ? new VariableMetricsComputerRegression().computeMetrics(frbs, testSet)
                : new VariableMetricsComputerClassification().computeMetrics(frbs, testSet)
        );

        if (isRegression) {
            new MetricsWriterRegression().write(measuresList.stream().map(metrics -> (RegressionMeasures) metrics).collect(Collectors.toList()));
        } else {
            new MetricsWriterClassification().write(measuresList.stream().map(metrics -> (ClassificationMeasures) metrics).collect(Collectors.toList()));
        }
    }

    private static boolean isRegression(Data testSet) {
        return testSet.getInstanceList().get(0) instanceof RegressionInstance;
    }







    private static FuzzyInferenceSystem getFRBS() {
        FuzzyInferenceSystem frbs = new FuzzyInferenceSystem();
        KnowledgeBaseType knowledgeBase = getKB();
        frbs.setKnowledgeBase(knowledgeBase);
        frbs.addRuleBase(getRB(knowledgeBase));
        return frbs;
    }

    private static KnowledgeBaseType getKB() {

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

    private static RuleBaseType getRB(KnowledgeBaseType knowledgeBase) {

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
