package jfmltrainer.trainer.rulebasetrainer.classification.furia.irep;

import jfml.knowledgebase.KnowledgeBaseType;
import jfmltrainer.trainer.rulebasetrainer.classification.furia.irep.rule.CrispRule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stats {

    int tp; // True positives
    int tn; // True negatives
    int fp; // False positives
    int fn; // False negatives

    public static Stats buildStats(
            List<BinaryIREPInstance> instanceList,
            List<CrispRule> ruleList,
            KnowledgeBaseType knowledgeBase
    ) {
        List<BinaryIREPInstance> uncoveredInstanceList = IREPUtils.getUncoveredInstanceList(instanceList, ruleList, knowledgeBase);

        int fn = (int) uncoveredInstanceList.stream().filter(BinaryIREPInstance::getIsPositive).count();
        int tp = (int) instanceList.stream().filter(BinaryIREPInstance::getIsPositive).count() - fn;
        int tn = (int) uncoveredInstanceList.stream().filter(binaryIREPInstance -> !binaryIREPInstance.getIsPositive()).count();
        int fp = (int) instanceList.stream().filter(binaryIREPInstance -> !binaryIREPInstance.getIsPositive()).count();

        return new Stats(tp, tn, fp, fn);
    }
}
