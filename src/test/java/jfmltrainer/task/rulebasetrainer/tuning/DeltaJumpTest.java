package jfmltrainer.task.rulebasetrainer.tuning;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.classification.furia.FURIA;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.DeltaJump;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;


public class DeltaJumpTest {

    @Test
    public void whenRun_noExceptionThrown() {

        Data data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        RuleBaseType ruleBase = TestUtils.getSimpleTipperMamdaniRuleBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();
        JFMLRandom.createObject(42);

        new DeltaJump().tuneFRBS(data, knowledgeBase, ruleBase, methodConfig);
    }
}
