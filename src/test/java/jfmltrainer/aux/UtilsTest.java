package jfmltrainer.aux;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.AntecedentType;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.RegressionInstance;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class UtilsTest {

    @Test
    public void accuracy() {
        List<List<String>> realValueList = List.of(Collections.singletonList("+"), Collections.singletonList("+"), Collections.singletonList("-"), Collections.singletonList("-"));
        List<List<String>> predictedValueList = List.of(Collections.singletonList("-"), Collections.singletonList("-"), Collections.singletonList("-"), Collections.singletonList("-"));
        Float accuracy = Utils.computeAccuracy(realValueList, predictedValueList);
        Assert.assertEquals("0.5", accuracy.toString());
    }

    @Test
    public void argMax() {
        List<Integer> list = List.of(-1,-2,-3);
        ToDoubleFunction<Integer> f = x -> x*x;
        Integer expected = -3;
        Integer actual = Utils.argMax(list, f);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getVariableByIndex() {
        List<List<Float>> list = List.of(
                List.of(1F,2F,3F),
                List.of(1F,2F,3F),
                List.of(1F,2F,3F)
        );
        List<Float> actual = Utils.getVariableByIndex(0, list);
        List<Float> expected = List.of(1F, 1F, 1F);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void computeMSE() {
        List<List<Float>> realValueList = List.of(Collections.singletonList(0F), Collections.singletonList(2F));
        List<List<Float>> predictedValueList = List.of(Collections.singletonList(1F), Collections.singletonList(1F));
        Float expected = 2F;
        Assert.assertEquals(expected, Utils.computeMSE(realValueList, predictedValueList));
    }

    @Test
    public void buildApproximativeKnowledgeBase() {

        FuzzyVariableType inputVariable = new FuzzyVariableType("input", 0, 10);
        FuzzyVariableType outputVariable = new FuzzyVariableType("output", 0, 10);
        outputVariable.setType("output");
        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();
        knowledgeBase.addVariable(inputVariable);
        knowledgeBase.addVariable(outputVariable);

        AntecedentType antecedent = new AntecedentType();
        ConsequentType consequent = new ConsequentType();

        FuzzyTermType antecedentTerm = new FuzzyTermType("inputTerm", 3, new float[]{0F,5F,10F});
        FuzzyTermType consequentTerm = new FuzzyTermType("outputTerm", 3, new float[]{0F,5F,10F});

        ClauseType antecedentClause = new ClauseType(inputVariable, antecedentTerm, null);
        ClauseType consequentClause = new ClauseType(outputVariable, consequentTerm, null);

        antecedent.addClause(antecedentClause);
        consequent.addThenClause(consequentClause);

        RuleBaseType ruleBase = new RuleBaseType();
        FuzzyRuleType rule = new FuzzyRuleType("rule", antecedent, consequent);
        ruleBase.addRule(rule);

        KnowledgeBaseType newKnowledgeBaseType = Utils.buildApproximativeKnowledgeBase(knowledgeBase, ruleBase);

        Assert.assertEquals(Collections.singletonList(antecedentTerm), newKnowledgeBaseType.getKnowledgeBaseVariables().get(0).getTerms());
        Assert.assertEquals(Collections.singletonList(consequentTerm), newKnowledgeBaseType.getKnowledgeBaseVariables().get(1).getTerms());
    }

    @Test
    @Ignore // TODO
    public void twoPointCrossover() {

        List<String> left = List.of("L", "L", "L", "L");
        List<String> right = List.of("R", "R", "R", "R");
        ImmutablePair<List<String>, List<String>> expected = new ImmutablePair<>(
                List.of("L", "R", "R", "L"),
                List.of("R", "L", "L", "R")
        );

        JFMLRandom jfmlRandom = Mockito.mock(JFMLRandom.class);
        Mockito.when(jfmlRandom.randInt(left.size())).thenReturn(1).thenReturn(3);
        JFMLRandom.setInstance(jfmlRandom);

        ImmutablePair<List<String>, List<String>> actual = Utils.twoPointCrossover(left, right);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void computeMean() {
        List<Float> list = List.of(0F, 2F);
        Float expected = 1F;
        Assert.assertEquals(expected, Utils.computeMean(list));
    }

    @Test
    public void computeSTD() {
        List<Float> list = List.of(0F, 2F);
        Float expected = 1F;
        Assert.assertEquals(expected, Utils.computeSTD(list));
    }

    @Test
    public void predict() {

        FuzzyVariableType inputVariable = new FuzzyVariableType("input", 0, 10);
        FuzzyVariableType outputVariable = new FuzzyVariableType("output", 0, 10);
        outputVariable.setType("output");

        AntecedentType antecedent1 = new AntecedentType();
        ConsequentType consequent1 = new ConsequentType();

        FuzzyTermType antecedentTerm1 = new FuzzyTermType("inputTerm1", 3, new float[]{0F,0F,10F});
        FuzzyTermType consequentTerm1 = new FuzzyTermType("outputTerm1", 3, new float[]{0F,0F,10F});

        ClauseType antecedentClause1 = new ClauseType(inputVariable, antecedentTerm1, null);
        ClauseType consequentClause1 = new ClauseType(outputVariable, consequentTerm1, null);

        antecedent1.addClause(antecedentClause1);
        consequent1.addThenClause(consequentClause1);


        AntecedentType antecedent2 = new AntecedentType();
        ConsequentType consequent2 = new ConsequentType();

        FuzzyTermType antecedentTerm2 = new FuzzyTermType("inputTerm2", 3, new float[]{0F,10F,10F});
        FuzzyTermType consequentTerm2 = new FuzzyTermType("outputTerm2", 3, new float[]{0F,10F,10F});

        ClauseType antecedentClause2 = new ClauseType(inputVariable, antecedentTerm2, null);
        ClauseType consequentClause2 = new ClauseType(outputVariable, consequentTerm2, null);

        antecedent2.addClause(antecedentClause2);
        consequent2.addThenClause(consequentClause2);


        RuleBaseType ruleBase = new RuleBaseType();
        FuzzyRuleType rule1 = new FuzzyRuleType("rule1", antecedent1, consequent1);
        FuzzyRuleType rule2 = new FuzzyRuleType("rule2", antecedent2, consequent2);
        ruleBase.addRule(rule1);
        ruleBase.addRule(rule2);

        ClassificationInstance instance = new ClassificationInstance(Collections.singletonList(0F), null);
        List<String> label = Utils.predict(instance, ruleBase);

        Assert.assertEquals(Collections.singletonList(consequentTerm1.getName()), label);
    }
}
