package jfmltrainer.task.metrics.writer;

import jfmltrainer.task.metrics.measure.ClassificationMeasures;

public class MetricsWriterClassification extends MetricsWriter<ClassificationMeasures> {

    @Override
    protected void addVariableInfo(ClassificationMeasures measures, StringBuffer stringBuffer) {
        addHeader(stringBuffer);
        Integer n = measures.getAccuracyList().size();
        for (int i = 0; i < n; i++) {
            addRow(measures, i, stringBuffer);
        }
    }

    private void addHeader(StringBuffer stringBuffer) {
        stringBuffer.append(String.format(
                "%10s | %10s | %10s | %10s | %10s | %10s \n",
                "Positive",
                "Accuracy",
                "Precision",
                "Sensitivity",
                "Specificity",
                "F1"
        ));
    }

    private void addRow(ClassificationMeasures measures, int i, StringBuffer stringBuffer) {
        stringBuffer.append(String.format(
                "%10s | %10d | %10d | %10d | %10d | %10d \n",
                measures.getAccuracyList().get(0).getPositiveClassName(),
                measures.getPrecisionList().get(0).getValue(),
                measures.getSensitivityList().get(0).getValue(),
                measures.getSpecificityList().get(0).getValue(),
                measures.getSensitivityList().get(0).getValue(),
                measures.getF1List().get(0).getValue()
        ));
    }

}
