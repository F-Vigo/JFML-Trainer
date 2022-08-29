package jfmltrainer.task.rulebasetrainer.regression;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.regression.ch.CordonHerrera;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class CordonHerreraTest {

    @Test
    public void test() {

        Data<RegressionInstance> data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();

        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = new CordonHerrera().trainRuleBase(data, knowledgeBase, methodConfig);
        RuleBaseType ruleBase = fuzzySystem.getRight();

        Assert.assertEquals(2, ruleBase.getRules().size());
        assertFirstRulePresent(ruleBase);
        assertSecondRulePresent(ruleBase);
    }

    private Optional<FuzzyRuleType> getRule(RuleBaseType ruleBase, String text) {
        return ruleBase.getRules().stream()
                .filter(rule -> rule.toString().contains(text))
                .findAny();
    }

    private void assertFirstRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(getRule(ruleBase, "IF food IS rancid AND service IS poor THEN tip IS cheap").isPresent());
    }

    private void assertSecondRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(getRule(ruleBase, "IF food IS good AND service IS good THEN tip IS average").isPresent());
    }
}
