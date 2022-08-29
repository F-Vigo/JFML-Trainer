package jfmltrainer.task.knowledgebasebuilder;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.term.FuzzyTermType;
import jfmltrainer.args.Args;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.fileparser.VariableDefinitionDataParser;
import jfmltrainer.fileparser.data.ClassificationDataParser;
import jfmltrainer.fileparser.data.RegressionDataParser;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.QualitativeVariableDefinitionData;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.QuantitativeVariableDefinitionData;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.VariableDefinitionData;

import javax.xml.bind.JAXBContext;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KnowledgeBaseBuilder {

    private static Integer DEFAULT_GRANULARITY = 5;

    public static KnowledgeBaseType build(Args args) {

        Optional<List<VariableDefinitionData>> variableDefinitionDataList = args.getVariableDefinitionDataPath().map(x -> VariableDefinitionDataParser.getInstance().read(x));
        Optional<Data> instanceData = args.getDataPath().map(KnowledgeBaseBuilder::getInstanceData);
        Optional<List<Integer>> granularityList = args.getGranularityList();
        Optional<Integer> singleGranularity = args.getSingleGranularity();

        KnowledgeBaseType knowledgeBase = build(variableDefinitionDataList, instanceData, granularityList, singleGranularity);
        exportKnowledgeBase(knowledgeBase, args.getOutputFolder().get(), args.getOutputName().get());
        return knowledgeBase;
    }

    private static Data getInstanceData(String dataPath) {
        try {
            return RegressionDataParser.getInstance().read(dataPath);
        } catch (Exception e1) {
            try {
                return ClassificationDataParser.getInstance().read(dataPath);
            } catch (Exception e2) {}
        } finally {
            return null;
        }
    }

    public static KnowledgeBaseType build(
            Optional<List<VariableDefinitionData>> variableDefinitionDataList,
            Optional<Data> instanceData,
            Optional<List<Integer>> granularityList,
            Optional<Integer> singleGranularity
    ) {
        List<KnowledgeBaseVariable> variableList = variableDefinitionDataList.isPresent()
                ? buildFromVariableDefinitionData(variableDefinitionDataList.get())
                : buildFromInstanceData(instanceData.get(), granularityList, singleGranularity);
        return buildKBFromVariableList(variableList);
    }



    // FROM VARIABLE DEFINITION DATA

    private static List<KnowledgeBaseVariable> buildFromVariableDefinitionData(List<VariableDefinitionData> variableDefinitionData) {
        return variableDefinitionData.stream()
                .map(KnowledgeBaseBuilder::buildVariable)
                .collect(Collectors.toList());
    }

    private static KnowledgeBaseVariable buildVariable(VariableDefinitionData variableDefinition) {
        return variableDefinition.getIsQuantitative()
                ? buildQuantitativeVariable((QuantitativeVariableDefinitionData) variableDefinition)
                : buildQualitativeVariable((QualitativeVariableDefinitionData) variableDefinition);
    }

    private static KnowledgeBaseVariable buildQuantitativeVariable(QuantitativeVariableDefinitionData variableDefinition) {
        FuzzyVariableType variable = new FuzzyVariableType(variableDefinition.getName(), variableDefinition.getMinValue(), variableDefinition.getMaxValue());
        variable.setType(variableDefinition.getIsInput() ? "input" : "output");
        List<FuzzyTermType> fuzzyTermList = buildTriangularTermList(variableDefinition.getGranularity(), variableDefinition.getMinValue(), variableDefinition.getMaxValue());
        fuzzyTermList.forEach(variable::addFuzzyTerm);
        return variable;
    }

    private static KnowledgeBaseVariable buildQualitativeVariable(QualitativeVariableDefinitionData variableDefinition) {
        FuzzyVariableType variable = new FuzzyVariableType(variableDefinition.getName(), 0, variableDefinition.getLabelList().size()-1);
        variable.setType(variableDefinition.getIsInput() ? "input" : "output");
        List<FuzzyTermType> fuzzyTermList = buildSingletonTermList(variableDefinition.getLabelList());
        fuzzyTermList.forEach(variable::addFuzzyTerm);
        return variable;
    }



    // FROM INSTANCE DATA

    private static List<KnowledgeBaseVariable> buildFromInstanceData(Data instanceData, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity) {
        Integer antecedentSize = ((List<Instance>) instanceData.getInstanceList()).get(0).getAntecedentValueList().size();
        List<KnowledgeBaseVariable> antecedentVariableList = buildAntecedentVariableList(instanceData, granularityList, singleGranularity, antecedentSize);
        List<KnowledgeBaseVariable> consequentVariableList = buildConsequentVariableList(instanceData, granularityList, singleGranularity, antecedentSize);
        List<KnowledgeBaseVariable> variableList = new ArrayList<>();
        variableList.addAll(antecedentVariableList);
        variableList.addAll(consequentVariableList);
        return variableList;
    }

    private static List<KnowledgeBaseVariable> buildAntecedentVariableList(Data instanceData, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity, Integer antecedentSize) {
        List<KnowledgeBaseVariable> result = new ArrayList<>();
        for (int i = 0; i < antecedentSize; i++) {
            int finalI = i;
            List<Float> variableValueList = ((List<Instance>) instanceData.getInstanceList()).stream()
                    .map(instance -> (Float) instance.getAntecedentValueList().get(finalI))
                    .collect(Collectors.toList());
            Integer granularity = granularityList.isPresent()
                    ? granularityList.get().get(i)
                    : singleGranularity.orElseGet(() -> DEFAULT_GRANULARITY);
            result.add(buildQuantitativeVariableFromData(variableValueList, granularity, i, true));
        }
        return result;
    }

    private static KnowledgeBaseVariable buildQuantitativeVariableFromData(List<Float> variableValueList, Integer granularity, Integer i, Boolean isInput) {
        Float min = (float) variableValueList.stream().mapToDouble(x -> (double) x).min().getAsDouble();
        Float max = (float) variableValueList.stream().mapToDouble(x -> (double) x).max().getAsDouble();
        FuzzyVariableType variable = new FuzzyVariableType((isInput ? "X" : "Y") + (i+1), min, max);
        variable.setType(isInput ? "input" : "output");
        List<FuzzyTermType> fuzzyTermList = buildTriangularTermList(granularity, min, max);
        fuzzyTermList.forEach(variable::addFuzzyTerm);
        return variable;
    }

    private static List<KnowledgeBaseVariable> buildConsequentVariableList(Data instanceData, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity, Integer antecedentSize) {
        return ((Instance) instanceData.getInstanceList().get(0)) instanceof RegressionInstance
                ? buildConsequentRegressionVariableList(instanceData, granularityList, singleGranularity, antecedentSize)
                : buildConsequentClassificationVariableList(instanceData);
    }

    private static List<KnowledgeBaseVariable> buildConsequentRegressionVariableList(Data instanceData, Optional<List<Integer>> granularityList, Optional<Integer> singleGranularity, Integer antecedentSize) {
        List<KnowledgeBaseVariable> result = new ArrayList<>();
        Integer consequentSize = ((RegressionInstance) instanceData.getInstanceList().get(0)).getConsequentValueList().size();
        for (int i = 0; i < consequentSize; i++) {
            int finalI = i;
            List<Float> variableValueList = ((List<RegressionInstance>) instanceData.getInstanceList()).stream()
                    .map(instance -> instance.getConsequentValueList().get(finalI))
                    .collect(Collectors.toList());
            Integer granularity = granularityList.isPresent()
                    ? granularityList.get().subList(antecedentSize, granularityList.get().size()).get(i)
                    : singleGranularity.orElseGet(() -> DEFAULT_GRANULARITY);
            result.add(buildQuantitativeVariableFromData(variableValueList, granularity, i, false));
        }
        return result;
    }

    private static List<KnowledgeBaseVariable> buildConsequentClassificationVariableList(Data instanceData) {
        List<KnowledgeBaseVariable> result = new ArrayList<>();
        Integer consequentSize = ((ClassificationInstance) instanceData.getInstanceList().get(0)).getConsequentValueList().size();
        for (int i = 0; i < consequentSize; i++) {
            int finalI = i;
            List<String> variableValueList = ((List<ClassificationInstance>) instanceData.getInstanceList()).stream()
                    .map(instance -> instance.getConsequentValueList().get(finalI))
                    .distinct()
                    .collect(Collectors.toList());
            result.add(buildQualitativeVariableFromData(variableValueList, i));
        }
        return result;
    }

    private static KnowledgeBaseVariable buildQualitativeVariableFromData(List<String> labelList, Integer i) {

        FuzzyVariableType variable = new FuzzyVariableType("Y" + (i+1), 0, labelList.size());
        variable.setType("output");
        List<FuzzyTermType> fuzzyTermList = buildSingletonTermList(labelList);
        fuzzyTermList.forEach(variable::addFuzzyTerm);
        return variable;
    }



    // EXPORTING


    private static void exportKnowledgeBase(KnowledgeBaseType knowledgeBase, String outputFolder, String outputName) {
        String path = outputFolder + outputName + "xml";
        try {
            JAXBContext.newInstance(KnowledgeBaseType.class).createMarshaller().marshal(knowledgeBase, new FileOutputStream(path));
        } catch (Exception e) {}
    }



    // AUXILIARY FUNCTIONS

    private static List<FuzzyTermType> buildTriangularTermList(Integer granularity, Float minValue, Float maxValue) {
        List<FuzzyTermType> result = new ArrayList<>();
        Float halfTriangularBase = (maxValue - minValue) / (granularity - 1);
        result.add(buildTriangle(minValue, minValue, minValue+halfTriangularBase));
        for (int i = 0; i < granularity-2; i++) {
            Float left = minValue + i*halfTriangularBase;
            result.add(buildTriangle(left, left+halfTriangularBase, left+2*halfTriangularBase));
        }
        result.add(buildTriangle(maxValue-halfTriangularBase, maxValue, maxValue));
        return result;
    }


    private static List<FuzzyTermType> buildSingletonTermList(List<String> labelList) {
        return IntStream.range(0, labelList.size()).boxed()
                .map(i -> new FuzzyTermType(labelList.get(i), 8, new float[]{i}))
                .collect(Collectors.toList());
    }

    private static  FuzzyTermType buildTriangle(Float left, Float middle, Float right) {
        return new FuzzyTermType("", 3, new float[]{left, middle, right});
    }


    private static KnowledgeBaseType buildKBFromVariableList(List<KnowledgeBaseVariable> variableList) {
        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();
        variableList.forEach(knowledgeBase::addVariable);
        return knowledgeBase;
    }
}
