package jfmltrainer.fileparser.data;

import jfmltrainer.data.instance.ClassificationInstance;

import java.util.List;

public class ClassificationDataParser extends DataParser<ClassificationInstance> {

    private static ClassificationDataParser instance = new ClassificationDataParser();

    private ClassificationDataParser(){}

    public static ClassificationDataParser getInstance() {
        return instance;
    }

    @Override
    protected ClassificationInstance getInstanceFromValues(List<Float> antecedentValueList, List<String> consequentValueList) {
        return new ClassificationInstance(antecedentValueList, consequentValueList);
    }
}
