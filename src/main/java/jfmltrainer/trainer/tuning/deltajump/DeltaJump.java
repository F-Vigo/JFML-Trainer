package jfmltrainer.trainer.tuning.deltajump;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.method.Problem;
import jfmltrainer.trainer.MethodConfig;
import jfmltrainer.trainer.tuning.Tuner;
import jfmltrainer.trainer.tuning.deltajump.parameter.MFParameter;
import jfmltrainer.trainer.tuning.deltajump.parameter.Parameter;
import jfmltrainer.trainer.tuning.deltajump.parameter.RWParameter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeltaJump extends Tuner {

    private static int DEFAULT_MAX_LINE_SEARCH_ITERATIONS = 20;

    protected int MAX_ITERATIONS = 20; // TODO



    // MAIN FUNCTIONS //

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> tune(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, MethodConfig methodConfig) {
        List<Parameter> optimizedParameterList = optimize(data, knowledgeBase, ruleBase);
        return buildFIS(knowledgeBase, ruleBase, optimizedParameterList);
    }

    private List<Parameter> optimize(Data data, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        List<Parameter> parameterList = initialParameterList(knowledgeBase, ruleBase);
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            parameterList = optimizeIteration(data, parameterList, knowledgeBase, ruleBase);
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


    private List<RWParameter> initialRuleWeightParameterList(RuleBaseType ruleBase) {
        return ruleBase.getRules().stream()
                .map(rule -> new RWParameter(rule.getWeight()))
                .collect(Collectors.toList());
    }



    // ITERATIONS //

    private List<Parameter> optimizeIteration(Data data, List<Parameter> parameterList, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase) {
        List<Parameter> result = new ArrayList<>();
        result.addAll(parameterList);
        IntStream.range(0, parameterList.size()).boxed()
                .forEach(i -> result.set(i, optimizeParameter(data, result.get(i), knowledgeBase, ruleBase, i)));
        return result;
    }

    private Parameter optimizeParameter(Data data, Parameter parameter, KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase, int parameterIndex) {
        Float errorOld = errorFunction(data, ruleBase);
        Float jump = estimateJump(data, parameter, errorOld, ruleBase);
        Boolean canJump = jump == 0;
        Parameter newParameter = parameter instanceof MFParameter
                ? new MFParameter(knowledgeBase, ((MFParameter) parameter).getVarIndex(), ((MFParameter) parameter).getTermIndex(), parameterIndex)
                : new RWParameter(parameter.getValue());
        if (canJump) {
            newParameter.setValue(doJump(data, parameter, jump, errorOld, ruleBase));
        }
        return newParameter;
    }


    private Float estimateJump(Data data, Parameter parameter, Float errorOld, RuleBaseType ruleBase) {
        Float epsilon = parameter.getEpsilon();
        Float value = parameter.getValue();
        Float errorNew = null;

        Boolean setPlus = parameter.tryToSetValue(value + epsilon);
        if (setPlus) {
            errorNew = errorFunction(data, ruleBase);
            parameter.tryToSetValue(value);
            if (errorNew < errorOld) {
                return epsilon;
            }
        }

        Boolean setMinus = parameter.tryToSetValue(value - epsilon);
        if (setMinus) {
            errorNew = errorFunction(data, ruleBase);
            parameter.tryToSetValue(value);
            if (errorNew < errorOld) {
                return -epsilon;
            }
        }

        return 0F;
    }



    private Float doJump(Data data, Parameter parameter, Float jump, Float errorOld, RuleBaseType ruleBase) {

        Float originalValue = parameter.getValue();
        Float alpha = 1F; // Initial value
        Float bestAlpha = 0F;
        Float bestAlphaError = errorOld;

        Boolean keepLooping = true;
        for (int lineIterIndex = 0; lineIterIndex < MAX_ITERATIONS && keepLooping; lineIterIndex++) {
            Boolean set = parameter.tryToSetValue(originalValue + jump * alpha);
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
