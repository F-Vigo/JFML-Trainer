package jfmltrainer.fileparser.data;

import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.fileparser.FileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public abstract class DataParser<T extends Instance> implements FileParser<Data<T>> {

    @Override
    public Data read(String filePath) {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<T> instanceList = new ArrayList<>(Collections.emptyList());
        while(scanner.hasNextLine()) {

            List<String> valueList = List.of(scanner.nextLine().split(";")); // Length equals 2 (input, output).

            List<Float> antecedentValueList = List.of(valueList.get(0).split(",")).stream()
                    .mapToDouble(Double::parseDouble)
                    .boxed()
                    .map(Double::floatValue)
                    .collect(Collectors.toList());

            List<String> consequentValueListAsString = List.of(valueList.get(1).split(","));

            instanceList.add(getInstanceFromValues(antecedentValueList, consequentValueListAsString));
        }

        return new Data<>(instanceList);
    }

    protected abstract T getInstanceFromValues(List<Float> antecedentValueList, List<String> consequentValueList);

}
