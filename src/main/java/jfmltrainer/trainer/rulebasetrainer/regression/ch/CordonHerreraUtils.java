package jfmltrainer.trainer.rulebasetrainer.regression.ch;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.FuzzyRuleType;
import jfml.term.FuzzyTermType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.trainer.rulebasetrainer.RuleBaseTrainerUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CordonHerreraUtils {

    public static List<ImmutablePair<List<Instance>, FuzzyRuleType>> generateCHCandidateRuleList(Data data, KnowledgeBaseType knowledgeBase) {
        List<FuzzyRuleType> allPossibleRuleList = generateAllPossibleRuleList(knowledgeBase);
        List<ImmutablePair<List<Instance>, FuzzyRuleType>> candidateRuleList = filterCandidateRules(allPossibleRuleList, data, knowledgeBase);
        return candidateRuleList;
    }


    private static List<FuzzyRuleType> generateAllPossibleRuleList(KnowledgeBaseType knowledgeBase) {
        return getAllFuzzyTermCombinations(knowledgeBase.getKnowledgeBaseVariables(), Collections.emptyList()).stream()
                .map(termList -> RuleBaseTrainerUtils.buildRuleFromTermList(termList, knowledgeBase))
                .collect(Collectors.toList());
    }

    private static List<List<FuzzyTermType>> getAllFuzzyTermCombinations(List<KnowledgeBaseVariable> variablesLeftList, List<List<FuzzyTermType>> accum) {

        if (variablesLeftList.isEmpty()) {
            return accum;
        }

        List<List<FuzzyTermType>> newAccum;
        if (accum.isEmpty()) {
            newAccum = variablesLeftList.get(0).getTerms().stream()
                    .map(term -> Collections.singletonList((FuzzyTermType) term))
                    .collect(Collectors.toList());
        } else {
            newAccum = accum.stream()
                    .map(termList -> enrichWithAllPossibleNewTerms(termList, variablesLeftList.get(0)))
                    .reduce((x,y) -> {
                        List<List<FuzzyTermType>> result = new ArrayList<>(x);
                        result.addAll(y);
                        return result;
                    })
                    .get();
        }
        return getAllFuzzyTermCombinations(variablesLeftList.subList(1, variablesLeftList.size()), newAccum);
    }

    private static List<List<FuzzyTermType>> enrichWithAllPossibleNewTerms(List<FuzzyTermType> termList, KnowledgeBaseVariable knowledgeBaseVariable) {
        return knowledgeBaseVariable.getTerms().stream()
                .map(newTerm -> {
                    List<FuzzyTermType> listToExpand = new ArrayList<>(termList); // To copy
                    listToExpand.add((FuzzyTermType) newTerm);
                    return listToExpand;
                })
                .collect(Collectors.toList());
    }

    private static List<ImmutablePair<List<Instance>, FuzzyRuleType>> filterCandidateRules(List<FuzzyRuleType> allPossibleRuleList, Data data, KnowledgeBaseType knowledgeBase) {
        return allPossibleRuleList.stream()
                .map(rule -> new ImmutablePair<>(RuleBaseTrainerUtils.getCoveredInstances(rule, data, knowledgeBase), rule))
                .filter(CordonHerreraUtils::actuallyCoversInstances)
                .collect(Collectors.toList());
    }

    private static Boolean actuallyCoversInstances(ImmutablePair<List<Instance>, FuzzyRuleType> instanceListAndRule) {
        return !instanceListAndRule.getLeft().isEmpty();
    }







}
