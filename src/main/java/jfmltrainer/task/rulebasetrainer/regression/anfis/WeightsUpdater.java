package jfmltrainer.task.rulebasetrainer.regression.anfis;

import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.rulebasetrainer.regression.anfis.layers.AnfisFirstLayer;
import jfmltrainer.task.rulebasetrainer.regression.anfis.layers.AnfisFourthLayer;
import jfmltrainer.task.rulebasetrainer.regression.anfis.layers.AnfisSecondLayer;
import jfmltrainer.task.rulebasetrainer.regression.anfis.layers.AnfisThirdLayer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeightsUpdater {

    private Float LEARNING_RATE = 0.01F; // TODO

    public Weights updateWeights(
            Weights weights,
            List<Float> iterationsPredictedValueList,
            Data data,
            Integer nRules,
            List<Integer> nTermList,
            AnfisFirstLayer firstLayer,
            AnfisSecondLayer secondLayer,
            AnfisThirdLayer thirdLayer,
            AnfisFourthLayer fourthLayer
    ) {
        Weights weightIncrement = getWeightIncrement(data, iterationsPredictedValueList, weights, nRules, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer);
        List<List<ImmutablePair<Float, Float>>> newSecondLayerWeights = updateSecondLayerWeights(weights.getSecondLayerWeights(), weightIncrement.getSecondLayerWeights());
        List<List<Float>> newFifthLayerWeighs = updateFifthLayerWeights(weights.getFifthLayerWeights(), weightIncrement.getFifthLayerWeights());
        return new Weights(newSecondLayerWeights, newFifthLayerWeighs);
    }

    private Weights getWeightIncrement(
            Data<RegressionInstance> data,
            List<Float> iterationsPredictedValueList,
            Weights currentWeights,
            Integer nRules,
            List<Integer> nTermList,
            AnfisFirstLayer firstLayer,
            AnfisSecondLayer secondLayer,
            AnfisThirdLayer thirdLayer,
            AnfisFourthLayer fourthLayer
    ) {

        List<Float> realValueList = data.getInstanceList().stream()
                .map(instance -> instance.getConsequentValueList().get(0))
                .collect(Collectors.toList());
        Integer N = realValueList.size();

        List<Float> outputDifference = IntStream.range(0, N).boxed()
                .map(i -> iterationsPredictedValueList.get(i) - realValueList.get(i))
                .collect(Collectors.toList());

        List<List<ImmutablePair<Float, Float>>> secondLayerDerivative = getSecondLayerDerivative(data, outputDifference, currentWeights, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer);

        List<List<Float>> fifthLayerDerivative = new ArrayList<>();
        List<Float> derivativeForAnyRule = getDerivativeForAnyRule(data, outputDifference);
        for (int i = 0; i < nRules; i++) {
            fifthLayerDerivative.add(derivativeForAnyRule);
        }
        return new Weights(secondLayerDerivative, fifthLayerDerivative);
    }



    // SECOND LAYER //

    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivative(
            Data<RegressionInstance> data,
            List<Float> outputDifference,
            Weights currentWeights,
            List<Integer> nTermList,
            AnfisFirstLayer firstLayer,
            AnfisSecondLayer secondLayer,
            AnfisThirdLayer thirdLayer,
            AnfisFourthLayer fourthLayer
    ) {
        Integer N = data.getInstanceList().size();
        List<List<List<ImmutablePair<Float, Float>>>> perInstance = IntStream.range(0, N).boxed()
                .map(instanceIndex -> getSecondLayerDerivativePerInstance(
                        data.getInstanceList().get(instanceIndex),
                        outputDifference.get(instanceIndex),
                        currentWeights,
                        nTermList,
                        firstLayer,
                        secondLayer,
                        thirdLayer,
                        fourthLayer
                ))
                .collect(Collectors.toList());
        return getSecondLayerDerivativeMean(perInstance, nTermList);
    }

    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivativePerInstance(
            RegressionInstance instance,
            Float outputDifference,
            Weights currentWeights,
            List<Integer> nTermList,
            AnfisFirstLayer firstLayer,
            AnfisSecondLayer secondLayer,
            AnfisThirdLayer thirdLayer,
            AnfisFourthLayer fourthLayer
    ) {

        List<Integer> antecedentTermList = nTermList.subList(0, nTermList.size()-1);

        List<Float> firstLayerOutput = firstLayer.run(instance);
        List<List<Float>> secondLayerOutput = secondLayer.run(firstLayerOutput, currentWeights.getSecondLayerWeights());
        List<Float> thirdLayerOutput = thirdLayer.run(secondLayerOutput);
        List<Float> fourthLayerOutput = fourthLayer.run(thirdLayerOutput);

        // These variable names refer to the calculations according to the chain rule
        Matrix secondOverWeights = getSecondOverWeightsMatrix(instance, currentWeights, antecedentTermList);
        Matrix thirdOverSecond = getThirdOverSecondMatrix(antecedentTermList, secondLayerOutput);
        Matrix fourthOverThird = getFourthOverThirdMatrix(thirdLayerOutput);
        Matrix fifthOverFourth = getFifthOverFourthMatrix(fourthLayerOutput, instance, currentWeights);
        Matrix sixthOverFifth = getSixthOverFifthMatrix(fourthLayerOutput.size());
        Matrix errorOverSixth = getErrorOverSixthMatrix(2 * outputDifference);

        Matrix gradient = new PipeFlow<>(secondOverWeights)
                .then(m -> thirdOverSecond.dot(m).get())
                .then(m -> fourthOverThird.dot(m).get())
                .then(m -> fifthOverFourth.dot(m).get())
                .then(m -> sixthOverFifth.dot(m).get())
                .then(m -> errorOverSixth.dot(m).get())
                .get(); // Shape = 1 x (2·sum(nTermList))

        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        for (int i = 0; i < antecedentTermList.size(); i ++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            for (int j = 0; j < antecedentTermList.get(i); j++) {
                Float aDerivative = gradient.get(0, 2*j);
                Float bDerivative = gradient.get(0, 2*j+1);
                perVariable.add(new ImmutablePair<>(aDerivative, bDerivative));
            }
            result.add(perVariable);
        }

        return result;
    }


    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivativeMean(List<List<List<ImmutablePair<Float, Float>>>> perInstance, List<Integer> nTermList) {
        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        Integer N = perInstance.size();
        for (int variableIndex = 0; variableIndex < nTermList.size()-1; variableIndex++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            for (int termIndex = 0; termIndex < nTermList.get(variableIndex); termIndex++) {
                int finalVariableIndex = variableIndex;
                int finalTermIndex = termIndex;
                Float a = IntStream.range(0, N).boxed().map(instanceIndex -> perInstance.get(instanceIndex)
                        .get(finalVariableIndex)
                        .get(finalTermIndex)
                        .getLeft()
                ).reduce(Float::sum).get()/N;
                Float b = IntStream.range(0, N).boxed().map(instanceIndex -> perInstance.get(instanceIndex).get(finalVariableIndex).get(finalTermIndex).getRight()).reduce(Float::sum).get()/N;
                perVariable.add(new ImmutablePair<>(a, b));
            }
            result.add(perVariable);
        }
        return result;
    }



    // MATRICES //


    private Matrix getSecondOverWeightsMatrix(RegressionInstance instance, Weights currentWeights, List<Integer> antecedentTermList) {

        Integer m = antecedentTermList.stream().reduce(Integer::sum).get();
        Integer n = 2*m;
        Matrix result = new Matrix(m, n);

        Integer variableIndex = 0;
        Integer termIndex = 0;

        for (int i = 0; i < m; i++) {
            Float x = variableIndex < instance.getAntecedentValueList().size()
                    ? instance.getAntecedentValueList().get(variableIndex)
                    : instance.getConsequentValueList().get(variableIndex-instance.getAntecedentValueList().size());
            Float a = currentWeights.getSecondLayerWeights().get(variableIndex).get(termIndex).getLeft();
            Float b = currentWeights.getSecondLayerWeights().get(variableIndex).get(termIndex).getRight();
            Float aSquared = a*a;
            Float xMinusBSquared = (x-b)*(x-b);
            Float exponential = (float) Math.exp(xMinusBSquared/(2*aSquared));
            result.set(i, i, -exponential * xMinusBSquared / (a*aSquared));
            result.set(i, i+1, exponential * (b-x) / aSquared);

            termIndex++;
            if(termIndex == antecedentTermList.get(variableIndex)) {
                variableIndex++;
                termIndex = 0;
            }
        }

        return result;
    }

    private Matrix getThirdOverSecondMatrix(List<Integer> antecedentTermList, List<List<Float>> secondLayerOutput) {

        Integer m = antecedentTermList.stream().reduce((x,y)->x*y).get();
        Integer n = antecedentTermList.stream().reduce(Integer::sum).get();
        Matrix result = new Matrix(m,n);

        List<List<Integer>> auxList = AnfisUtils.getCombinationList(antecedentTermList);
        for (int i = 0; i < m; i++) {
            for (int variableIndex = 0; variableIndex < antecedentTermList.size(); variableIndex++) {
                Integer j = auxList.get(i).get(variableIndex);
                int finalVariableIndex = variableIndex;
                int finalI = i;
                List<Float> a = IntStream.range(0, antecedentTermList.size()).boxed()
                        .filter(k -> k != finalVariableIndex)
                        .map(k -> secondLayerOutput
                                .get(k)
                                .get(auxList.get(finalI).get(k)))
                        .collect(Collectors.toList());

                Float otherFactorsProduct = a.stream()
                        .reduce((x,y) -> x*y).get();
                result.set(i, j, otherFactorsProduct);
            }
        }
        return result;
    }

    private Matrix getFourthOverThirdMatrix(List<Float> thirdLayerOutput) {
        Integer m = thirdLayerOutput.size();
        Float sum = thirdLayerOutput.stream().reduce(Float::sum).get();
        Float sumSquared = sum*sum;
        Matrix result = new Matrix(m, m);
        for (int i = 0; i < thirdLayerOutput.size(); i++) {
            for (int j = 0; j < thirdLayerOutput.size(); j++) {
                result.set(i, j, i == j
                        ? (sum - thirdLayerOutput.get(j)) / sumSquared
                        : -thirdLayerOutput.get(j) / sumSquared);
            }
        }
        return result;
    }

    private Matrix getFifthOverFourthMatrix(List<Float> fourthLayerOutput, RegressionInstance instance, Weights currentWeights) {
        Integer m = fourthLayerOutput.size();
        Matrix result = new Matrix(m, m);
        for (int i = 0; i < m; i++) {
            result.set(i, i, AnfisUtils.dot(instance.getAntecedentValueList(), currentWeights.getFifthLayerWeights().get(i)));
        }
        return result;
    }

    private Matrix getSixthOverFifthMatrix(int n) {
        Matrix result = new Matrix(1, n);
        for (int i = 0; i < n; i++) {
            result.set(0, i, 1F);
        }
        return result;
    }

    private Matrix getErrorOverSixthMatrix(Float value) {
        Matrix result = new Matrix(1, 1);
        result.set(0, 0, value);
        return result;
    }



    // FIFTH LAYER //

    private List<Float> getDerivativeForAnyRule(Data<RegressionInstance> data, List<Float> outputDifference) {
        Integer antecedentSize = data.getInstanceList().get(0).getAntecedentValueList().size();
        return IntStream.range(0, antecedentSize).boxed()
                .map(varIndex -> getDerivativeForAnyRuleGivenVariable(data, outputDifference, varIndex))
                .collect(Collectors.toList());
    }

    private Float getDerivativeForAnyRuleGivenVariable(Data<RegressionInstance> data, List<Float> outputDifference, Integer varIndex) {
        Integer N = data.getInstanceList().size();
        Float mean = IntStream.range(0, N).boxed()
                .map(instanceIndex -> {
                    Float x = data.getInstanceList().get(instanceIndex).getAntecedentValueList().get(varIndex);
                    Float d = outputDifference.get(instanceIndex);
                    return x*d;
                })
                .reduce(Float::sum)
                .get() / N;
        return LEARNING_RATE * 2 * mean;
    }



    // UPDATING //

    private List<List<ImmutablePair<Float, Float>>> updateSecondLayerWeights(List<List<ImmutablePair<Float, Float>>> current, List<List<ImmutablePair<Float, Float>>> increment) {
        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        for (int i = 0; i < current.size(); i++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            for (int j = 0; j < current.get(i).size(); j++) {
                perVariable.add(new ImmutablePair<>(
                        current.get(i).get(j).getLeft() + increment.get(i).get(j).getLeft(),
                        current.get(i).get(j).getRight() + increment.get(i).get(j).getRight()
                ));
            }
            result.add(perVariable);
        }
        return result;
    }

    private List<List<Float>> updateFifthLayerWeights(List<List<Float>> current, List<List<Float>> increment) {
        List<List<Float>> result = new ArrayList<>();
        Integer ruleSize = current.get(0).size()-1;
        for (int i = 0; i < current.size(); i++) {
            List<Float> perRule = new ArrayList<>();
            perRule.add(0F);
            for (int j = 0; j < ruleSize; j++) {
                perRule.add(current.get(i).get(j) + increment.get(i).get(j));
            }
            result.add(perRule);
        }
        return result;
    }
}
