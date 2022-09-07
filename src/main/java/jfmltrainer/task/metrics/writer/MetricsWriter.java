package jfmltrainer.task.metrics.writer;

import jfmltrainer.task.metrics.measure.Measures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public abstract class MetricsWriter<T extends Measures> {

    public void write(List<T> measuresList) throws IOException {
        StringBuffer stringBuffer = buildStringBuffer(measuresList);
        String instantString = "metrics/" + Instant.now().toString();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(instantString + ".txt")));
        bufferedWriter.write(stringBuffer.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private StringBuffer buildStringBuffer(List<T> measuresList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (T measures : measuresList) {
            addVariableToBuffer(measures, stringBuffer);
        }
        return stringBuffer;
    }

    private void addVariableToBuffer(T measures, StringBuffer stringBuffer) {
        addVariableName(measures.varName, stringBuffer);
        addVariableInfo(measures, stringBuffer);
        addVariableClosing(stringBuffer);
    }


    private void addVariableName(String varName, StringBuffer stringBuffer) {
        stringBuffer.append("Variable name: " + varName);
        stringBuffer.append("\n");
    }

    protected abstract void addVariableInfo(T measures, StringBuffer stringBuffer);


    private void addVariableClosing(StringBuffer stringBuffer) {
        stringBuffer.append("\n");
        stringBuffer.append("==========");
        stringBuffer.append("\n");
    }
}
