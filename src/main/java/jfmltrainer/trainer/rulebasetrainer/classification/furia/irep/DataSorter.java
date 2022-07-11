package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep;

import jfmltrainer.data.instance.ClassificationInstance;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataSorter {

    public static  List<String> getSortedLabelList(List<ClassificationInstance> instanceList) {
        List<String> labelList = instanceList.stream()
                .map(DataSorter::getLabel)
                .collect(Collectors.toList());

        List<ImmutablePair<String, Integer>> labelHistogram = labelList.stream()
                .map(label -> new ImmutablePair<>(label, countLabel(label, instanceList)))
                .collect(Collectors.toList());

        List<String> sortedLabelList = labelHistogram.stream()
                .sorted(Comparator.comparing(ImmutablePair::getRight))
                .map(ImmutablePair::getLeft)
                .collect(Collectors.toList());

        return sortedLabelList;
    }

    private static int countLabel(String label, List<ClassificationInstance> instanceList) {
        return (int) instanceList.stream()
                .filter(instance -> getLabel(instance).equals(label))
                .count();
    }

    private static String getLabel(ClassificationInstance instance) { // TODO
        return instance.getConsequentValueList().get(0);
    }
}
