package jfmltrainer.task.graphics;

import jfml.FuzzyInferenceSystem;
import jfml.jaxb.FuzzySystemType;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.*;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.fileparser.frbs.FISParser;
import jfmltrainer.fileparser.frbs.KnowledgeBaseParser;
import jfmltrainer.fileparser.frbs.RuleBaseParser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JFMLTrainerGraphics {

    public static void drawAndSaveImage(Args args) {

        Optional<FuzzyInferenceSystem> frbsOpt = args.getFrbsPath().map(x -> FISParser.getInstance().read(x));
        Optional<FuzzyInferenceSystem> newFrbsOpt = args.getNewFrbsPath().map(x -> FISParser.getInstance().read(x));

        KnowledgeBaseType knowledgeBase;
        Optional<KnowledgeBaseType> newKnowledgeBase;
        RuleBaseType ruleBase;
        Optional<RuleBaseType> newRuleBase;

        // TODO - Fix!
        //frbsOpt = Optional.of(getFRBS());
        newFrbsOpt = Optional.of(getFRBS());

        if (frbsOpt.isPresent()) {
            knowledgeBase = frbsOpt.get().getKnowledgeBase();
            ruleBase = (RuleBaseType) frbsOpt.get().getRuleBase(0);
        } else {
            //knowledgeBase = KnowledgeBaseParser.getInstance().read(args.getKnowledgeBasePath().get());
            //ruleBase = RuleBaseParser.getInstance().read(args.getRuleBasePath().get());
        }

        if (newFrbsOpt.isPresent()) {
            newKnowledgeBase = newFrbsOpt.map(FuzzySystemType::getKnowledgeBase);
            newRuleBase = newFrbsOpt.map(x -> (RuleBaseType) x.getRuleBase(0));
        } else {
            newKnowledgeBase = args.getNewKnowledgeBasePath().map(path -> KnowledgeBaseParser.getInstance().read(path));
            newRuleBase = args.getNewKnowledgeBasePath().map(path -> RuleBaseParser.getInstance().read(path));;
        }


        // TODO - Fix!
        knowledgeBase = getKB1();
        ruleBase = getRB2(knowledgeBase);
        //newKnowledgeBase = Optional.of(getKB2());
        //newRuleBase = Optional.of(getRB2(newKnowledgeBase.get()));

        drawAndSaveImage(knowledgeBase, newKnowledgeBase, ruleBase, newRuleBase);
    }

    public static void drawAndSaveImage(KnowledgeBaseType knowledgeBase, Optional<KnowledgeBaseType> newKnowledgeBase, RuleBaseType ruleBase, Optional<RuleBaseType> newRuleBase) {
        new JFMLTrainerJFrame(knowledgeBase, newKnowledgeBase, ruleBase, newRuleBase);
    }









    private static FuzzyInferenceSystem getFRBS() {
        FuzzyInferenceSystem frbs = new FuzzyInferenceSystem();
        KnowledgeBaseType knowledgeBase = getKB1();
        frbs.setKnowledgeBase(knowledgeBase);
        frbs.addRuleBase(getRB1(knowledgeBase));
        return frbs;
    }



    private static KnowledgeBaseType getKB1() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType food = new FuzzyVariableType("food", 0, 10);
        food.setType("input");
        food.addFuzzyTerm("rancid", 3, new float[]{0F,0F,5F});
        food.addFuzzyTerm("good", 3, new float[]{0F,5F,10F});
        food.addFuzzyTerm("delicious", 3, new float[]{5F,10F,10F});

        FuzzyVariableType service = new FuzzyVariableType("service", 0, 10);
        service.setType("input");
        service.addFuzzyTerm("poor", 3, new float[]{0F,0F,5F});
        service.addFuzzyTerm("good", 3, new float[]{0F,5F,10F});
        service.addFuzzyTerm("excellent", 3, new float[]{5F,10F,10F});

        FuzzyVariableType tip = new FuzzyVariableType("tip", 0, 10);
        tip.setType("output");
        tip.addFuzzyTerm("cheap", 3, new float[]{0F,0F,5F});
        tip.addFuzzyTerm("average", 3, new float[]{0F,5F,10F});
        tip.addFuzzyTerm("generous", 3, new float[]{5F,10F,10F});

        knowledgeBase.addVariable(food);
        knowledgeBase.addVariable(service);
        knowledgeBase.addVariable(tip);

        return knowledgeBase;
    }

    private static KnowledgeBaseType getKB2() {

        KnowledgeBaseType knowledgeBase = new KnowledgeBaseType();

        FuzzyVariableType food = new FuzzyVariableType("food", 0, 10);
        food.setType("input");
        food.addFuzzyTerm("rancid", 3, new float[]{0F,0F,5F});
        food.addFuzzyTerm("good", 3, new float[]{0F,10F,10F});
        food.addFuzzyTerm("delicious", 3, new float[]{5F,10F,10F});

        FuzzyVariableType service = new FuzzyVariableType("service", 0, 10);
        service.setType("input");
        service.addFuzzyTerm("poor", 3, new float[]{0F,0F,5F});
        service.addFuzzyTerm("good", 3, new float[]{0F,5F,10F});
        service.addFuzzyTerm("excellent", 3, new float[]{5F,10F,10F});

        FuzzyVariableType tip = new FuzzyVariableType("tip", 0, 10);
        tip.setType("output");
        tip.addFuzzyTerm("cheap", 3, new float[]{0F,0F,5F});
        tip.addFuzzyTerm("average", 3, new float[]{0F,5F,10F});
        tip.addFuzzyTerm("generous", 3, new float[]{5F,10F,10F});

        knowledgeBase.addVariable(food);
        knowledgeBase.addVariable(service);
        knowledgeBase.addVariable(tip);

        return knowledgeBase;
    }

    private static RuleBaseType getRB1(KnowledgeBaseType knowledgeBase) {

        RuleBaseType ruleBase = new RuleBaseType();

        FuzzyVariableType food = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(0);
        FuzzyVariableType service = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(1);
        FuzzyVariableType tip = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(2);

        AntecedentType antecedent1 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("rancid")),
                new ClauseType(service, service.getTerm("good"))
        ));
        ConsequentType consequent1 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("average")))),
                null
        );
        FuzzyRuleType rule1 = new FuzzyRuleType("", antecedent1, consequent1);

        AntecedentType antecedent2 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("rancid")),
                new ClauseType(service, service.getTerm("poor"))
        ));
        ConsequentType consequent2 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("cheap")))),
                null
        );
        FuzzyRuleType rule2 = new FuzzyRuleType("", antecedent2, consequent2);

        ruleBase.addRule(rule1);
        ruleBase.addRule(rule2);

        return ruleBase;
    }

    private static RuleBaseType getRB2(KnowledgeBaseType knowledgeBase) {

        RuleBaseType ruleBase = new RuleBaseType();

        FuzzyVariableType food = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(0);
        FuzzyVariableType service = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(1);
        FuzzyVariableType tip = (FuzzyVariableType) knowledgeBase.getKnowledgeBaseVariables().get(2);

        AntecedentType antecedent1 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("good")),
                new ClauseType(service, service.getTerm("good"))
        ));
        ConsequentType consequent1 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("average")))),
                null
        );
        FuzzyRuleType rule1 = new FuzzyRuleType("", antecedent1, consequent1);

        AntecedentType antecedent2 = new AntecedentType(List.of(
                new ClauseType(food, food.getTerm("rancid")),
                new ClauseType(service, service.getTerm("poor"))
        ));
        ConsequentType consequent2 = new ConsequentType(
                new ConsequentClausesType(Collections.singletonList(new ClauseType(tip, tip.getTerm("cheap")))),
                null
        );
        FuzzyRuleType rule2 = new FuzzyRuleType("", antecedent2, consequent2);

        ruleBase.addRule(rule1);
        ruleBase.addRule(rule2);

        return ruleBase;
    }
}
