package jfmltrainer.trainer.rulebasetrainer.regression.cor;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.rulebasetrainer.regression.ch.CordonHerreraUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.stream.Collectors;

public class CORCH extends CORAbstract {
    @Override
    protected List<FuzzyRuleType> getCandidateRuleList(Data data, KnowledgeBaseType knowledgeBase) {
        return CordonHerreraUtils.generateCHCandidateRuleList(data, knowledgeBase).stream()
                .map(ImmutablePair::getRight)
                .distinct()
                .collect(Collectors.toList());
    }
}
