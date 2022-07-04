package jfmltrainer.graphics.panel;

import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.ClauseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.graphics.GraphicalConstants;

import java.awt.*;

public class RuleBaseJPanel extends JFMLTrainerJPanel<RuleBaseType> {

    public RuleBaseJPanel(RuleBaseType base) {
        super(base);
    }

    @Override
    protected String getImageName() {
        return "ruleBase.png";
    }

    @Override
    protected void paintBase(Graphics2D g) {
        g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        for (int i = 0; i < base.getRules().size(); i++) {
            paintRule(g, base.getRules().get(i), i);
        }
    }

    private void paintRule(Graphics2D g, FuzzyRuleType fuzzyRuleType, int i) {
        paintIf(g, i);
        Integer antecedentSize = fuzzyRuleType.getAntecedent().getClauses().size();
        for (int j = 0; j < antecedentSize; j++) {
            paintVariable(g, fuzzyRuleType.getAntecedent().getClauses().get(j), i, j, antecedentSize);
        }
        paintThenClause(g, i, fuzzyRuleType);
    }

    private void paintIf(Graphics2D g, int i) {
        g.drawString("R" + (i+1) + ": IF", GraphicalConstants.MARGIN, 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT);
    }

    private void paintVariable(Graphics2D g, ClauseType clause, int i, int j, int antecedentSize) {
        String varName = "X"+(j+1);
        Integer startingLeft = GraphicalConstants.MARGIN + (3*j+1)*GraphicalConstants.RULE_BLOCK_WIDTH;
        Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT;
        g.drawString(varName + " IS", startingLeft, top);
        paintFuzzyTerm(g, clause, startingLeft + GraphicalConstants.RULE_BLOCK_WIDTH, top);
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

    private void paintThenClause(Graphics2D g, int i, FuzzyRuleType rule) {
        // TODO - Add little box with black depending on weight
        Integer n = rule.getAntecedent().getClauses().size();
        Integer startingLeft = GraphicalConstants.MARGIN + (1+n+n+(n-1))*GraphicalConstants.RULE_BLOCK_WIDTH;
        Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.RULE_BLOCK_HEIGHT;
        g.drawString("THEN", startingLeft, top);
        g.drawString("Y IS", startingLeft+GraphicalConstants.RULE_BLOCK_WIDTH, top);

        FuzzyTermType term = (FuzzyTermType) rule.getConsequent().getThen().getClause().get(0).getTerm();
        Boolean isRegression = term.getSingletonShape() != null;
        if (isRegression) {
            paintFuzzyTerm(g, rule.getConsequent().getThen().getClause().get(0), startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH, top);
        } else {
            g.drawString(term.getName(), startingLeft + 2*GraphicalConstants.RULE_BLOCK_WIDTH - 10, top);
        }

        int leftRect = startingLeft + 3*GraphicalConstants.RULE_BLOCK_WIDTH + (int) (Math.floor(0.5 * GraphicalConstants.RULE_BLOCK_WIDTH));
        int topRect = top-GraphicalConstants.RULE_BLOCK_HEIGHT + (int) (Math.floor(0.5 * GraphicalConstants.RULE_BLOCK_HEIGHT));
        int rectSide = 10;
        g.drawRect(leftRect, topRect, rectSide, rectSide);
        int luminosity = (int) Math.floor((1- rule.getWeight()) * 255);
        g.setColor(new Color(luminosity, luminosity, luminosity));
        g.fillRect(leftRect, topRect, rectSide, rectSide);
        g.setColor(Color.BLACK);

    }



}
