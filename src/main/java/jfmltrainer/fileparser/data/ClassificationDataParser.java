package jfmltrainer.fileparser.data;

import jfmltrainer.data.instance.ClassificationInstance;

import java.util.List;

public class ClassificationDataParser extends DataParser<ClassificationInstance> {

    @Override
    protected ClassificationInstance getInstanceFromValues(List<Float> antecedentValueList, List<String> consequentValueList) {
        return new ClassificationInstance(antecedentValueList, consequentValueList);
    }
}
