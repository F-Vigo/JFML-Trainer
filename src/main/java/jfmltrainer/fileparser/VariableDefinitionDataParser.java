package jfmltrainer.fileparser;

import jfmltrainer.task.knowledgebasebuilder.variabledefinition.QualitativeVariableDefinitionData;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.QuantitativeVariableDefinitionData;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.VariableDefinitionData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VariableDefinitionDataParser implements FileParser<List<VariableDefinitionData>> {

    private static VariableDefinitionDataParser instance = new VariableDefinitionDataParser();

    private VariableDefinitionDataParser(){}

    public static VariableDefinitionDataParser getInstance() {
        return instance;
    }

    @Override
    public List<VariableDefinitionData> read(String filePath) {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<VariableDefinitionData> variableDefinitionDataList = new ArrayList<>();
        while(scanner.hasNextLine()) {
            String row = scanner.nextLine();
            variableDefinitionDataList.add(buildVariableDefinitionData(row));
        }

        scanner.close();
        return variableDefinitionDataList;
    }

    private VariableDefinitionData buildVariableDefinitionData(String row) {

        List<String> valueList = List.of(row.split(","));
        String name = valueList.get(0);
        Boolean isInput = Integer.parseInt(valueList.get(1)) == 1;
        Boolean isQuantitative = Integer.parseInt(valueList.get(2)) == 1;
        List<String> otherValueList = valueList.subList(3, valueList.size());

        return isQuantitative
                ? buildQuantitativeVariableDefinitionData(name, isInput, otherValueList)
                : buildQualitativeVariableDefinitionData(name, isInput, otherValueList);
    }

    private QuantitativeVariableDefinitionData buildQuantitativeVariableDefinitionData(String name, Boolean isInput, List<String> otherValueList) {
        Float minValue = Float.parseFloat(otherValueList.get(0));
        Float maxValue = Float.parseFloat(otherValueList.get(1));
        Integer granularity = Integer.parseInt(otherValueList.get(2));
        return new QuantitativeVariableDefinitionData(name, isInput, minValue, maxValue, granularity);
    }

    private QualitativeVariableDefinitionData buildQualitativeVariableDefinitionData(String name, Boolean isInput, List<String> otherValueList) {
        return new QualitativeVariableDefinitionData(name, isInput, otherValueList);
    }

}
