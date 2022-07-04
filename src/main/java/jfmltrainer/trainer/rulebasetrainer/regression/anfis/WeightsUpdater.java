package jfmltrainer.trainer.rulebasetrainer.regression.anfis;

import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeightsUpdater {

    private Float LEARNING_RATE = 0.01F; // TODO

    public Weights updateWeights(Weights weights, List<Float> iterationsPredictedValueList, Data data, Integer nRules, List<Integer> nTermList, AnfisFirstLayer firstLayer, AnfisSecondLayer secondLayer, AnfisThirdLayer thirdLayer, AnfisFourthLayer fourthLayer, AnfisFifthLayer fifthLayer) {

        Weights weightIncrement = getWeightIncrement(data, iterationsPredictedValueList, weights, nRules, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer, fifthLayer);

        Integer secondLayerWeightSize = weights.getSecondLayerWeights().stream()
                .map(List::size)
                .reduce(Integer::sum)
                .get() * 2;

        List<List<ImmutablePair<Float, Float>>> newSecondLayerWeights = updateSecondLayerWeights(weights.getSecondLayerWeights(), weightIncrement.getSecondLayerWeights());
        List<List<Float>> newFifthLayerWeighs = updateFifthLayerWeights(weights.getFifthLayerWeights(), weightIncrement.getFifthLayerWeights());
        return new Weights(newSecondLayerWeights, newFifthLayerWeighs);
    }

    private Weights getWeightIncrement(Data<RegressionInstance> data, List<Float> iterationsPredictedValueList, Weights currentWeights, Integer nRules, List<Integer> nTermList, AnfisFirstLayer firstLayer, AnfisSecondLayer secondLayer, AnfisThirdLayer thirdLayer, AnfisFourthLayer fourthLayer, AnfisFifthLayer fifthLayer) {

        List<Float> realValueList = data.getInstanceList().stream()
                .map(instance -> instance.getConsequentValueList().get(0))
                .collect(Collectors.toList());
        Integer N = realValueList.size();

        List<Float> outputDifference = IntStream.range(0, N).boxed()
                .map(i -> iterationsPredictedValueList.get(i) - realValueList.get(i))
                .collect(Collectors.toList());

        List<List<ImmutablePair<Float, Float>>> secondLayerDerivative = getSecondLayerDerivative(data, outputDifference, currentWeights, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer, fifthLayer);

        List<List<Float>> sixthLayerDerivative = new ArrayList<>(nRules);
        List<Float> derivativeForAnyRule = getDerivativeForAnyRule(data, outputDifference);
        Collections.fill(sixthLayerDerivative, derivativeForAnyRule);

        return new Weights(secondLayerDerivative, sixthLayerDerivative); // TODO
    }



    // SECOND LAYER //

    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivative(Data<RegressionInstance> data, List<Float> outputDifference, Weights currentWeights, List<Integer> nTermList, AnfisFirstLayer firstLayer, AnfisSecondLayer secondLayer, AnfisThirdLayer thirdLayer, AnfisFourthLayer fourthLayer, AnfisFifthLayer fifthLayer) {
        Integer N = data.getInstanceList().size();
        List<List<List<ImmutablePair<Float, Float>>>> perInstance = IntStream.range(0, N).boxed()
                .map(instanceIndex -> getSecondLayerDerivativePerInstance(data.getInstanceList().get(instanceIndex), outputDifference.get(instanceIndex), currentWeights, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer, fifthLayer))
                .collect(Collectors.toList());
        return getSecondLayerDerivativeMean(perInstance, nTermList);
    }

    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivativePerInstance(RegressionInstance instance, Float outputDifference, Weights currentWeights, List<Integer> nTermList, AnfisFirstLayer firstLayer, AnfisSecondLayer secondLayer, AnfisThirdLayer thirdLayer, AnfisFourthLayer fourthLayer, AnfisFifthLayer fifthLayer) {

        // TODO - Rewrite
        List<Float> firstLayerOutput = firstLayer.run(instance);
        List<List<Float>> secondLayerOutput = secondLayer.run(firstLayerOutput, currentWeights.getSecondLayerWeights());
        List<Float> thirdLayerOutput = thirdLayer.run(secondLayerOutput);
        List<Float> fourthLayerOutput = fourthLayer.run(thirdLayerOutput);
        List<Float> fifthLayerOutput = fifthLayer.run(fourthLayerOutput, instance, currentWeights.getFifthLayerWeights());

        // These variable names refer to the calculations according to the chain rule
        Matrix secondOverWeights = getSecondOverWeightsMatrix(instance, currentWeights, nTermList);
        Matrix thirdOverSecond = getThirdOverSecondMatrix(nTermList, secondLayerOutput);
        Matrix fourthOverThird = getFourthOverThirdMatrix(thirdLayerOutput);
        Matrix fifthOverFourth = getFifthOverFourthMatrix(fourthLayerOutput, instance, currentWeights);
        Matrix sixthOverFifth = getSixthOverFifthMatrix(fourthLayerOutput.size());
        Matrix errorOverSixth = getErrorOverSixthMatrix(2 * outputDifference);

        Matrix gradient = new PipeFlow<>(secondOverWeights)
                .then(m -> thirdOverSecond.dot(m).get())
                .then(m -> fourthOverThird.dot(m).get())
                .then(m -> fifthOverFourth.dot(m).get())
                .then(m -> sixthOverFifth.dot(fifthOverFourth).get())
                .then(m -> errorOverSixth.dot(m).get())
                .get(); // shape = 1 x sum(nTermList)

        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        for (int i = 0; i < nTermList.size(); i ++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            for (int j = 0; j < nTermList.get(i); j++) {
                Float aDerivative = gradient.get(2*j, 1);
                Float bDerivative = gradient.get(2*j+1, 1);
                perVariable.add(new ImmutablePair<>(aDerivative, bDerivative));
            }
            result.add(perVariable);
        }

        return result;
    }

    private Matrix getErrorOverSixthMatrix(Float value) {
        Matrix result = new Matrix(1, 1);
        result.set(1, 1, value);
        return result;
    }


    private Matrix getSecondOverWeightsMatrix(RegressionInstance instance, Weights currentWeights, List<Integer> nTermList) {

        Integer m = nTermList.stream().reduce(Integer::sum).get();
        Integer n = 2*m;
        Matrix result = new Matrix(m, n);

        Integer variableIndex = 0;
        Integer termIndex = 0;

        for (int i = 0; i < m; i++) {
            Float x = instance.getAntecedentValueList().get(variableIndex);
            Float a = currentWeights.getSecondLayerWeights().get(variableIndex).get(termIndex).getLeft();
            Float b = currentWeights.getSecondLayerWeights().get(variableIndex).get(termIndex).getRight();
            Float aSquared = a*a;
            Float xMinusBSquared = (x-b)*(x-b);
            Float exponential = (float) Math.exp(xMinusBSquared/(2*aSquared));
            result.set(i, i, -exponential * xMinusBSquared / (a*aSquared));
            result.set(i, i+1, exponential * (b-x) / aSquared);

            termIndex++;
            if(termIndex == nTermList.get(variableIndex)) {
                variableIndex++;
                termIndex = 0;
            }
        }

        return result;
    }


    private Matrix getThirdOverSecondMatrix(List<Integer> nTermList, List<List<Float>> secondLayerOutput) {

        Integer m = nTermList.stream().reduce((x,y)->x*y).get();
        Integer n = nTermList.stream().reduce(Integer::sum).get();
        Matrix result = new Matrix(m,n);

        List<List<Integer>> auxList = AnfisUtils.getCombinationList(nTermList);
        for (int i = 0; i < m; i++) {
            for (int variableIndex = 0; variableIndex < nTermList.size(); variableIndex++) {
                Integer j = auxList.get(i).get(variableIndex);
                int finalVariableIndex = variableIndex;
                int finalI = i;
                Float otherFactorsProduct = IntStream.range(0, nTermList.size()).boxed()
                        .filter(k -> k != finalVariableIndex)
                        .map(k -> secondLayerOutput.get(finalI).get(k))
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
            result.set(1, i, 1F);
        }
        return result;
    }





    private List<List<ImmutablePair<Float, Float>>> getSecondLayerDerivativeMean(List<List<List<ImmutablePair<Float, Float>>>> perInstance, List<Integer> nTermList) {
        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        Integer N = perInstance.size();
        for (int variableIndex = 0; variableIndex < nTermList.size(); variableIndex++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            for (int termIndex = 0; termIndex < nTermList.get(variableIndex); termIndex++) {
                int finalVariableIndex = variableIndex;
                int finalTermIndex = termIndex;
                Float a = IntStream.range(0, N).boxed().map(instanceIndex -> perInstance.get(instanceIndex).get(finalVariableIndex).get(finalTermIndex).getLeft()).reduce(Float::sum).get()/N;
                Float b = IntStream.range(0, N).boxed().map(instanceIndex -> perInstance.get(instanceIndex).get(finalVariableIndex).get(finalTermIndex).getRight()).reduce(Float::sum).get()/N;
                perVariable.add(new ImmutablePair<>(a, b));
            }
            result.add(perVariable);
        }
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
        Integer ruleSize = current.get(0).size();
        for (int i = 0; i < current.size(); i++) {
            List<Float> perRule = new ArrayList<>();
            for (int j = 0; j < ruleSize; j++) {
                perRule.add(current.get(i).get(j) + increment.get(i).get(j));
            }
            result.add(perRule);
        }
        return result;
    }




















    private Weights fromListToWeights(List<Float> list, Weights reference) {

        List<List<ImmutablePair<Float, Float>>> newSecondLayerWeights = new ArrayList<>();
        List<List<Float>> newFifthLayerWeights = new ArrayList<>();

        for (int i = 0; i < reference.getSecondLayerWeights().size(); i ++) {
            List<ImmutablePair<Float, Float>> perVariable = new ArrayList<>();
            Integer nTerm = reference.getSecondLayerWeights().get(i).size();
            for (int j = 0; j < nTerm; j+=2) {
                perVariable.add(new ImmutablePair<>(list.get(i*nTerm+j), list.get(i*nTerm+j+1)));
            }
            newSecondLayerWeights.add(perVariable);
        }

        Integer secondLayerPartSize = reference.getSecondLayerWeights().stream()
                .map(perVariable -> perVariable.size())
                .reduce(Integer::sum)
                .get() * 2;

        for (int i = 0; i < reference.getFifthLayerWeights().size(); i++) {
            Integer ruleSize = reference.getFifthLayerWeights().get(0).size();
            List<Float> perRule = new ArrayList<>();
            for (int j = 0; j < ruleSize; j++) {
                Integer actualPosition = secondLayerPartSize + i*ruleSize + j;
                perRule.add(list.get(actualPosition));
            }
            newFifthLayerWeights.add(perRule);
        }
        return new Weights(newSecondLayerWeights, newFifthLayerWeights);
    }


    private List<Float> fromWeightsToList(Weights weights) {

        List<Float> result = new ArrayList<>();

        weights.getSecondLayerWeights().forEach(perVariable -> perVariable.forEach(pair -> {
            result.add(pair.getLeft());
            result.add(pair.getRight());
        }));

        weights.getFifthLayerWeights().forEach(result::addAll);

        return result;
    }
}
