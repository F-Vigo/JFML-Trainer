package jfmltrainer.task.rulebasetrainer.regression;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.regression.wm.WangMendel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

public class WangMendelTest {

    @Test
    public void test() {
        Data data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();

        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = new WangMendel().trainRuleBase(data, knowledgeBase, methodConfig);
        RuleBaseType ruleBase = fuzzySystem.getRight();

        Assert.assertEquals(2, ruleBase.getRules().size());
        assertFirstRulePresent(ruleBase);
        assertSecondRulePresent(ruleBase);
    }

    private void assertFirstRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(TestUtils.getRule(ruleBase, "IF food IS rancid AND service IS poor THEN tip IS cheap").isPresent());
    }

    private void assertSecondRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(TestUtils.getRule(ruleBase, "IF food IS good AND service IS good THEN tip IS average").isPresent());
    }
}
