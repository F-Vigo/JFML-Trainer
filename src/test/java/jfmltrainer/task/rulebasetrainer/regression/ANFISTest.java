package jfmltrainer.task.rulebasetrainer.regression;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.regression.anfis.ANFIS;
import org.junit.Test;

public class ANFISTest {

    @Test
    public void whenRun_noExceptionThrown() {
        Data data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();
        JFMLRandom.createObject(42);

        new ANFIS().trainRuleBase(data, knowledgeBase, methodConfig);
    }
}
