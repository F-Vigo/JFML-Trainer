package jfmltrainer.task.rulebasetrainer.regression.cor;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.rulebasetrainer.MethodConfig;
import jfmltrainer.task.rulebasetrainer.RuleBaseTrainerUtils;
import jfmltrainer.task.rulebasetrainer.regression.RegressionTrainer;
import jfmltrainer.task.rulebasetrainer.regression.cor.targetfunction.CORTargetFunction;
import jfmltrainer.task.rulebasetrainer.regression.cor.targetfunction.CORTargetFunctionMSE;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CORAbstract extends RegressionTrainer {

        @Override
        public ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data<RegressionInstance> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) {
            List<FuzzyRuleType> candidateRuleList = getCandidateRuleList(data, knowledgeBase);
            List<FuzzyRuleType> optimalRuleList = getOptimalRuleList(candidateRuleList, data, knowledgeBase, methodConfig);
            RuleBaseType ruleBase = RuleBaseTrainerUtils.buildRuleBase(optimalRuleList);
            return new ImmutablePair<>(knowledgeBase, ruleBase);
        }

        protected abstract List<FuzzyRuleType> getCandidateRuleList(Data data, KnowledgeBaseType knowledgeBase);

        private List<FuzzyRuleType> getOptimalRuleList(
                List<FuzzyRuleType> candidateRuleList,
                Data<RegressionInstance> data,
                KnowledgeBaseType knowledgeBase,
                MethodConfig methodConfig
        ) {

            Map<String, List<FuzzyRuleType>> searchSpacePerSubspace = candidateRuleList.stream()
                    .collect(Collectors.groupingBy(RuleBaseTrainerUtils::getAntecedentClausesAsString));

            return searchSpacePerSubspace.values().stream()
                    .map(getOptimalRulePerSubspace(methodConfig.getCorSearchMethod(), data, knowledgeBase, methodConfig))
                    .collect(Collectors.toList());
        }

        private Function<List<FuzzyRuleType>, FuzzyRuleType> getOptimalRulePerSubspace(CORSearchMethod corSearchMethod, Data data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig) { // TODO
            CORTargetFunction targetFunction = new CORTargetFunctionMSE();
            switch (corSearchMethod) {
                case EXPLICIT_ENUMERATION:
                    return ruleList -> Utils.argMax(ruleList, currentSolution -> targetFunction.apply(currentSolution, data, knowledgeBase));
                default: // This includes SA (Simulated Annealing)
                    return ruleList -> SimulatedAnnealing.run(
                            ruleList,
                            currentSolution -> (solution -> isNeighbour(currentSolution, solution)),
                            currentSolution -> targetFunction.apply(currentSolution, data, knowledgeBase),
                            ruleList.stream().findAny().get());
            }
        }

        private Boolean isNeighbour(FuzzyRuleType currentSolution, FuzzyRuleType solution) {

            Function<FuzzyRuleType, List<FuzzyTermType>> getTerms = rule -> {
                List<FuzzyTermType> termList = new ArrayList<>();
                List<FuzzyTermType> antecedentTermList = rule.getAntecedent().getClauses().stream()
                        .map(clause -> (FuzzyTermType) clause.getTerm())
                        .collect(Collectors.toList());
                List<FuzzyTermType> consequentTermList = rule.getConsequent().getThen().getClause().stream()
                        .map(clause -> (FuzzyTermType) clause.getTerm())
                        .collect(Collectors.toList());
                termList.addAll(antecedentTermList);
                termList.addAll(consequentTermList);
                return termList;
            };

            List<FuzzyTermType> currentSolutionTermList = getTerms.apply(currentSolution);
            List<FuzzyTermType> solutionTermList = getTerms.apply(solution);

            int nonMatchingTermsCount = (int) IntStream.range(0, currentSolutionTermList.size())
                    .filter(i -> !currentSolutionTermList.get(i).getName().equals(solutionTermList.get(i).getName()))
                    .boxed()
                    .count();

            return nonMatchingTermsCount == 1;
        }
}
