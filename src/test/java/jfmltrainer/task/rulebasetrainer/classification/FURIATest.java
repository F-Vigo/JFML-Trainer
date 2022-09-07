package jfmltrainer.task.rulebasetrainer.classification;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.TestUtils;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.task.graphics.JFMLTrainerGraphics;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.classification.furia.FURIA;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;


public class FURIATest {

    @Test
    @Ignore
    public void whenRun_noExceptionThrown() {

        Data data = TestUtils.getSimpleIrisMamdaniData();
        KnowledgeBaseType knowledgeBase = TestUtils.getSimpleIrisMamdaniKnowledgeBase();
        MethodConfig methodConfig = TestUtils.getSimpleMethodConfig();
        JFMLRandom.createObject(42);

        new FURIA().trainRuleBase(data, knowledgeBase, methodConfig);
    }
}
