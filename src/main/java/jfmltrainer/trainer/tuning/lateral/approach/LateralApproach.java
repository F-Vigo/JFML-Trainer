package jfmltrainer.trainer.tuning.lateral.approach;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.parameter.ThreeParamType;
import jfml.rule.*;
import jfml.rulebase.RuleBaseType;
import jfml.term.FuzzyTermType;
import jfmltrainer.trainer.tuning.lateral.Chromosome;
import jfmltrainer.trainer.tuning.lateral.Evaluator;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class LateralApproach {


    @Getter
    protected Evaluator evaluator;

    public abstract Integer getNGenes(KnowledgeBaseType knowledgeBase, RuleBaseType ruleBase);

    public abstract RuleBaseType buildRuleBase(KnowledgeBaseType oldKnowledgeBase, RuleBaseType oldRuleBase, Chromosome chromosome);


    protected FuzzyRuleType updateRule(KnowledgeBaseType oldKnowledgeBase, FuzzyRuleType oldRule, List<Float> geneList) {

        int antecedentSize = oldRule.getAntecedent().getClauses().size();

        List<ThreeParamType> oldAntecedentParamList = oldRule.getAntecedent().getClauses().stream()
                .map(clause -> (FuzzyTermType) clause.getTerm())
                .map(FuzzyTermType::getTriangularShape)
                .collect(Collectors.toList());
        List<ThreeParamType> oldConsequentParamList = oldRule.getConsequent().getThen().getClause().stream()
                .map(clause -> (FuzzyTermType) clause.getTerm())
                .map(FuzzyTermType::getTriangularShape)
                .collect(Collectors.toList());

        List<float[]> newAntecedentParamList = IntStream.range(0, antecedentSize).boxed()
                .map(i -> updateThreeParams(oldAntecedentParamList.get(i), geneList.get(i)))
                .map(threeParam -> new float[]{threeParam.getParam1(), threeParam.getParam2(), threeParam.getParam3()})
                .collect(Collectors.toList());
        List<float[]> newConsequentParamList = IntStream.range(0, oldConsequentParamList.size()).boxed()
                .map(i -> updateThreeParams(oldConsequentParamList.get(i), geneList.get(i+antecedentSize)))
                .map(threeParam -> new float[]{threeParam.getParam1(), threeParam.getParam2(), threeParam.getParam3()})
                .collect(Collectors.toList());

        List<FuzzyTermType> newAntecedentTermList = newAntecedentParamList.stream()
                .map(threeParam -> new FuzzyTermType("", 3, threeParam))
                .collect(Collectors.toList());
        List<FuzzyTermType> newConsequentTermList = newConsequentParamList.stream()
                .map(threeParam -> new FuzzyTermType("", 3, threeParam))
                .collect(Collectors.toList());

        List<ClauseType> newAntecedentClauseList = IntStream.range(0, antecedentSize).boxed()
                .map(i -> new ClauseType(oldKnowledgeBase.getKnowledgeBaseVariables().get(i), newAntecedentTermList.get(i)))
                .collect(Collectors.toList());
        List<ClauseType> newConsequentClauseList = IntStream.range(0, newConsequentTermList.size()).boxed()
                .map(i -> new ClauseType(oldKnowledgeBase.getKnowledgeBaseVariables().get(i+antecedentSize), newConsequentTermList.get(i)))
                .collect(Collectors.toList());

        FuzzyRuleType newRule = new FuzzyRuleType();
        newRule.setAntecedent(new AntecedentType(newAntecedentClauseList));
        newRule.setConsequent(new ConsequentType(new ConsequentClausesType(newConsequentClauseList), null));
        return newRule;
    }

    private ThreeParamType updateThreeParams(ThreeParamType threeParam, Float displacement) {
        ThreeParamType newParams = new ThreeParamType();
        newParams.setParam1(threeParam.getParam1() + displacement);
        newParams.setParam2(threeParam.getParam2() + displacement);
        newParams.setParam3(threeParam.getParam3() + displacement);
        return newParams;
    }
}
