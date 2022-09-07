package jfmltrainer.task.rulebasetrainer.tuning.lateral;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import org.junit.Ignore;
import org.junit.Test;


public class LateralDisplacementTunerTest {

    @Test
    @Ignore
    public void whenRun_noExceptionThrown() {

        Data data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        RuleBaseType ruleBase = TestUtils.getSimpleTipperMamdaniRuleBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();
        JFMLRandom.createObject(42);

        new LateralDisplacementTuner().tuneFRBS(data, knowledgeBase, ruleBase, methodConfig);
    }
}
