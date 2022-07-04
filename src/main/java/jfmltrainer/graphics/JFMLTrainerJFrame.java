package jfmltrainer.graphics;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.graphics.panel.KnowledgeBaseJPanel;
import jfmltrainer.graphics.panel.RuleBaseJPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

class JFMLTrainerJFrame extends JFrame {

    private KnowledgeBaseType knowledgeBase;
    private RuleBaseType ruleBase;


    JFMLTrainerJFrame(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        this.knowledgeBase = knowledgeBase;
        this.ruleBase = ruleBase;
        init();
    }

    private void init() {

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        setSize(getWidthFromKnowledgeBase(knowledgeBase), getHeightFromKnowledgeBase(knowledgeBase));
        KnowledgeBaseJPanel knowledgeBaseJPanel = new KnowledgeBaseJPanel(knowledgeBase);
        knowledgeBaseJPanel.setSize(this.getWidth(), this.getHeight());
        this.add(knowledgeBaseJPanel);
        knowledgeBaseJPanel.saveImage();
        waitFor(1000);
        this.remove(knowledgeBaseJPanel);

        setSize(getWidthFromRuleBase(ruleBase), getHeightFromRuleBase(ruleBase));
        RuleBaseJPanel ruleBaseJPanel = new RuleBaseJPanel(ruleBase);
        ruleBaseJPanel.setSize(this.getWidth(), this.getHeight());
        this.add(ruleBaseJPanel);
        ruleBaseJPanel.saveImage();
        waitFor(1000);
        this.remove(ruleBaseJPanel);

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    private Integer getWidthFromKnowledgeBase(KnowledgeBaseType knowledgeBase) {
        return 2 * GraphicalConstants.MARGIN + 2 * GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH;
    }

    private Integer getHeightFromKnowledgeBase(KnowledgeBaseType knowledgeBase) {
        return 3 * GraphicalConstants.MARGIN + knowledgeBase.getKnowledgeBaseVariables().size() * GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT;
    }

    private Integer getWidthFromRuleBase(RuleBaseType ruleBase) {
        FuzzyRuleType rule = ruleBase.getRules().get(0);
        Integer n = rule.getAntecedent().getClauses().size();
        return (n + n + (n - 1) + 5) * GraphicalConstants.RULE_BLOCK_WIDTH + 2 * GraphicalConstants.MARGIN;
    }

    private Integer getHeightFromRuleBase(RuleBaseType ruleBase) {
        return GraphicalConstants.RULE_BLOCK_HEIGHT * ruleBase.getRules().size() + 3 * GraphicalConstants.MARGIN;
    }

    private void waitFor(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}