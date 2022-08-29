package jfmltrainer.args;

import jfmltrainer.method.RuleBaseTrainerMethod;
import jfmltrainer.operator.and.AndOperator;
import jfmltrainer.operator.and.AndOperatorMIN;
import jfmltrainer.operator.or.OrOperator;
import jfmltrainer.operator.or.OrOperatorMAX;
import jfmltrainer.operator.rvf.RVFOperator;
import jfmltrainer.operator.rvf.RVFOperatorMAX;
import jfmltrainer.operator.then.ThenOperator;
import jfmltrainer.operator.then.ThenOperatorMIN;
import jfmltrainer.task.Task;
import jfmltrainer.task.rulebasetrainer.regression.cor.CORSearchMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static jfmltrainer.method.RuleBaseTrainerMethod.WANG_MENDEL;
import static jfmltrainer.task.Task.RULE_BASE_TRAINER;

public class ArgsParserTest {

    @Test
    public void whenAWellFormattedListIsPassed_thenMethodIsReturnedAsExpected() {

        String dataPath = "dataPath";
        String knowledgeBasePath = "knowledgeBasePath";
        String ruleBasePath = "ruleBasePath";
        String newKnowledgeBasePath = "newKnowledgeBasePath";
        String newRuleBasePath = "newRuleBasePath";
        String variableDefinitionDataPath = "variableDefinitionDataPath";
        List<Integer> granularityList = List.of(3,5);
        Integer singleGranularity = 5;
        String frbsPath = "frbsPath";
        String newFrbsPath = "newFrbsPath";
        Integer seed = 42;
        Integer maxIter = 100;
        Integer populationSize = 100;
        Float mutationProb = 0.1F;
        Float growProportion = 0.8F;
        Integer surplus = 64;
        Integer bitsgene = 10;
        Boolean isGlobal = true;
        Boolean isWithoutSelection = true;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-t " + Task.RULE_BASE_TRAINER.getValue());
        stringBuilder.append(" -m " + RuleBaseTrainerMethod.WANG_MENDEL.getName());
        stringBuilder.append(" -d " + dataPath);
        stringBuilder.append(" -kb " + knowledgeBasePath);
        stringBuilder.append(" -rb " + ruleBasePath);
        stringBuilder.append(" -nkb " + newKnowledgeBasePath);
        stringBuilder.append(" -nrb " + newRuleBasePath);
        stringBuilder.append(" -vdd " + variableDefinitionDataPath);
        stringBuilder.append(" -gl 3,5" );
        stringBuilder.append(" -sg 5");
        stringBuilder.append(" -frbs " + frbsPath);
        stringBuilder.append(" -nfrbs " + newFrbsPath);
        stringBuilder.append(" -ao MIN");
        stringBuilder.append(" -oo MAX");
        stringBuilder.append(" -to MIN");
        stringBuilder.append(" -s 42");
        stringBuilder.append(" -rvf MAX");
        stringBuilder.append(" -cs " + CORSearchMethod.EXPLICIT_ENUMERATION.getName());
        stringBuilder.append(" -mi 100");
        stringBuilder.append(" -ps 100");
        stringBuilder.append(" -mp 0.1");
        stringBuilder.append(" -gp 0.8");
        stringBuilder.append(" -sp 64");
        stringBuilder.append(" -bi 10");
        stringBuilder.append(" -glo 1");
        stringBuilder.append(" -wou 1");

        String string = stringBuilder.toString();
        Args args = ArgsParser.parse(string.split(" "));

        Assert.assertTrue(args.getTask().isPresent());
        Assert.assertTrue(args.getMethod().isPresent());
        Assert.assertTrue(args.getDataPath().isPresent());
        Assert.assertTrue(args.getKnowledgeBasePath().isPresent());
        Assert.assertTrue(args.getRuleBasePath().isPresent());
        Assert.assertTrue(args.getNewKnowledgeBasePath().isPresent());
        Assert.assertTrue(args.getNewRuleBasePath().isPresent());
        Assert.assertTrue(args.getVariableDefinitionDataPath().isPresent());
        Assert.assertTrue(args.getGranularityList().isPresent());
        Assert.assertTrue(args.getSingleGranularity().isPresent());
        Assert.assertTrue(args.getFrbsPath().isPresent());
        Assert.assertTrue(args.getNewFrbsPath().isPresent());
        Assert.assertTrue(args.getAndOperator().isPresent());
        Assert.assertTrue(args.getOrOperator().isPresent());
        Assert.assertTrue(args.getThenOperator().isPresent());
        Assert.assertTrue(args.getSeed().isPresent());
        Assert.assertTrue(args.getRvfOperator().isPresent());
        Assert.assertTrue(args.getCorSearchMethod().isPresent());
        Assert.assertTrue(args.getMaxIter().isPresent());
        Assert.assertTrue(args.getPopulationSize().isPresent());
        Assert.assertTrue(args.getMutationProb().isPresent());
        Assert.assertTrue(args.getGrowProportion().isPresent());
        Assert.assertTrue(args.getSurplus().isPresent());
        Assert.assertTrue(args.getBitsgene().isPresent());
        Assert.assertTrue(args.getIsGlobal().isPresent());
        Assert.assertTrue(args.getIsWithoutSelection().isPresent());

        Assert.assertEquals(Task.RULE_BASE_TRAINER, args.getTask().get());
        Assert.assertEquals(RuleBaseTrainerMethod.WANG_MENDEL, args.getMethod().get());
        Assert.assertEquals(dataPath, args.getDataPath().get());
        Assert.assertEquals(knowledgeBasePath, args.getKnowledgeBasePath().get());
        Assert.assertEquals(ruleBasePath, args.getRuleBasePath().get());
        Assert.assertEquals(newKnowledgeBasePath, args.getNewKnowledgeBasePath().get());
        Assert.assertEquals(newRuleBasePath, args.getNewRuleBasePath().get());
        Assert.assertEquals(variableDefinitionDataPath, args.getVariableDefinitionDataPath().get());
        Assert.assertEquals(granularityList, args.getGranularityList().get());
        Assert.assertEquals(singleGranularity, args.getSingleGranularity().get());
        Assert.assertEquals(frbsPath, args.getFrbsPath().get());
        Assert.assertEquals(newFrbsPath, args.getNewFrbsPath().get());
        Assert.assertEquals(AndOperatorMIN.getInstance(), args.getAndOperator().get());
        Assert.assertEquals(OrOperatorMAX.getInstance(), args.getOrOperator().get());
        Assert.assertEquals(ThenOperatorMIN.getInstance(), args.getThenOperator().get());
        Assert.assertEquals(seed, args.getSeed().get());
        Assert.assertEquals(RVFOperatorMAX.getInstance(), args.getRvfOperator().get());
        Assert.assertEquals(CORSearchMethod.EXPLICIT_ENUMERATION, args.getCorSearchMethod().get());
        Assert.assertEquals(maxIter, args.getMaxIter().get());
        Assert.assertEquals(populationSize, args.getPopulationSize().get());
        Assert.assertEquals(mutationProb, args.getMutationProb().get());
        Assert.assertEquals(growProportion, args.getGrowProportion().get());
        Assert.assertEquals(surplus, args.getSurplus().get());
        Assert.assertEquals(bitsgene, args.getBitsgene().get());
        Assert.assertEquals(isGlobal, args.getIsGlobal().get());
        Assert.assertEquals(isWithoutSelection, args.getIsWithoutSelection().get());
    }
}
