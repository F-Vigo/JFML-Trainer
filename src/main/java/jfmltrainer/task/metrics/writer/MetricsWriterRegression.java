package jfmltrainer.task.metrics.writer;

import jfmltrainer.task.metrics.measure.RegressionMeasures;

public class MetricsWriterRegression extends MetricsWriter<RegressionMeasures> {

    @Override
    protected void addVariableInfo(RegressionMeasures measures, StringBuffer stringBuffer) {
        stringBuffer.append("MSE: " + measures.getMSE() + "\n");
        stringBuffer.append("RMSE: " + measures.getRMSE() + "\n");
        stringBuffer.append("MAE: " + measures.getMAE() + "\n");
        stringBuffer.append("GMSE: " + measures.getGMSE() + "\n");
    }
}
