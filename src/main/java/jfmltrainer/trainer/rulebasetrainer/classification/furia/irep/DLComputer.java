package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule.CrispRule;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DLComputer {

    public static Float getMDL(List<BinaryIREPInstance> instanceList, List<CrispRule> possibleNewRuleSet, KnowledgeBaseType knowledgeBase){
        Float theoryCost = getTheoryCost(
                instanceList.stream().map(BinaryIREPInstance::getInstance).collect(Collectors.toList()),
                possibleNewRuleSet
        );
        Float exceptionCost = getExceptionCost(instanceList, possibleNewRuleSet, knowledgeBase);
        return theoryCost + exceptionCost;
    }

    private static Float getTheoryCost(List<ClassificationInstance> instanceList, List<CrispRule> possibleNewRuleSet){
        return possibleNewRuleSet.stream()
                .map(rule -> theoryDL(rule, instanceList))
                .reduce(Float::sum)
                .get();
    }

    private static Float theoryDL(CrispRule rule, List<ClassificationInstance> instanceList) { // TODO
        Integer k = rule.getAntecedent().size();
        Integer allPossibleConditions = computeAllPossibleConditions(instanceList);

        if(k == 0)
            return 0F;

        Float tdl = (float) Math.log(k);
        if(k > 1)                           // Approximation
            tdl += 2 * (float) Math.log(tdl);   // of log2 star
        tdl += subsetDL(allPossibleConditions, k, allPossibleConditions);

        return 0.5F * tdl;
    }

    private static Integer computeAllPossibleConditions(List<ClassificationInstance> instanceList) {
        // Given the i-th numerical variable, X_i, among the data there will be N_x distinct values.
        // For such a value, namely x, there will be two possible conditions: X_i <= x or X_i >= x.

        List<Integer> distinctValuesPerAttribute = IntStream.range(0, instanceList.get(0).getAntecedentValueList().size())
                .boxed()
                .map(variableIndex -> instanceList.stream()
                        .map(instance -> instance.getAntecedentValueList().get(variableIndex)).collect(Collectors.toList()))
                .map(variableValues -> (int) variableValues.stream().distinct().count())
                .collect(Collectors.toList());

        return 2 * distinctValuesPerAttribute.stream().reduce(Integer::sum).get();
    }


    private static Float subsetDL(Integer t, Integer k, Integer p) {
        Float rt = p > 0F
                ? -k * IREPUtils.log2(p)
                : 0F;
        rt -= (t-k) * IREPUtils.log2(1-p);
        return rt;
    }

    private static Float getExceptionCost(List<BinaryIREPInstance> instanceList, List<CrispRule> ruleList, KnowledgeBaseType knowledgeBase) {
        if (ruleList.size() == 0) {
            return 0F;
        }

        Stats stats = Stats.buildStats(instanceList, ruleList, knowledgeBase);
        Integer uncoveredCases = stats.getTn() + stats.getFn();
        Integer coveredCases = stats.getTp() + stats.getFp();
        Integer d = uncoveredCases + coveredCases;
        Integer falseCases = stats.getFn() + stats.getFp();

        Float uncoveredBits, coveredBits;
        if (coveredCases > uncoveredCases) {
            coveredBits = (float) falseCases / (2*coveredCases);
            uncoveredBits = divideOrZero((float) stats.getFn(), (float) uncoveredCases);
        } else {
            coveredBits = divideOrZero((float) stats.getFp(), (float) coveredCases);
            uncoveredBits = (float) falseCases / (2 * uncoveredCases);
        }

        Float tpProb = coveredBits == 0
                ? 0F
                : stats.getTp() * (-IREPUtils.log2(1-coveredBits));
        Float fpProb = coveredBits == 0
                ? 0F
                : stats.getFp() * (-IREPUtils.log2(coveredBits));
        Float tnProb = uncoveredBits == 0
                ? 0F
                : stats.getTn() * (-IREPUtils.log2(1-uncoveredBits));
        Float fnProb = uncoveredBits == 0
                ? 0F
                : stats.getFn() * (-IREPUtils.log2(uncoveredBits));

        return IREPUtils.log2(d + 1) + tpProb + tnProb + fpProb + fnProb;
    }

    private static Float divideOrZero(Float x, Float y) {
        return y == 0
                ? 0
                : x/y;
    }
}
