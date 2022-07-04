package jfmltrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.operator.and.AndOperatorMIN;
import jfmltrainer.operator.or.OrOperatorMAX;
import jfmltrainer.operator.rvf.RVFOperatorMAX;
import jfmltrainer.operator.then.ThenOperatorMIN;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.rulebasetrainer.regression.cor.CORSearchMethod;

import java.util.Optional;

public class TestUtils {

    public static Data<RegressionInstance> getSimpleTipperMamdaniData() {
        return new RegressionDataParser().read("src/test/resources/data/SimpleTipperMamdaniData.txt");
    }

    public static Data<ClassificationInstance> getSimpleIrisMamdaniData() {
        return new ClassificationDataParser().read("src/test/resources/data/SimpleIrisMamdaniData.txt");
    }

    public static KnowledgeBaseType getSimpleTipperMamdaniKnowledgeBase() {
        //return KnowledgeBaseParser.read("src/test/resources/knowledgebase/SimpleTipperMamdaniKnowledgeBase.xml");
        return buildSimpleTipperMamdaniKB();
    }

    public static KnowledgeBaseType getSimpleIrisMamdaniKnowledgeBase() {
        //return KnowledgeBaseParser.read("src/test/resources/knowledgebase/SimpleTipperMamdaniKnowledgeBase.xml");
        return buildSimpleIrisMamdaniKB();
    }

    public static MethodConfig getSimpleMethodConfig() {
        return new MethodConfig(
                Optional.of(new AndOperatorMIN()),
                Optional.of(new OrOperatorMAX()),
                Optional.of(new ThenOperatorMIN()),
                Optional.of(new RVFOperatorMAX()),
                Optional.of(CORSearchMethod.EXPLICIT_ENUMERATION)
        );
    }

    public static Optional<FuzzyRuleType> getRule(RuleBaseType ruleBase, String text) {
        return ruleBase.getRules().stream()
                .filter(rule -> rule.toString().contains(text))
                .findAny();
    }

    private static KnowledgeBaseType buildSimpleTipperMamdaniKB() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType food = new FuzzyVariableType("food", 0, 10);
        food.addFuzzyTerm(new FuzzyTermType("rancid", 3, new float[]{0, 0, 5}));
        food.addFuzzyTerm(new FuzzyTermType("good", 3, new float[]{0, 5, 10}));
        food.addFuzzyTerm(new FuzzyTermType("delicious", 3, new float[]{5, 10, 10}));

        FuzzyVariableType service = new FuzzyVariableType("service", 0, 10);
        service.addFuzzyTerm(new FuzzyTermType("poor", 3, new float[]{0, 0, 5}));
        service.addFuzzyTerm(new FuzzyTermType("good", 3, new float[]{0, 5, 10}));
        service.addFuzzyTerm(new FuzzyTermType("excellent", 3, new float[]{5, 10, 10}));

        FuzzyVariableType tip = new FuzzyVariableType("tip", 0, 10);
        tip.addFuzzyTerm(new FuzzyTermType("cheap", 3, new float[]{0, 0, 5}));
        tip.addFuzzyTerm(new FuzzyTermType("average", 3, new float[]{0, 5, 10}));
        tip.addFuzzyTerm(new FuzzyTermType("generous", 3, new float[]{5, 10, 10}));
        tip.setType("output");

        knowledgeBase.addVariable(food);
        knowledgeBase.addVariable(service);
        knowledgeBase.addVariable(tip);

        return knowledgeBase;
    }


    private static KnowledgeBaseType buildSimpleIrisMamdaniKB() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType food = new FuzzyVariableType("PetalLength", 0, 10);
        food.addFuzzyTerm(new FuzzyTermType("low", 3, new float[]{0, 0, 5}));
        food.addFuzzyTerm(new FuzzyTermType("medium", 3, new float[]{0, 5, 10}));
        food.addFuzzyTerm(new FuzzyTermType("high", 3, new float[]{5, 10, 10}));

        FuzzyVariableType service = new FuzzyVariableType("PetalWidth", 0, 10);
        service.addFuzzyTerm(new FuzzyTermType("low", 3, new float[]{0, 0, 5}));
        service.addFuzzyTerm(new FuzzyTermType("medium", 3, new float[]{0, 5, 10}));
        service.addFuzzyTerm(new FuzzyTermType("high", 3, new float[]{5, 10, 10}));

        FuzzyVariableType tip = new FuzzyVariableType("IrisClass", 0, 2);
        tip.addFuzzyTerm(new FuzzyTermType("setosa", 1, new float[]{0}));
        tip.addFuzzyTerm(new FuzzyTermType("virginica", 1, new float[]{1}));
        tip.addFuzzyTerm(new FuzzyTermType("versicolor", 1, new float[]{2}));
        tip.setType("output");

        knowledgeBase.addVariable(food);
        knowledgeBase.addVariable(service);
        knowledgeBase.addVariable(tip);

        return knowledgeBase;
    }


}
