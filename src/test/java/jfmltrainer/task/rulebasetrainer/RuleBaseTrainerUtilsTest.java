package jfmltrainer.task.rulebasetrainer;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.*;
import jfml.term.FuzzyTermType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.operator.and.AndOperatorPROD;
import jfmltrainer.operator.then.ThenOperatorPROD;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RuleBaseTrainerUtilsTest {

    @Test
    public void getBestRule() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType variable1 = new FuzzyVariableType("variable1", 0F, 10F);
        variable1.setType("input");
        variable1.addFuzzyTerm(new FuzzyTermType("variable1_term1", 3, new float[]{0F,0F,10F}));
        variable1.addFuzzyTerm(new FuzzyTermType("variable1_term2", 3, new float[]{0F,10F,10F}));
        knowledgeBase.addVariable(variable1);

        FuzzyVariableType variable2 = new FuzzyVariableType("variable2", 0F, 10F);
        variable2.setType("output");
        variable2.addFuzzyTerm(new FuzzyTermType("variable2_term1", 3, new float[]{0F,0F,10F}));
        variable2.addFuzzyTerm(new FuzzyTermType("variable2_term2", 3, new float[]{0F,10F,10F}));
        knowledgeBase.addVariable(variable2);

        RegressionInstance instance = new RegressionInstance(Collections.singletonList(0F), Collections.singletonList(0F));

        FuzzyRuleType rule = RuleBaseTrainerUtils.getBestRule(instance, knowledgeBase);

        Assert.assertEquals(variable1.getTerms().get(0).getName(), ((FuzzyTermType) rule.getAntecedent().getClauses().get(0).getTerm()).getName());
        Assert.assertEquals(variable2.getTerms().get(0).getName(), ((FuzzyTermType) rule.getConsequent().getThen().getClause().get(0).getTerm()).getName());
    }


    @Test
    public void getValueList() {
        RegressionInstance regressionInstance = new RegressionInstance(Collections.singletonList(1F), Collections.singletonList(2F));
        Assert.assertEquals(List.of(1F, 2F), RuleBaseTrainerUtils.getValueList(regressionInstance));
    }


    @Test
    public void buildRuleFromTermList() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();
        FuzzyVariableType variable1 = new FuzzyVariableType();
        variable1.setType("input");
        FuzzyVariableType variable2 = new FuzzyVariableType();
        variable2.setType("output");
        knowledgeBase.addVariable(variable1);
        knowledgeBase.addVariable(variable2);

        List<FuzzyTermType> termList = List.of(
                new FuzzyTermType("term1", 7, new float[]{0F, 5F, 10F}),
                new FuzzyTermType("term2", 7, new float[]{0F, 5F, 10F})
        );

        FuzzyRuleType rule = RuleBaseTrainerUtils.buildRuleFromTermList(termList, knowledgeBase);

        Assert.assertEquals(termList.get(0).getName(), ((FuzzyTermType) rule.getAntecedent().getClauses().get(0).getTerm()).getName());
        Assert.assertEquals(termList.get(1).getName(), ((FuzzyTermType) rule.getConsequent().getThen().getClause().get(0).getTerm()).getName());
    }


    @Test
    public void computeWeight() {

        RegressionInstance instance = new RegressionInstance(Collections.singletonList(2F), Collections.singletonList(3F));
        ClauseType antecedentClause = new ClauseType(new FuzzyVariableType(), new FuzzyTermType("term1", 3, new float[]{0F,0F,4F}));
        AntecedentType antecedent = new AntecedentType(Collections.singletonList(antecedentClause));
        ClauseType consequentClause = new ClauseType(new FuzzyVariableType(), new FuzzyTermType("term2", 3, new float[]{0F,3F,3F}));
        ConsequentType consequent = new ConsequentType(new ConsequentClausesType(Collections.singletonList(consequentClause)), null);
        FuzzyRuleType rule = new FuzzyRuleType("rule", antecedent, consequent);

        Float expected = 0.5F;
        Float actual = RuleBaseTrainerUtils.computeWeight(instance, rule, AndOperatorPROD.getInstance(), ThenOperatorPROD.getInstance());

        Assert.assertEquals(expected, actual);
    }


    @Test
    public void getCoveredInstances() {
        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType variable1 = new FuzzyVariableType("variable1", 0F, 10F);
        variable1.setType("input");
        FuzzyTermType inputTerm1 = new FuzzyTermType("variable1_term1", 3, new float[]{0F,0F,10F});
        FuzzyTermType inputTerm2 = new FuzzyTermType("variable1_term2", 3, new float[]{0F,10F,10F});
        variable1.addFuzzyTerm(inputTerm1);
        variable1.addFuzzyTerm(inputTerm2);
        knowledgeBase.addVariable(variable1);

        FuzzyVariableType variable2 = new FuzzyVariableType("variable2", 0F, 10F);
        variable2.setType("output");
        FuzzyTermType outputTerm = new FuzzyTermType("variable2_term", 3, new float[]{0F,0F,10F});
        variable2.addFuzzyTerm(outputTerm);
        knowledgeBase.addVariable(variable2);

        ClauseType antecedentClause = new ClauseType(variable1, inputTerm1);
        AntecedentType antecedent = new AntecedentType(Collections.singletonList(antecedentClause));
        ClauseType consequentClause = new ClauseType(variable2, outputTerm);
        ConsequentType consequent = new ConsequentType(new ConsequentClausesType(Collections.singletonList(consequentClause)), null);
        FuzzyRuleType rule = new FuzzyRuleType("rule", antecedent, consequent);

        RegressionInstance instance1 = new RegressionInstance(Collections.singletonList(0F), Collections.singletonList(0F));
        RegressionInstance instance2 = new RegressionInstance(Collections.singletonList(10F), Collections.singletonList(0F));
        Data data = new Data(List.of(instance1, instance2));

        Assert.assertEquals(Collections.singletonList(instance1), RuleBaseTrainerUtils.getCoveredInstances(rule, data, knowledgeBase));
    }
}
