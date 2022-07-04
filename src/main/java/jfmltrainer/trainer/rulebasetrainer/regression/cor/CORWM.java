package jfmltrainer.trainer.rulebasetrainer.regression.cor;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rule.FuzzyRuleType;
import jfmltrainer.data.Data;
import jfmltrainer.trainer.rulebasetrainer.regression.wm.WangMendelUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.stream.Collectors;

public class CORWM extends CORAbstract {
    @Override
    protected List<FuzzyRuleType> getCandidateRuleList(Data data, KnowledgeBaseType knowledgeBase) {
        return WangMendelUtils.generateWMCandidateRuleList(data, knowledgeBase).stream()
                .map(ImmutablePair::getRight)
                .distinct()
                .collect(Collectors.toList());
    }
}
