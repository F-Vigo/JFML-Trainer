package jfmltrainer.task.graphics.panel;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.membershipfunction.CircularDefinitionType;
import jfml.membershipfunction.CustomShapeType;
import jfml.membershipfunction.PointSetShapeType;
import jfml.parameter.FourParamType;
import jfml.parameter.OneParamType;
import jfml.parameter.ThreeParamType;
import jfml.parameter.TwoParamType;
import jfml.term.FuzzyTermType;
import jfmltrainer.task.graphics.GraphicalConstants;

import java.awt.*;
import java.util.Optional;
import java.util.function.Function;

public class KnowledgeBaseJPanel extends JFMLTrainerJPanel<KnowledgeBaseType> {

    public KnowledgeBaseJPanel(KnowledgeBaseType knowledgeBase, Optional<KnowledgeBaseType> newKnowledgeBase) {
        super(knowledgeBase, newKnowledgeBase);
    }
    @Override
    protected String getImageName() {
        return "knowledgeBase";
    }

    @Override
    protected void paintBase(Graphics2D g) {
        g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        for (int i = 0; i < base.getKnowledgeBaseVariables().size(); i++) {
            int finalI = i;
            paintVariable(
                    g,
                    base.getKnowledgeBaseVariables().get(i),
                    newBase.map(x -> x.getKnowledgeBaseVariables().get(finalI)),
                    i
            );
        }
    }

    private void paintVariable(Graphics2D g, KnowledgeBaseVariable oldVar, Optional<KnowledgeBaseVariable> newVar, int i) {
        String fullVarName = getFullVarName(oldVar, i);
        g.drawString(fullVarName, GraphicalConstants.MARGIN, 2*GraphicalConstants.MARGIN + i*GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT);
        if (isSingleton(oldVar)) {
            Integer width = (int) Math.floor(GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH * 0.8);
            Integer height = (int) Math.floor(GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT * 0.8);
            Integer paddingH = (GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH  - width) / 2;
            Integer left = GraphicalConstants.MARGIN + GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH + paddingH - 10;
            Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT;
            Function<Float, Integer> mfValueToRow = mfValue -> (int) Math.floor(mfValue*height);
            Function<Integer, Integer> columnToPixelX = column -> left + column;
            Function<Integer, Integer> rowToPixelY = row -> top - row;

            paintSingleton(g, oldVar, width, mfValueToRow, columnToPixelX, rowToPixelY);
        } else {
            paintVariableTermList(g, oldVar, newVar, i);
        }
    }

    private void paintVariableTermList(Graphics2D g, KnowledgeBaseVariable oldVar, Optional<KnowledgeBaseVariable> newVar, int i) {

        Integer TIMES = 1;

        Integer width = (int) Math.floor(GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH * 0.8) * TIMES;
        Integer height = (int) Math.floor(GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT * 0.8);
        Integer paddingH = (GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH * TIMES - width) / 2;
        Float min = ((FuzzyVariableType) oldVar).getDomainleft();
        Float max = ((FuzzyVariableType) oldVar).getDomainright();

        Integer offset = 10;

        Integer left = GraphicalConstants.MARGIN + GraphicalConstants.KNOWLEDGE_BLOCK_WIDTH + paddingH - offset;
        Integer top = 2*GraphicalConstants.MARGIN + i*GraphicalConstants.KNOWLEDGE_BLOCK_HEIGHT;

        g.drawLine(left, top, left + width, top);

        Function<Integer, Float> columnToX = column -> (max-min)/(width-1)*column + min;
        Function<Float, Integer> mfValueToRow = mfValue -> (int) Math.floor(mfValue*height);
        Function<Integer, Integer> columnToPixelX = column -> left + column;
        Function<Integer, Integer> rowToPixelY = row -> top - row;

        for (int termIndex = 0; termIndex < oldVar.getTerms().size(); termIndex++) {
            if (newVar.isEmpty()) {
                paintTerm(g, (FuzzyTermType) oldVar.getTerms().get(termIndex), width, columnToX, mfValueToRow, columnToPixelX, rowToPixelY);
            } else {
                g.setColor(new Color(255, 0, 0, 50));
                paintTerm(g, (FuzzyTermType) oldVar.getTerms().get(termIndex), width, columnToX, mfValueToRow, columnToPixelX, rowToPixelY);
                g.setColor(new Color(0, 0, 255, 50));
                paintTerm(g, (FuzzyTermType) newVar.get().getTerms().get(termIndex), width, columnToX, mfValueToRow, columnToPixelX, rowToPixelY);
                g.setColor(Color.BLACK);
            }
        }
    }

    private void paintTerm(Graphics2D g, FuzzyTermType term, Integer width, Function<Integer, Float> columnToX, Function<Float, Integer> mfValueToRow, Function<Integer, Integer> columnToPixelX, Function<Integer, Integer> rowToPixelY) {
        for (int column = 0; column < width; column++) {
            Float x = columnToX.apply(column);
            Float mfValue = term.getMembershipValue(x);
            Integer row = mfValueToRow.apply(mfValue);
            drawPoint(g, columnToPixelX.apply(column), rowToPixelY.apply(row));
        }
    }

    private void paintSingleton(Graphics2D g, KnowledgeBaseVariable variable, Integer width, Function<Float, Integer> mfValueToRow, Function<Integer, Integer> columnToPixelX, Function<Integer, Integer> rowToPixelY) {

        Integer n = variable.getTerms().size();
        Float h = width / (float) (n-1);

        g.drawLine(
                columnToPixelX.apply(0),
                rowToPixelY.apply(mfValueToRow.apply(0F)),
                columnToPixelX.apply(width),
                rowToPixelY.apply(mfValueToRow.apply(0F))
        );

        for (int i = 0; i < n; i++) {
            Integer column = (int) (i*h);
            g.drawLine(
                    columnToPixelX.apply(column),
                    rowToPixelY.apply(mfValueToRow.apply(0F)),
                    columnToPixelX.apply(column),
                    rowToPixelY.apply(mfValueToRow.apply(1F))
            );
        }
    }



    private String getFullVarName(KnowledgeBaseVariable variable, int i) {
        return variable.isInput()
                ? variable.getName() + " (X" + (i+1) + ")"
                : variable.getName() + " (Y)";
    }



    private Boolean isSingleton(KnowledgeBaseVariable variable) {
        FuzzyTermType term = (FuzzyTermType) variable.getTerms().get(0);
        return term.getRightLinearShape() == null
                && term.getLeftLinearShape() == null
                && term.getPiShape() == null
                && term.getTriangularShape() == null
                && term.getGaussianShape() == null
                && term.getRightGaussianShape() == null
                && term.getLeftGaussianShape() == null
                && term.getTrapezoidShape() == null
                && term.getRectangularShape() == null
                && term.getZShape() == null
                && term.getSShape() == null
                && term.getPointSetShape() == null
                && term.getCircularDefinition() == null;
    }
}
