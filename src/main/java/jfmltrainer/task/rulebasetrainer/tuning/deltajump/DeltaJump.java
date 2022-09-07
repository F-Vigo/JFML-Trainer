package jfmltrainer.task.rulebasetrainer.tuning.deltajump;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.method.Problem;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.tuning.Tuner;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter.MFParameter;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter.Parameter;
import jfmltrainer.task.rulebasetrainer.tuning.deltajump.parameter.RWParameter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeltaJump extends Tuner {

    // MAIN FUNCTIONS //

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> tuneFRBS(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        List<Parameter> optimizedParameterList = optimize(data, knowledgeBase, ruleBase, methodConfig);
        return buildFIS(knowledgeBase, ruleBase, optimizedParameterList);
    }

    private List<Parameter> optimize(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        List<Parameter> parameterList = initialParameterList(knowledgeBase, ruleBase);
        for (int i = 0; i < methodConfig.getMaxIter(); i++) {
            parameterList = optimizeIteration(data, parameterList, knowledgeBase, ruleBase, methodConfig);
        }
        return parameterList;
    }



    // INITIAL PARAMETER LIST //

    private List<Parameter> initialParameterList(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        List<Parameter> result = new ArrayList<>();
        result.addAll(initialMembershipFunctionParameterList(knowledgeBase));
        result.addAll(initialRuleWeightParameterList(ruleBase));
        return result;
    }

    private List<MFParameter> initialMembershipFunctionParameterList(KnowledgeBaseType knowledgeBase) {
        List<MFParameter> result = new ArrayList<>();
        IntStream.range(0, knowledgeBase.getKnowledgeBaseVariables().size()).boxed()
                .forEach(varIndex -> result.addAll(initialMembershipFunctionParameterListPerVariable(knowledgeBase, varIndex)));
        return result;
    }

    private List<MFParameter> initialMembershipFunctionParameterListPerVariable(KnowledgeBaseType knowledgeBase, Integer varIndex) {
        List<MFParameter> result = new ArrayList<>();
        KnowledgeBaseVariable variable = knowledgeBase.getKnowledgeBaseVariables().get(varIndex);
        IntStream.range(0, variable.getTerms().size()).boxed()
                .forEach(termIndex -> result.addAll(initialMembershipFunctionParameterListPerTerm(knowledgeBase, variable, varIndex, termIndex)));
        return result;
    }

    private List<MFParameter> initialMembershipFunctionParameterListPerTerm(KnowledgeBaseType knowledgeBase, KnowledgeBaseVariable variable, Integer varIndex, Integer termIndex) {
        List<MFParameter> result = new ArrayList<>();
        IntStream.range(0, ((FuzzyTermType) variable.getTerms().get(termIndex)).getParam().length).boxed()
                .forEach(paramIndex -> result.add(new MFParameter(knowledgeBase, varIndex, termIndex, paramIndex)));
        return result;
    }

    private int getParamIndexWithinTerm(KnowledgeBaseType knowledgeBase, Integer varIndex, Integer termIndex, Integer paramIndex) {

        int accum = 0;

        for (int i = 0; i < varIndex; i++) {
            accum += knowledgeBase.getKnowledgeBaseVariables().get(i).getTerms().stream()
                    .map(term -> ((FuzzyTermType) term).getParam().length)
                    .reduce(Integer::sum).get();
        }

        for (int i = 0; i < termIndex; i++) {
            accum += ((FuzzyTermType) knowledgeBase.getKnowledgeBaseVariables().get(varIndex).getTerms().get(i)).getParam().length;
        }

        return paramIndex - accum;
    }


    private List<RWParameter> initialRuleWeightParameterList(RuleBaseType ruleBase) {
        return ruleBase.getRules().stream()
                .map(rule -> new RWParameter(rule.getWeight()))
                .collect(Collectors.toList());
    }



    // ITERATIONS //

    private List<Parameter> optimizeIteration(Data data, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        List<Parameter> result = new ArrayList<>();
        result.addAll(parameterList);
        IntStream.range(0, parameterList.size()).boxed()
                .forEach(i -> result.set(i, optimizeParameter(data, result.get(i), parameterList, knowledgeBase, ruleBase, methodConfig, i, true)));
        return result;
    }

    private Parameter optimizeParameter(Data data, Parameter parameter, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig, int parameterIndex, Boolean isFromParamList) {
        Float errorOld = errorFunction(data, ruleBase);
        Float jump = estimateJump(data, parameter, errorOld, parameterList, knowledgeBase, ruleBase);
        Boolean canJump = jump == 0;
        Parameter newParameter = parameter instanceof MFParameter
                ? new MFParameter(knowledgeBase, ((MFParameter) parameter).getVarIndex(), ((MFParameter) parameter).getTermIndex(), isFromParamList ? getParamIndexWithinTerm(knowledgeBase, ((MFParameter) parameter).getVarIndex(), ((MFParameter) parameter).getTermIndex(), parameterIndex) : parameterIndex)
                : new RWParameter(parameter.getValue());
        if (canJump) {
            newParameter.setValue(doJump(data, parameter, jump, errorOld, parameterList, knowledgeBase, ruleBase, methodConfig));
        }
        return newParameter;
    }


    private Float estimateJump(Data data, Parameter parameter, Float errorOld, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        Float epsilon = parameter.getEpsilon();
        Float value = parameter.getValue();
        Float errorNew = null;

        Boolean setPlus = parameter.tryToSetValue(value + epsilon, parameterList, knowledgeBase);
        if (setPlus) {
            errorNew = errorFunction(data, ruleBase);
            parameter.tryToSetValue(value, parameterList, knowledgeBase);
            if (errorNew < errorOld) {
                return epsilon;
            }
        }

        Boolean setMinus = parameter.tryToSetValue(value - epsilon, parameterList, knowledgeBase);
        if (setMinus) {
            errorNew = errorFunction(data, ruleBase);
            parameter.tryToSetValue(value, parameterList, knowledgeBase);
            if (errorNew < errorOld) {
                return -epsilon;
            }
        }

        return 0F;
    }



    private Float doJump(Data data, Parameter parameter, Float jump, Float errorOld, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {

        Float originalValue = parameter.getValue();
        Float alpha = 1F; // Initial value
        Float bestAlpha = 0F;
        Float bestAlphaError = errorOld;

        Boolean keepLooping = true;
        for (int lineIterIndex = 0; lineIterIndex < methodConfig.getMaxIter() && keepLooping; lineIterIndex++) {
            Boolean set = parameter.tryToSetValue(originalValue + jump * alpha, parameterList, knowledgeBase);
            if (set) {
                Float error = errorFunction(data, ruleBase);
                Float errorIncrement = error - errorOld;
                if (errorIncrement < 0) {
                    if (error < bestAlphaError) {
                        bestAlphaError = error;
                        bestAlpha = alpha;
                    }
                    alpha *= 2;
                } else {
                    keepLooping = false;
                }
            } else {
                keepLooping = false;
            }
        }
        return originalValue + bestAlpha * jump;
    }



    // FINAL FUNCTIONS //

    private ImmutablePair<KnowledgeBaseType, RuleBaseType> buildFIS(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, List<Parameter> optimizedParameterList) {
        ImmutablePair<List<MFParameter>, List<RWParameter>> splitParameters = splitParameters(optimizedParameterList);
        return new ImmutablePair<>(
                buildKB(knowledgeBase, splitParameters.getLeft()),
                buildRB(ruleBase, splitParameters.getRight())
        );
    }

    private ImmutablePair<List<MFParameter>, List<RWParameter>> splitParameters(List<Parameter> optimizedParameterList) {
        List<MFParameter> mfParametersList = new ArrayList<>();
        List<RWParameter> rwParameterList = new ArrayList<>();
        for (Parameter parameter : optimizedParameterList) {
            if (parameter instanceof MFParameter) {
                mfParametersList.add((MFParameter) parameter);
            } else {
                rwParameterList.add((RWParameter) parameter);
            }
        }
        return new ImmutablePair<>(mfParametersList, rwParameterList);
    }

    private KnowledgeBaseType buildKB(KnowledgeBaseType knowledgeBase, List<MFParameter> parameterList) {
        for (MFParameter parameter : parameterList) {
            ((FuzzyTermType) knowledgeBase.getKnowledgeBaseVariables().get(parameter.getVarIndex()).getTerms().get(parameter.getTermIndex())).getParam()[parameter.getParamIndex()] = parameter.getValue();
        }
        return knowledgeBase;
    }

    private RuleBaseType buildRB(RuleBaseType ruleBase, List<RWParameter> parameterList) {
        IntStream.range(0, parameterList.size()).boxed()
                .forEach(i -> ruleBase.getRules().get(i).setWeight(parameterList.get(i).getValue()));
        return ruleBase;
    }



    // ERROR FUNCTIONS //

    private Float errorFunction(Data data, RuleBaseType ruleBase) {
        return ((Instance) data.getInstanceList().get(0)).getProblem().equals(Problem.REGRESSION)
                ? errorFunctionMSE(data, ruleBase)
                : errorFunctionAccuracy(data, ruleBase);

    }

    private Float errorFunctionMSE(Data<RegressionInstance> data, RuleBaseType ruleBase) {
        List<List<Float>> realValueList = data.getInstanceList().stream()
                .map(Instance::getConsequentValueList)
                .collect(Collectors.toList());
        List<List<Float>> predictedValueList = data.getInstanceList().stream()
                .map(instance -> Utils.predict(instance, ruleBase))
                .collect(Collectors.toList());
        return Utils.computeMSE(realValueList, predictedValueList);
    }

    private Float errorFunctionAccuracy(Data<ClassificationInstance> data, RuleBaseType ruleBase) {
        List<List<String>> realValueList = data.getInstanceList().stream()
                .map(Instance::getConsequentValueList)
                .collect(Collectors.toList());
        List<List<String>> predictedValueList = data.getInstanceList().stream()
                .map(instance -> Utils.predict(instance, ruleBase))
                .collect(Collectors.toList());
        return 1-Utils.computeAccuracy(realValueList, predictedValueList);
    }
}
