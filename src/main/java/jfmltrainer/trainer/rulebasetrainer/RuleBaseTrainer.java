package jfmltrainer.trainer.rulebasetrainer;

import jfml.jaxb.FuzzySystemType;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.graphics.JFMLTrainerGraphics;
import jfmltrainer.trainer.MethodConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;

public abstract class RuleBaseTrainer<T extends Instance> {

    public void train(Data<T> data, KnowledgeBaseType knowledgeBase, String outputFolder, String outputName, MethodConfig methodConfig) {
        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = trainRuleBase(data, knowledgeBase, methodConfig);
        fuzzySystem.getRight().setAndMethod(methodConfig.getAndOperator().get().getName());
        fuzzySystem.getRight().setOrMethod(methodConfig.getOrOperator().get().getName());
        fuzzySystem.getRight().setActivationMethod(methodConfig.getThenOperator().get().getName());
        JFMLTrainerGraphics.drawAndSaveImage(fuzzySystem.getLeft(), fuzzySystem.getRight());
        exportFuzzySystem(fuzzySystem, outputFolder, outputName);
    }

    protected abstract ImmutablePair<KnowledgeBaseType, RuleBaseType> trainRuleBase(Data<T> data, KnowledgeBaseType knowledgeBase, MethodConfig methodConfig);

    private void exportFuzzySystem(ImmutablePair<KnowledgeBaseType, ? extends RuleBaseType> fuzzySystem, String outputFolder, String outputName) {
        FuzzySystemType fuzzySystemType = new FuzzySystemType();
        fuzzySystemType.setKnowledgeBase(fuzzySystem.getLeft());
        fuzzySystemType.addRuleBase(fuzzySystem.getRight());
        fuzzySystemType.setName(outputName);
        // TODO - Write to XML
    }
}
