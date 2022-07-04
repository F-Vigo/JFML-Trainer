package jfmltrainer.trainer.rulebasetrainer.classification.chi;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.MethodConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

public class ChiCordonHerreraTest {


    @Test
    public void test() {

        Data data = TestUtils.getSimpleIrisMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleIrisMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();

        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = new ChiCHBased().trainRuleBase(data, knowledgeBase, methodConfig);
        RuleBaseType ruleBase = fuzzySystem.getRight();

        Assert.assertEquals(2, ruleBase.getRules().size());
        assertFirstRulePresent(ruleBase);
        assertSecondRulePresent(ruleBase);
    }

    private void assertFirstRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(TestUtils.getRule(ruleBase, "IF PetalLength IS low AND PetalWidth IS low THEN IrisClass IS setosa").isPresent());
    }

    private void assertSecondRulePresent(RuleBaseType ruleBase) {
        Assert.assertTrue(TestUtils.getRule(ruleBase, "IF PetalLength IS medium AND PetalWidth IS medium THEN IrisClass IS virginica").isPresent());
    }
}
