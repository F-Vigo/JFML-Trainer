package jfmltrainer.task.graphics;

import jfml.FuzzyInferenceSystem;
import jfml.jaxb.FuzzySystemType;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.args.Args;
import jfmltrainer.fileparser.frbs.FISParser;
import jfmltrainer.fileparser.frbs.KnowledgeBaseParser;
import jfmltrainer.fileparser.frbs.RuleBaseParser;

import java.util.Optional;

public class JFMLTrainerGraphics {

    public static void drawAndSaveImage(Args args) {

        Optional<FuzzyInferenceSystem> frbsOpt = args.getFrbsPath().map(x -> FISParser.getInstance().read(x));
        Optional<FuzzyInferenceSystem> newFrbsOpt = args.getNewFrbsPath().map(x -> FISParser.getInstance().read(x));

        KnowledgeBaseType knowledgeBase;
        Optional<KnowledgeBaseType> newKnowledgeBase;
        RuleBaseType ruleBase;
        Optional<RuleBaseType> newRuleBase;

        if (frbsOpt.isPresent()) {
            knowledgeBase = frbsOpt.get().getKnowledgeBase();
            ruleBase = (RuleBaseType) frbsOpt.get().getRuleBase().get(0);
        } else {
            knowledgeBase = KnowledgeBaseParser.getInstance().read(args.getKnowledgeBasePath().get());
            ruleBase = RuleBaseParser.getInstance().read(args.getRuleBasePath().get());
        }

        if (newFrbsOpt.isPresent()) {
            newKnowledgeBase = newFrbsOpt.map(FuzzySystemType::getKnowledgeBase);
            newRuleBase = newFrbsOpt.map(x -> (RuleBaseType) x.getRuleBase().get(0));
        } else {
            newKnowledgeBase = args.getNewKnowledgeBasePath().map(path -> KnowledgeBaseParser.getInstance().read(path));
            newRuleBase = args.getNewKnowledgeBasePath().map(path -> RuleBaseParser.getInstance().read(path));;
        }

        drawAndSaveImage(knowledgeBase, newKnowledgeBase, ruleBase, newRuleBase);
    }

    public static void drawAndSaveImage(KnowledgeBaseType knowledgeBase, Optional<KnowledgeBaseType> newKnowledgeBase, RuleBaseType ruleBase, Optional<RuleBaseType> newRuleBase) {
        new JFMLTrainerJFrame(knowledgeBase, newKnowledgeBase, ruleBase, newRuleBase);
    }
}
