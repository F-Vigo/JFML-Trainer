package jfmltrainer.task.metrics.measure;

import lombok.Value;

import java.util.List;

@Value
public class ClassificationMeasures extends Measures {
    List<ClassificationMeasureValue> accuracyList;
    List<ClassificationMeasureValue> precisionList;
    List<ClassificationMeasureValue> sensitivityList;
    List<ClassificationMeasureValue> specificityList;
    List<ClassificationMeasureValue> F1List;
    // List<ClassificationMeasureValue> AUCList; // TODO - Cannot be done as it is

    public ClassificationMeasures(
            String varName, List<ClassificationMeasureValue> accuracyList,
            List<ClassificationMeasureValue> precisionList,
            List<ClassificationMeasureValue> sensitivityList,
            List<ClassificationMeasureValue> specificityList,
            List<ClassificationMeasureValue> F1List
    ) {
        this.varName = varName;
        this.accuracyList = accuracyList;
        this.precisionList = precisionList;
        this.sensitivityList = sensitivityList;
        this.specificityList = specificityList;
        this.F1List = F1List;
    }
}
