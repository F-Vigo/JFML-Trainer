package jfmltrainer.fileparser.data;

import jfmltrainer.data.instance.RegressionInstance;

import java.util.List;
import java.util.stream.Collectors;

public class RegressionDataParser extends DataParser<RegressionInstance> {

    private static RegressionDataParser instance = new RegressionDataParser();

    private RegressionDataParser(){}

    public static RegressionDataParser getInstance() {
        return instance;
    }


    @Override
    protected RegressionInstance getInstanceFromValues(List<Float> antecedentValueList, List<String> consequentValueListAsString) {

        List<Float> consequentValueList = consequentValueListAsString.stream()
                .mapToDouble(Double::parseDouble)
                .boxed()
                .map(Double::floatValue)
                .collect(Collectors.toList());

        return new RegressionInstance(antecedentValueList, consequentValueList);

    }

}
