package jfmltrainer.task.rulebasetrainer.classification.furia.irep;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.data.instance.ClassificationInstance;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispClause;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispClauseCateg;
import jfmltrainer.task.rulebasetrainer.classification.furia.irep.rule.CrispRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IREPUtils {

    public static <T extends Number> Float log2(T x) {
        Float xF = Float.parseFloat(x.toString());
        return xF > 0F
                ? (float) (Math.log(xF) / Math.log(2))
                : 0F;
    }

    public static List<BinaryIREPInstance> getUncoveredInstanceList(List<BinaryIREPInstance> instanceList, List<CrispRule> ruleList, KnowledgeBaseType knowledgeBase) {
        List<BinaryIREPInstance> uncoveredInstanceList = new ArrayList<>(instanceList);
        for (int i = 0; i < ruleList.size(); i++) {
            uncoveredInstanceList = getUncoveredInstanceListByRule(uncoveredInstanceList, ruleList.get(i), knowledgeBase);
        }
        return uncoveredInstanceList;
    }

    public static List<BinaryIREPInstance> getUncoveredInstanceListByRule(List<BinaryIREPInstance> instanceList, CrispRule rule, KnowledgeBaseType knowledgeBase) {
        return instanceList.stream()
                .filter(instance -> !covers(rule, instance.getInstance(), knowledgeBase))
                .collect(Collectors.toList());
    }

    public static boolean covers(CrispRule crispRule, ClassificationInstance instance, KnowledgeBaseType knowledgeBase) {

        if (crispRule.getAntecedent().isEmpty())
            return false;

        Boolean keepLooping = true;
        Boolean soFarSoGood = true;
        int i = 0;

        while (keepLooping) {
            String varName = crispRule.getAntecedent().get(i).getVarName();
            Integer varPosition = IntStream.range(0, instance.getAntecedentValueList().size())
                    .boxed()
                    .filter(pos -> knowledgeBase.getKnowledgeBaseVariables().get(pos).getName() == varName)
                    .findAny()
                    .get();
            Boolean coversAttribute = coversAttribute(crispRule.getAntecedent().get(i), instance.getAntecedentValueList().get(varPosition));
            if (coversAttribute) {
                i++;
                if (i == crispRule.getAntecedent().size()) {
                    keepLooping = false;
                }
            } else {
                soFarSoGood = false;
                keepLooping = false;
            }
        }
        Boolean coversConsequent = Objects.equals(crispRule.getConsequent(), instance.getConsequentValueList().get(0));
        return soFarSoGood && coversConsequent;
    }

    private static boolean coversAttribute(CrispClause crispClause, Float value) {
        return crispClause.getType().equals(CrispClauseCateg.LESS_OR_EQUAL)
                ? value <= crispClause.getValue()
                : value >= crispClause.getValue();
    }
}
