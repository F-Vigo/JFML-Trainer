package jfmltrainer.task.graphics.panel;

import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.ClauseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.task.graphics.GraphicalConstants;

import java.awt.*;
import java.util.Optional;


public class RuleBaseJPanel extends JFMLTrainerJPanel<RuleBaseType> {

    public RuleBaseJPanel(RuleBaseType ruleBase, Optional<RuleBaseType> newRuleBase) {
        super(ruleBase, newRuleBase);
    }

    @Override
    protected String getImageName() {
        return newBase.isEmpty()
                ? "ruleBase"
                : "ruleBase_comparison";
    }

    @Override
    protected void paintBase(Graphics2D g) {
        g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        for (int i = 0; i < base.getRules().size(); i++) {
            paintRule(g, i);
        }
    }

    private void paintRule(Graphics2D g, int i) {

        Boolean oneBase = newBase.isEmpty();

        FuzzyRuleType oldRule = base.getRules().get(i);
        Optional<FuzzyRuleType> newRule = newBase.map(x -> x.getRules().get(i));

        paintIf(g, i);
        Integer antecedentSize = oldRule.getAntecedent().getClauses().size();
        for (int j = 0; j < antecedentSize; j++) {
            paintVariable(g, oldRule, newRule, i, j, antecedentSize);
        }
        paintThenClause(g, i, oldRule, newRule);
    }

    private void paintIf(Graphics2D g, int i) {
        g.drawString("R" + (i+1) + ": IF", GraphicalConstants.MARGIN, 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT);
    }

    private void paintVariable(Graphics2D g, FuzzyRuleType oldRule, Optional<FuzzyRuleType> newRule, int i, int j, int antecedentSize) {
        Boolean oneBase = newRule.isEmpty();
        String varName = "X"+(j+1);
        Integer startingLeft = GraphicalConstants.MARGIN + (3*j+1)*GraphicalConstants.RULE_BLOCK_WIDTH;
        Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT;
        g.drawString(varName + " IS", startingLeft, top);
        if(oneBase) {
            paintFuzzyTerm(g, oldRule.getAntecedent().getClauses().get(j), startingLeft + GraphicalConstants.RULE_BLOCK_WIDTH, top);
        } else {
            g.setColor(new Color(255, 0, 0, 50));
            paintFuzzyTerm(g, oldRule.getAntecedent().getClauses().get(j), startingLeft + GraphicalConstants.RULE_BLOCK_WIDTH, top);
            g.setColor(new Color(0, 0, 255, 50));
            paintFuzzyTerm(g, newRule.get().getAntecedent().getClauses().get(j), startingLeft + GraphicalConstants.RULE_BLOCK_WIDTH, top);
            g.setColor(Color.BLACK);
        }

        if (j < antecedentSize-1) {
            g.drawString("AND", startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH, top);
        }
    }


    private void paintFuzzyTerm(Graphics2D g, ClauseType clause, Integer left, Integer top) {
        Integer width = (int) Math.floor(GraphicalConstants.RULE_BLOCK_WIDTH * 0.8);
        Integer height = (int) Math.floor(GraphicalConstants.RULE_BLOCK_HEIGHT * 0.8);
        Integer paddingH = (GraphicalConstants.RULE_BLOCK_WIDTH - width) / 2;
        Float min = ((FuzzyVariableType) clause.getVariable()).getDomainleft();
        Float max = ((FuzzyVariableType) clause.getVariable()).getDomainright();

        Integer offset = 10;

        g.drawLine(left + paddingH - offset, top, left + paddingH + width - offset, top);
        for (int column = 0; column < width; column++) {
            Float x = (max-min)/(width-1)*column + min;
            Float mfValue = ((FuzzyTermType) clause.getTerm()).getMembershipValue(x);
            for (int row = 0; row < height - 1; row++) {
                Float y = (float) row/height;
                if (y <= mfValue) {
                    drawPoint(g, left + paddingH + column - offset, top-row);
                }
            }
        }
    }

    private void paintThenClause(Graphics2D g, int i, FuzzyRuleType oldRule, Optional<FuzzyRuleType> newRule) {

        Boolean oneBase = newRule.isEmpty();

        Integer n = oldRule.getAntecedent().getClauses().size();
        Integer startingLeft = GraphicalConstants.MARGIN + (1+n+n+(n-1))*GraphicalConstants.RULE_BLOCK_WIDTH;
        Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT;
        g.drawString("THEN", startingLeft, top);
        g.drawString("Y IS", startingLeft+GraphicalConstants.RULE_BLOCK_WIDTH, top);

        FuzzyTermType oldTerm = (FuzzyTermType) oldRule.getConsequent().getThen().getClause().get(0).getTerm();
        Boolean isRegression = oldTerm.getSingletonShape() != null;

        if (isRegression) {
            if (oneBase) {
                paintFuzzyTerm(g, oldRule.getConsequent().getThen().getClause().get(0), startingLeft + 2 * GraphicalConstants.RULE_BLOCK_WIDTH, top);
            } else {
                g.setColor(new Color(255, 0, 0, 50));
                paintFuzzyTerm(g, oldRule.getConsequent().getThen().getClause().get(0), startingLeft + 2 * GraphicalConstants.RULE_BLOCK_WIDTH, top);
                g.setColor(new Color(0, 0, 255, 50));
                paintFuzzyTerm(g, newRule.get().getConsequent().getThen().getClause().get(0), startingLeft + 2 * GraphicalConstants.RULE_BLOCK_WIDTH, top);
                g.setColor(Color.BLACK);
            }

        } else {
            if (oneBase) {
                g.drawString(oldTerm.getName(), startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH - 10, top);
            } else {
                g.setColor(new Color(255, 0, 0, 50));
                g.drawString(oldTerm.getName(), startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH - 10, top);
                g.setColor(new Color(0, 0, 255, 50));
                g.drawString(
                        ((FuzzyTermType) newRule.get().getConsequent().getThen().getClause().get(0).getTerm()).getName(),
                        startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH - 10,
                        top
                );
                g.setColor(Color.BLACK);
            }
        }

        int leftRect = startingLeft + 3*GraphicalConstants.RULE_BLOCK_WIDTH + (int) (Math.floor(0.5 * GraphicalConstants.RULE_BLOCK_WIDTH));
        int topRect = top-GraphicalConstants.RULE_BLOCK_HEIGHT + (int) (Math.floor(0.5 * GraphicalConstants.RULE_BLOCK_HEIGHT));
        int rectSide = 10;
        g.drawRect(leftRect, topRect, rectSide, rectSide);
        int luminosity = (int) Math.floor((1-(oneBase ? oldRule : newRule.get()).getWeight()) * 255);
        g.setColor(new Color(luminosity, luminosity, luminosity));
        g.fillRect(leftRect, topRect, rectSide, rectSide);
        g.setColor(Color.BLACK);

    }



}
