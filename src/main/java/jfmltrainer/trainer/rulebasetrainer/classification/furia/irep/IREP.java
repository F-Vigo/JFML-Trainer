package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.DataSorter;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule.CrispClause;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule.CrispClauseCateg;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule.CrispRule;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IREP {

    private static Float GROW_PROPORTION = 0.8F; // TODO
    private static Integer SURPLUS = 64;

    public List<CrispRule> getIREPRuleSet(Data data, KnowledgeBaseType knowledgeBase) {
        List<ClassificationInstance> instanceList = data.getInstanceList();
        List<String> sortedLabelList = DataSorter.getSortedLabelList(instanceList);
        List<CrispRule> ruleList = new ArrayList<>();
        for (int i = 0; i < sortedLabelList.size()-1; i++) {
            ruleList.addAll(runBinaryIREP(instanceList, knowledgeBase, sortedLabelList.get(i)));
        }
        ruleList.add(buildDefaultRule(sortedLabelList));
        return ruleList;
    }

    private CrispRule buildDefaultRule(List<String> sortedLabelList) {
        return new CrispRule(new ArrayList<>(), sortedLabelList.get(sortedLabelList.size()-1));
    }


    private String getLabel(ClassificationInstance instance) { // TODO
        return instance.getConsequentValueList().get(0);
    }

    private List<BinaryIREPInstance> getBinaryInstanceList(List<ClassificationInstance> instanceList, String positiveLabel) {
        return instanceList.stream()
                .map(instance -> new BinaryIREPInstance(instance, getLabel(instance).equals(positiveLabel)))
                .collect(Collectors.toList());
    }

    private List<CrispRule> runBinaryIREP(List<ClassificationInstance> instanceList, KnowledgeBaseType knowledgeBase, String positiveLabel) {
        List<BinaryIREPInstance> binaryInstanceList = getBinaryInstanceList(instanceList, positiveLabel);
        List<CrispRule> ruleList = generateRuleAntecedentList(binaryInstanceList, knowledgeBase);
        ruleList.forEach(rule -> rule.setConsequent(positiveLabel));
        return ruleList;
    }

    private List<CrispRule> generateRuleAntecedentList(List<BinaryIREPInstance> instanceList, KnowledgeBaseType knowledgeBase) {
        List<CrispRule> ruleList = buildPhase(instanceList, knowledgeBase);
        ruleList = optimizationPhase(ruleList, instanceList, knowledgeBase);
        return ruleList;
    }

    private List<CrispRule> buildPhase(List<BinaryIREPInstance> instanceList, KnowledgeBaseType knowledgeBase) {
        List<CrispRule> ruleList = new ArrayList<>();
        Float minDL = Float.MAX_VALUE;
        Float newDL;
        ImmutablePair<List<BinaryIREPInstance>, List<BinaryIREPInstance>> growAndPruneListPair = splitInGrowAndPrune(instanceList);
        List<BinaryIREPInstance> uncoveredInstanceList = null;
        Boolean keepLooping = true;
        while (keepLooping) {

            CrispRule newRule = growRule(growAndPruneListPair.getLeft(), knowledgeBase);
            // No prune for FURIA.

            List<CrispRule> possibleNewRuleSet = new ArrayList<>(ruleList);
            possibleNewRuleSet.add(newRule);
            newDL = DLComputer.getMDL(instanceList, possibleNewRuleSet, knowledgeBase);

            if (newDL <= minDL + SURPLUS) {
                ruleList = new ArrayList<>(possibleNewRuleSet);
                uncoveredInstanceList = IREPUtils.getUncoveredInstanceListByRule(uncoveredInstanceList, newRule, knowledgeBase);
                keepLooping = true;
                if (newDL < minDL) {
                    minDL = newDL;
                }
            } else {
                keepLooping = false;
            }
            Boolean existRemainingPositives = uncoveredInstanceList.stream().filter(BinaryIREPInstance::getIsPositive).count() > 0;
            keepLooping = keepLooping && existRemainingPositives;
        }
        return ruleList;

    }


    private CrispRule growRule(List<BinaryIREPInstance> instanceList, KnowledgeBaseType knowledgeBase) {
        return growRule(instanceList, knowledgeBase, new CrispRule(new ArrayList<>(), null));
    }

    private CrispRule growRule(List<BinaryIREPInstance> instanceList, KnowledgeBaseType knowledgeBase, CrispRule rule) {
        Float bestH = Float.MIN_VALUE; // HeuristicValue
        List<Integer> unusedAttributePositionList = getUnusedAttributePositionList(knowledgeBase, rule);
        List<BinaryIREPInstance> coveredInstanceList = IREPUtils.getUncoveredInstanceListByRule(instanceList, rule, knowledgeBase);

        int p = (int) instanceList.stream().filter(BinaryIREPInstance::getIsPositive).count();
        int n = (int) instanceList.stream().filter(binaryIREPInstance -> !binaryIREPInstance.getIsPositive()).count();
        Float positiveTotalProportionLog2 = IREPUtils.log2(p / (p+n));

        CrispRule bestRuleSoFar = new CrispRule(rule.getAntecedent(), rule.getConsequent());
        Boolean keepGrowing = unusedAttributePositionList.size() > 0 && p > 0 && n > 0;
        if (!keepGrowing) {
            return bestRuleSoFar;
        }


        CrispClause bestCrispClauseSoFar = null;
        CrispClause newCrispClause = null;
        CrispRule newRule = null;
        List<BinaryIREPInstance> candidateCoveredInstanceList = null;

        for (int i : unusedAttributePositionList) {
            // We assume it's a numerical attribute
            List<Float> possibleCutValues = coveredInstanceList.stream()
                    .map(instance -> instance.getInstance().getAntecedentValueList().get(i))
                    .collect(Collectors.toList());
            for (Float x : possibleCutValues) {
                for (CrispClauseCateg clauseType : CrispClauseCateg.values()) {
                    newCrispClause = new CrispClause(knowledgeBase.getKnowledgeBaseVariables().get(i).getName(), clauseType, x);
                    newRule = new CrispRule(bestRuleSoFar.getAntecedent(), bestRuleSoFar.getConsequent());
                    newRule.getAntecedent().add(newCrispClause);
                    CrispRule finalNewRule = newRule;
                    candidateCoveredInstanceList = coveredInstanceList.stream()
                            .filter(instance -> IREPUtils.covers(finalNewRule, instance.getInstance(), knowledgeBase))
                            .collect(Collectors.toList());
                    int pRule = (int) candidateCoveredInstanceList.stream().filter(BinaryIREPInstance::getIsPositive).count();
                    int nRule = (int) candidateCoveredInstanceList.stream().filter(binaryIREPInstance -> !binaryIREPInstance.getIsPositive()).count();
                    Float h = pRule * (IREPUtils.log2(pRule / (pRule+nRule)) - positiveTotalProportionLog2);
                    if (h > bestH) {
                        bestH = h;
                        bestCrispClauseSoFar = newCrispClause;
                    }
                }
            }
        }
        bestRuleSoFar.getAntecedent().add(bestCrispClauseSoFar);
        coveredInstanceList = coveredInstanceList.stream()
                .filter(instance -> IREPUtils.covers(bestRuleSoFar, instance.getInstance(), knowledgeBase))
                .collect(Collectors.toList());


        return growRule(coveredInstanceList, knowledgeBase, bestRuleSoFar);
    }

    private CrispRule pruneRule(List<BinaryIREPInstance> instanceList, CrispRule rule, KnowledgeBaseType knowledgeBase) {
        Float hOld = 0F;
        Float hNew = 0F;
        List<BinaryIREPInstance> coveredInstanceList = instanceList.stream()
                .filter(instance -> IREPUtils.covers(rule, instance.getInstance(), knowledgeBase))
                .collect(Collectors.toList());

        int p = (int) coveredInstanceList.stream()
                .filter(BinaryIREPInstance::getIsPositive)
                .count();
        int n = (int) coveredInstanceList.stream()
                .filter(instance -> !instance.getIsPositive())
                .count();
        int T = instanceList.size();
        int T_N = (int) instanceList.stream().filter(instance -> !instance.getIsPositive()).count();
        int nPrime = T_N - n;

        hNew = (float) (p + nPrime) / T;

        CrispRule pruningRule = new CrispRule(rule.getAntecedent(), rule.getConsequent());

        Boolean keepLooping = true;
        while(keepLooping) {
            hOld = hNew;
            CrispRule initRule = new CrispRule(pruningRule.getAntecedent().subList(0, pruningRule.getAntecedent().size()-1), pruningRule.getConsequent());
            List<BinaryIREPInstance> coveredByInitList = coveredInstanceList.stream()
                    .filter(instance -> IREPUtils.covers(initRule, instance.getInstance(), knowledgeBase))
                    .collect(Collectors.toList());
            p = (int) coveredByInitList.stream()
                    .filter(BinaryIREPInstance::getIsPositive)
                    .count();
            n = (int) coveredByInitList.stream()
                    .filter(instance -> !instance.getIsPositive())
                    .count();
            nPrime = T_N - n;

            hNew = (float) (p + nPrime) / T;

            if (hOld < hNew && rule.getAntecedent().size() > 1) {
                pruningRule = initRule;
            }
            keepLooping = hOld < hNew && pruningRule.getAntecedent().size() > 0; // TODO - Difference with RIPPER?
        }
        return pruningRule;
    }

    private List<Integer> getUnusedAttributePositionList(KnowledgeBaseType knowledgeBase, CrispRule rule) {
        int antecedentSize = (int) knowledgeBase.getKnowledgeBaseVariables().stream()
                .filter(KnowledgeBaseVariable::isInput)
                .count();
        List<String> varUsedNameList = rule.getAntecedent().stream()
                .map(CrispClause::getVarName)
                .collect(Collectors.toList());
        List<Boolean> unusedAttributes = IntStream.range(0, antecedentSize)
                .mapToObj(i -> true)
                .collect(Collectors.toList());
        for (int i = 0; i < antecedentSize; i++) {
            String varName = knowledgeBase.getKnowledgeBaseVariables().get(i).getName();
            if (varUsedNameList.contains(varName)) {
                unusedAttributes.set(i, false);
            }
        }
        List<Integer> unusedAttributePositionList = IntStream.range(0, antecedentSize)
                .boxed()
                .filter(unusedAttributes::get)
                .collect(Collectors.toList());
        return unusedAttributePositionList;
    }

    private List<CrispRule> optimizationPhase(List<CrispRule> ruleList, List<BinaryIREPInstance> instanceList, KnowledgeBaseType knowledgeBase) {
        return ruleList.stream()
                .map(rule -> optimizeRule(rule, instanceList, ruleList, knowledgeBase))
                .collect(Collectors.toList());
    }

    private CrispRule optimizeRule(CrispRule currentRule, List<BinaryIREPInstance> instanceList, List<CrispRule> ruleList, KnowledgeBaseType knowledgeBase) {
        ImmutablePair<List<BinaryIREPInstance>, List<BinaryIREPInstance>> growAndPruneListPair = splitInGrowAndPrune(instanceList);

        List<CrispRule> otherRulesList = ruleList.stream()
                .filter(rule -> !rule.equals(currentRule))
                .collect(Collectors.toList());
        growAndPruneListPair.setValue(IREPUtils.getUncoveredInstanceList(growAndPruneListPair.getValue(), otherRulesList, knowledgeBase));

        CrispRule revisionRule = growRule(growAndPruneListPair.getLeft(), knowledgeBase);
        revisionRule = pruneRule(growAndPruneListPair.getRight(), revisionRule, knowledgeBase);

        CrispRule replacementRule = new CrispRule(currentRule.getAntecedent(), currentRule.getConsequent());
        replacementRule = growRule(growAndPruneListPair.getLeft(), knowledgeBase, replacementRule);
        replacementRule = pruneRule(growAndPruneListPair.getRight(), replacementRule, knowledgeBase);

        List<CrispRule> possibleNewRuleSetCurrent = ruleList;
        Float currentMDL = DLComputer.getMDL(instanceList, possibleNewRuleSetCurrent, knowledgeBase);

        List<CrispRule> possibleNewRuleSetRevision = new ArrayList<>(otherRulesList);
        possibleNewRuleSetRevision.add(revisionRule);
        Float revisionMDL = DLComputer.getMDL(instanceList, possibleNewRuleSetRevision, knowledgeBase);

        List<CrispRule> possibleNewRuleSetReplacement = new ArrayList<>(otherRulesList);
        possibleNewRuleSetReplacement.add(replacementRule);
        Float replacementMDL = DLComputer.getMDL(instanceList, possibleNewRuleSetReplacement, knowledgeBase);

        if (currentMDL <= Math.min(revisionMDL, replacementMDL)) {
            return currentRule;
        } else if (revisionMDL <= Math.min(currentMDL, replacementMDL)) {
            return revisionRule;
        } else {
            return replacementRule;
        }
    }


    private Float labelProportion(Data sortedData, String positiveLabel) {
        int num = (int) sortedData.getInstanceList().stream()
                .filter(instance -> getLabel((ClassificationInstance) instance).equals(positiveLabel))
                .count();
        int den = sortedData.getInstanceList().size();
        return (float) num/den;
    }





    private ImmutablePair<List<BinaryIREPInstance>, List<BinaryIREPInstance>> splitInGrowAndPrune(List<BinaryIREPInstance> instanceList) {

        Integer size = instanceList.size();
        Integer border = Math.round(GROW_PROPORTION * size);

        List<Integer> positions = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(positions);

        List<Integer> positionsToGrow = positions.subList(0, border);
        List<BinaryIREPInstance> growList = new ArrayList<>();
        List<BinaryIREPInstance> pruneList = new ArrayList<>();

        for (int i = 0; i < size; i ++) {
            if (positionsToGrow.contains(i)) {
                growList.add(instanceList.get(i));
            } else {
                pruneList.add(instanceList.get(i));
            }
        }
        return new ImmutablePair<>(growList, pruneList);
    }
}
