package jfmltrainer.graphics;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;

public class JFMLTrainerGraphics {

    public static void drawAndSaveImage(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        new JFMLTrainerJFrame(knowledgeBase, ruleBase);
    }
}
