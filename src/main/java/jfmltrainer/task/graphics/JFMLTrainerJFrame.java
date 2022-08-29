package jfmltrainer.task.graphics;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.task.graphics.panel.KnowledgeBaseJPanel;
import jfmltrainer.task.graphics.panel.RuleBaseJPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

class JFMLTrainerJFrame extends JFrame {

    private KnowledgeBaseType knowledgeBase;
    private Optional<KnowledgeBaseType> newKnowledgeBase;
    private RuleBaseType ruleBase;
    private Optional<RuleBaseType> newRuleBase;


    JFMLTrainerJFrame(KnowledgeBaseType knowledgeBase, Optional<KnowledgeBaseType> newKnowledgeBase, RuleBaseType ruleBase, Optional<RuleBaseType> newRuleBase) {
        this.knowledgeBase = knowledgeBase;
        this.newKnowledgeBase = newKnowledgeBase;
        this.ruleBase = ruleBase;
        this.newRuleBase = newRuleBase;
        run();
    }


    private void run() {

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        String instant = Instant.now().toString();

        setSize(getWidthFromKnowledgeBase(knowledgeBase), getHeightFromKnowledgeBase(knowledgeBase));
        KnowledgeBaseJPanel knowledgeBaseJPanel = new KnowledgeBaseJPanel(knowledgeBase, newKnowledgeBase);
        knowledgeBaseJPanel.setSize(this.getWidth(), this.getHeight());
        this.add(knowledgeBaseJPanel);
        knowledgeBaseJPanel.saveImage(instant);
        waitFor(500);
        this.remove(knowledgeBaseJPanel);

        setSize(getWidthFromRuleBase(ruleBase), getHeightFromRuleBase(ruleBase));
        RuleBaseJPanel ruleBaseJPanel = new RuleBaseJPanel(ruleBase, newRuleBase);
        ruleBaseJPanel.setSize(this.getWidth(), this.getHeight());
        this.add(ruleBaseJPanel);
        ruleBaseJPanel.saveImage(instant);
        waitFor(500);
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
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}