package jfmltrainer.task.rulebasetrainer.regression.anfis;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentClausesType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfml.term.TskTermType;
import jfmltrainer.aux.JFMLRandom;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.regression.RegressionTrainer;
import jfmltrainer.task.rulebasetrainer.regression.anfis.layers.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ANFIS extends RegressionTrainer {

    private AnfisFirstLayer firstLayer = new AnfisFirstLayer();
    private AnfisSecondLayer secondLayer = new AnfisSecondLayer();
    private AnfisThirdLayer thirdLayer = new AnfisThirdLayer();
    private AnfisFourthLayer fourthLayer = new AnfisFourthLayer();
    private AnfisFifthLayer fifthLayer = new AnfisFifthLayer();
    private AnfisSixthLayer sixthLayer = new AnfisSixthLayer();

    private WeightsUpdater weightsUpdater = new WeightsUpdater();

    private JFMLRandom JFMLRandom = jfmltrainer.aux.JFMLRandom.getInstance();


    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        Integer nVar = knowledgeBase.getKnowledgeBaseVariables().size();
        List<Integer> nTermList = knowledgeBase.getKnowledgeBaseVariables().stream()
                .map(var -> var.getTerms().size())
                .collect(Collectors.toList());
        Integer nRules = nTermList.stream().reduce((x,y) -> x*y).get();
        List<List<ImmutablePair<Float, Float>>> secondLayerWeights = getInitialSecondLayerWeights(nVar, nTermList);
        List<List<Float>> fifthLayerWeights = getInitialFifthLayerWeights(nVar, nRules);
        Weights weights = new Weights(secondLayerWeights, fifthLayerWeights);

        for (int i = 0; i < methodConfig.getMaxIter().get(); i++) {
            List<Float> iterationsPredictedValueList = iteration(data, secondLayerWeights, fifthLayerWeights);
            weights = weightsUpdater.updateWeights(weights, iterationsPredictedValueList, data, nRules, nTermList, firstLayer, secondLayer, thirdLayer, fourthLayer);
        }
        KnowledgeBaseType newKnowledgeBase = fromWeightsToKnowledgeBase(weights.getSecondLayerWeights());
        RuleBaseType newRuleBase = fromWeightsToRuleBase(weights.getFifthLayerWeights());
        return new ImmutablePair<>(newKnowledgeBase, newRuleBase);
    }



    // INITIAL WEIGHTS //

    private List<List<Float>> getInitialFifthLayerWeights(Integer nVar, Integer nRules) {
        List<List<Float>> result = new ArrayList<>();
        for (int i = 0; i < nRules; i++) {
            List<Float> sublist = new ArrayList<>();
            for (int j = 0; j < nVar; j++) {
                sublist.add((float) JFMLRandom.randReal());
            }
            result.add(sublist);
        }
        return result;
    }

    private List<List<ImmutablePair<Float, Float>>> getInitialSecondLayerWeights(Integer nVar, List<Integer> nTermList) {
        List<List<ImmutablePair<Float, Float>>> result = new ArrayList<>();
        for (int i = 0; i < nVar; i++) {
            List<ImmutablePair<Float, Float>> weightsPerVariable = new ArrayList<>();
            for (int j = 0; j < nTermList.get(i); j++) {
                Float a = JFMLRandom.randReal();
                Float b = JFMLRandom.randReal();
                weightsPerVariable.add(new ImmutablePair<>(a, b));
            }
            result.add(weightsPerVariable);
        }
        return result;
    }



    // MAIN LOOP //

    public List<Float> iteration(Data<RegressionInstance> data, List<List<ImmutablePair<Float,Float>>> secondLayerWeights, List<List<Float>> fifthLayerWeights) {
        List<Float> predictedValueList = data.getInstanceList().stream()
                .map(instance -> iterationPerInstance(instance, secondLayerWeights, fifthLayerWeights))
                .collect(Collectors.toList());
        return predictedValueList;
    }

    public Float iterationPerInstance(RegressionInstance instance, List<List<ImmutablePair<Float,Float>>> secondLayerWeights, List<List<Float>> fifthLayerWeights) {
        return new PipeFlow<>(instance)
                .then(input -> firstLayer.run(input))
                .then(output -> secondLayer.run(output, secondLayerWeights))
                .then(output -> thirdLayer.run(output))
                .then(output -> fourthLayer.run(output))
                .then(output -> fifthLayer.run(output, instance, fifthLayerWeights))
                .then(output -> sixthLayer.run(output))
                .get();
    }



    // AUXILIARY FUNCTIONS //

    private KnowledgeBaseType fromWeightsToKnowledgeBase(List<List<ImmutablePair<Float, Float>>> secondLayerWeights) {
        List<FuzzyVariableType> variableList = secondLayerWeights.stream()
                .map(this::fromWeightsToVariable)
                .collect(Collectors.toList());
        KnowledgeBaseType newKnowledgeBase = new KnowledgeBaseType();
        variableList.forEach(newKnowledgeBase::addVariable);
        return newKnowledgeBase;
    }

    private FuzzyVariableType fromWeightsToVariable(List<ImmutablePair<Float, Float>> weightPairList) {
        List<FuzzyTermType> termList = weightPairList.stream()
                .map(this::fromWeightsToTerm)
                .collect(Collectors.toList());
        FuzzyVariableType variable = new FuzzyVariableType();
        termList.forEach(variable::addFuzzyTerm);
        return variable;
    }

    private FuzzyTermType fromWeightsToTerm(ImmutablePair<Float, Float> weightPair) {
        return new FuzzyTermType(
                "",
                4, // Gaussian
                new float[]{weightPair.getLeft(), weightPair.getRight()}
        );
    }

    private RuleBaseType fromWeightsToRuleBase(List<List<Float>> fifthLayerWeightList) {
        List<FuzzyRuleType> ruleList = fifthLayerWeightList.stream()
                .map(this::fromWeightsToRule)
                .collect(Collectors.toList());
        return buildRuleBase(ruleList);
    }

    private FuzzyRuleType fromWeightsToRule(List<Float> weightList) {
        float[] array = new float[weightList.size()];
        for (int i = 0; i < weightList.size(); i++) {
            array[i] = weightList.get(i);
        }
        TskTermType term = new TskTermType("", 1, array);
        ClauseType clause = new ClauseType(null, term);
        ConsequentClausesType then = new ConsequentClausesType();
        then.addClause(clause);
        ConsequentType consequent = new ConsequentType(then, null);
        FuzzyRuleType rule = new FuzzyRuleType();
        rule.setConsequent(consequent);
        return rule;
    }

    public static RuleBaseType buildRuleBase(List<FuzzyRuleType> prunedRuleList) {
        RuleBaseType ruleBase = new RuleBaseType();
        prunedRuleList.forEach(ruleBase::addRule);
        return ruleBase;
    }
}
