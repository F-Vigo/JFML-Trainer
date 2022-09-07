package jfmltrainer.task.rulebasetrainer.regression;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.data.Data;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORWM;
import org.junit.Test;

public class CORTest {

    @Test
    public void whenRun_noExceptionThrown() {
        Data data = TestUtils.getSimpleTipperMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleTipperMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();

        new CORWM().trainRuleBase(data, knowledgeBase, methodConfig);
    }
}
