package jfmltrainer.task.rulebasetrainer.classification.furia;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentClausesType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.task.rulebasetrainer.classification.ClassificationTrainer;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.IREP;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.IREPUtils;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispClause;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispClauseCateg;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispRule;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FURIA extends ClassificationTrainer {

    @Override
    public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
        List<CrispRule> IREPRuleSet = new IREP().getIREPRuleSet(data, knowledgeBase, methodConfig);
        List<FuzzyRuleType> fuzzyRuleSet = fuzzifyIREPRuleSet(data.getInstanceList(), IREPRuleSet, knowledgeBase);
        RuleBaseType ruleBase = RuleBaseTrainerUtils.buildRuleBase(fuzzyRuleSet);
        KnowledgeBaseType approximativeKnowledgeBase = Utils.buildApproximativeKnowledgeBase(knowledgeBase, ruleBase);
        return new ImmutablePair<>(approximativeKnowledgeBase, ruleBase);
    }



    // RULE SET FUZZIFICATION //

    private List<FuzzyRuleType> fuzzifyIREPRuleSet(List<ClassificationInstance> instanceList, List<CrispRule> irepRuleSet, KnowledgeBaseType knowledgeBase) {
        return irepRuleSet.stream()
                .map(crispRule -> fuzzifyRule(crispRule, instanceList, knowledgeBase))
                .collect(Collectors.toList());
    }

    private FuzzyRuleType fuzzifyRule(
            CrispRule crispRule,
            List<ClassificationInstance> instanceList,
            KnowledgeBaseType knowledgeBase
    ) {
        List<CrispClause> numericAntecedentList = crispRule.getAntecedent();
        List<ClassificationInstance> coveredInstance = instanceList.stream()
                .filter(instance -> IREPUtils.covers(crispRule, instance, knowledgeBase))
                .collect(Collectors.toList());

        List<FuriaClauseAndPurity> bestFuzzificationFound = fuzzyRuleAux(coveredInstance, numericAntecedentList, knowledgeBase, new ArrayList<>());
        FuzzyRuleType fuzzyRule = buildFuzzyRule(bestFuzzificationFound, coveredInstance, knowledgeBase);
        fuzzyRule = enrichWithClass(crispRule, fuzzyRule, knowledgeBase);

        return fuzzyRule;
    }

    private List<FuriaClauseAndPurity> fuzzyRuleAux(
            List<ClassificationInstance> instanceList,
            List<CrispClause> availableNumericAntecedentList,
            KnowledgeBaseType knowledgeBase,
            List<FuriaClauseAndPurity> accum
    ) {
        if (availableNumericAntecedentList.isEmpty()) {
            return accum;
        }

        FuriaClauseAndPurity bestSoFar = new FuriaClauseAndPurity(null, null, Float.MIN_VALUE);

        for (CrispClause crispClause : availableNumericAntecedentList) {
            FuriaClauseAndPurity newCandidate = fuzzifyAntecedent(instanceList, crispClause, knowledgeBase, crispClause.getVarName());
            if (newCandidate.getPurity() > bestSoFar.getPurity()) {
                bestSoFar = newCandidate;
            }
        }
        accum.add(bestSoFar);
        FuriaClauseAndPurity finalBestSoFar = bestSoFar;
        List<CrispClause> newAvailableNumericAntecedentList = availableNumericAntecedentList.stream()
                .filter(antecedent -> !antecedent.getVarName().equals(finalBestSoFar.getVarName())
                        || finalBestSoFar.getTrapezoid().getMembershipValue(antecedent.getValue()) > 0)
                .collect(Collectors.toList());

        return fuzzyRuleAux(
                instanceList,
                newAvailableNumericAntecedentList,
                knowledgeBase,
                accum
        );
    }

    private FuzzyRuleType enrichWithClass(CrispRule crispRule, FuzzyRuleType fuzzyRule, KnowledgeBaseType knowledgeBase) {

        KnowledgeBaseVariable variable = knowledgeBase.getKnowledgeBaseVariables().stream()
                .filter(KnowledgeBaseVariable::isOutput)
                .findFirst()
                .get();

        ConsequentClausesType consequentClausesType = new ConsequentClausesType();
        consequentClausesType.addClause(
                variable,
                variable.getTerm(crispRule.getConsequent())
        );

        ConsequentType consequentType = new ConsequentType();
        consequentType.setThen(consequentClausesType);

        fuzzyRule.setConsequent(consequentType);

        return fuzzyRule;
    }


    private FuriaClauseAndPurity fuzzifyAntecedent(
            List<ClassificationInstance> instanceList,
            CrispClause crispClause,
            KnowledgeBaseType knowledgeBase,
            String className
    ) {
        Integer varPosition = IntStream.range(0, knowledgeBase.getKnowledgeBaseVariables().size())
                .boxed()
                .filter(i -> knowledgeBase.getKnowledgeBaseVariables().get(i).getName().equals(crispClause.getVarName()))
                .findAny()
                .get();
        List<Float> attributeValues = instanceList.stream()
                .map(instance -> instance.getAntecedentValueList().get(varPosition))
                .collect(Collectors.toList());
        ImmutablePair<Trapezoid, Float> bestTrapezoidAndPurity = getBestTrapezoidAndPurity(crispClause, attributeValues, instanceList, className);

        return new FuriaClauseAndPurity(crispClause.getVarName(), bestTrapezoidAndPurity.getLeft(), bestTrapezoidAndPurity.getRight());
    }

    private ImmutablePair<Trapezoid, Float> getBestTrapezoidAndPurity(CrispClause crispClause, List<Float> attributeValues, List<ClassificationInstance> instanceList, String className) {

        Trapezoid bestTrapezoidSoFar = null;
        Float bestPuritySoFar = Float.MIN_VALUE;

        Boolean isLessOrEqual = crispClause.getType().equals(CrispClauseCateg.LESS_OR_EQUAL);

        Optional<Float> coreExtreme = Optional.of(crispClause.getValue());
        Predicate<Float> filter = isLessOrEqual
                ? value -> crispClause.getValue() <= value
                : value -> value <= crispClause.getValue();
        List<Float> candidateSupportList = attributeValues.stream()
                .filter(filter)
                .collect(Collectors.toList());

        for (Float candidateSupport : candidateSupportList) {

            Trapezoid newTrapezoid = isLessOrEqual
                    ? new Trapezoid(Optional.empty(), Optional.empty(), coreExtreme, Optional.of(candidateSupport))
                    : new Trapezoid(Optional.of(candidateSupport), coreExtreme, Optional.empty(), Optional.empty());
            Float newPurity = computePurity(newTrapezoid, instanceList, className, attributeValues);

            if (newPurity > bestPuritySoFar) {
                bestPuritySoFar = newPurity;
                bestTrapezoidSoFar = newTrapezoid;
            } else if (newPurity == bestPuritySoFar) {
                Boolean isWider = isLessOrEqual
                        ? candidateSupport > bestTrapezoidSoFar.getSupportR().get()
                        : candidateSupport < bestTrapezoidSoFar.getSupportL().get();
                if (isWider) {
                    bestPuritySoFar = newPurity;
                    bestTrapezoidSoFar = newTrapezoid;
                }
            }
        }
        return new ImmutablePair<>(bestTrapezoidSoFar, bestPuritySoFar);
    }


    // EVALUATOR //

    private Float computePurity(Trapezoid trapezoid, List<ClassificationInstance> instanceList, String className, List<Float> attributeValues) {
        List<Float> positiveList = new ArrayList<>();
        List<Float> negativeList = new ArrayList<>();
        IntStream.range(0, instanceList.size()).boxed()
                .forEach(i -> {
                    if (instanceList.get(i).getConsequentValueList().get(0).equals(className)) {
                        positiveList.add(attributeValues.get(i));
                    } else {
                        negativeList.add(attributeValues.get(i));
                    }
                });

        Float p = positiveList.stream().map(trapezoid::getMembershipValue).reduce(Float::sum).get();
        Float n = negativeList.stream().map(trapezoid::getMembershipValue).reduce(Float::sum).get();

        return p / (p+n);
    }



    // BUILDING //

    private FuzzyRuleType buildFuzzyRule(List<FuriaClauseAndPurity> bestFuzzificationFound, List<ClassificationInstance> instanceList, KnowledgeBaseType knowledgeBase) {
        FuzzyRuleType fuzzyRule = new FuzzyRuleType();
        for (KnowledgeBaseVariable variable : knowledgeBase.getKnowledgeBaseVariables()) {
            ClauseType clauseType = new ClauseType();
            Integer varIndex = knowledgeBase.getKnowledgeBaseVariables().indexOf(variable);
            FuriaClauseAndPurity protoClause = bestFuzzificationFound.stream().filter(clause -> clause.getVarName().equals(variable.getName())).findFirst().get();
            clauseType.setTerm(buildFuzzyTerm(protoClause, instanceList, varIndex));
            fuzzyRule.getAntecedent().addClause(clauseType);
        }
        return fuzzyRule;
    }

    private FuzzyTermType buildFuzzyTerm(FuriaClauseAndPurity protoClause, List<ClassificationInstance> instanceList, Integer varIndex) {
        float[] paramList = buildParams(protoClause, instanceList, varIndex);
        return new FuzzyTermType(
                "",
                7, // Trapezoid
                paramList
        );
    }

    private float[] buildParams(FuriaClauseAndPurity protoClause, List<ClassificationInstance> instanceList, Integer varIndex) {
        List<Float> varValues = instanceList.stream().map(instance -> instance.getAntecedentValueList().get(varIndex)).collect(Collectors.toList());
        Float min = varValues.stream().min(Float::compareTo).get();
        Float max = varValues.stream().max(Float::compareTo).get();
        return protoClause.getTrapezoid().getCoreL().isEmpty() // If original rule was of <= type.
                ? new float[]{min, min, protoClause.getTrapezoid().getCoreR().get(), protoClause.getTrapezoid().getSupportR().get()}
                : new float[]{protoClause.getTrapezoid().getCoreR().get(), protoClause.getTrapezoid().getSupportR().get(), max, max};
    }
}
