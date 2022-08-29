package jfmltrainer.task.metrics;

import jfml.FuzzyInferenceSystem;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.fileparser.frbs.FISParser;
import jfmltrainer.task.metrics.computer.VariableMetricsComputerClassification;
import jfmltrainer.task.metrics.computer.VariableMetricsComputerRegression;
import jfmltrainer.task.metrics.measure.ClassificationMeasures;
import jfmltrainer.task.metrics.measure.Measures;
import jfmltrainer.task.metrics.measure.RegressionMeasures;
import jfmltrainer.task.metrics.writer.MetricsWriterClassification;
import jfmltrainer.task.metrics.writer.MetricsWriterRegression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Metrics {

    public static void computeMetrics(Args args) {

        FuzzyInferenceSystem frbs = FISParser.getInstance().read(args.getFrbsPath().get());
        Data testSet = null;
        try {
            testSet = RegressionDataParser.getInstance().read(args.getDataPath().get());
        } catch (Exception e) {
            testSet = ClassificationDataParser.getInstance().read(args.getDataPath().get());
        }

        try {
            computeMetrics(frbs, testSet);
        } catch (Exception e) {}
    }

    public static void computeMetrics(FuzzyInferenceSystem frbs, Data testSet) throws IOException {

        List<Measures> measuresList = new ArrayList<>();
        Boolean isRegression = isRegression(testSet);

        measuresList.addAll(
                isRegression
                ? new VariableMetricsComputerRegression().computeMetrics(frbs, testSet)
                : new VariableMetricsComputerClassification().computeMetrics(frbs, testSet)
        );

        if (isRegression) {
            new MetricsWriterRegression().write(measuresList.stream().map(metrics -> (RegressionMeasures) metrics).collect(Collectors.toList()));
        } else {
            new MetricsWriterClassification().write(measuresList.stream().map(metrics -> (ClassificationMeasures) metrics).collect(Collectors.toList()));
        }
    }

    private static boolean isRegression(Data testSet) {
        return testSet.getInstanceList().get(0) instanceof RegressionInstance;
    }


}
