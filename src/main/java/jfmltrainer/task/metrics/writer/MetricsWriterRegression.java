package jfmltrainer.task.metrics.writer;

import jfmltrainer.task.metrics.measure.RegressionMeasures;

public class MetricsWriterRegression extends MetricsWriter<RegressionMeasures> {

    @Override
    protected void addVariableInfo(RegressionMeasures measures, StringBuffer stringBuffer) {
        stringBuffer.append("MSE: " + measures.getMSE());
        stringBuffer.append("RMSE: " + measures.getRMSE());
        stringBuffer.append("MAE: " + measures.getMAE());
        stringBuffer.append("GMSE: " + measures.getGMSE());
    }
}
