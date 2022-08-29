package jfmltrainer.task.rulebasetrainer;

import jfml.jaxb.FuzzySystemType;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.task.graphics.JFMLTrainerGraphics;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.xml.bind.JAXBContext;
import java.io.FileOutputStream;
import java.util.Optional;

public abstract class RuleBaseTrainer<T extends Instance> {

    public void train(Data<T> data, KnowledgeBaseType knowledgeBase, String outputFolder, String outputName, MethodConfig methodConfig) {
        ImmutablePair<KnowledgeBaseType, RuleBaseType> fuzzySystem = trainRuleBase(data, knowledgeBase, methodConfig);
        fuzzySystem.getRight().setAndMethod(methodConfig.getAndOperator().getName());
        fuzzySystem.getRight().setOrMethod(methodConfig.getOrOperator().getName());
        fuzzySystem.getRight().setActivationMethod(methodConfig.getThenOperator().getName());
        JFMLTrainerGraphics.drawAndSaveImage(fuzzySystem.getLeft(), Optional.empty(), fuzzySystem.getRight(),Optional.empty());
        exportFuzzySystem(fuzzySystem, outputFolder, outputName);
    }

    public abstract ImmutablePair<
            KnowledgeBaseType,
            RuleBaseType
            > trainRuleBase(
                    Data<T> data,
                    KnowledgeBaseType knowledgeBase,
                    MethodConfig methodConfig
    );

    private void exportFuzzySystem(ImmutablePair<KnowledgeBaseType, ? extends RuleBaseType> fuzzySystem, String outputFolder, String outputName) {
        FuzzySystemType fuzzySystemType = new FuzzySystemType();
        fuzzySystemType.setKnowledgeBase(fuzzySystem.getLeft());
        fuzzySystemType.addRuleBase(fuzzySystem.getRight());
        fuzzySystemType.setName(outputName);
        writeToXML(fuzzySystemType, outputFolder, outputName);
    }

    private void writeToXML(FuzzySystemType fuzzySystemType, String outputFolder, String outputName) {
        String path = outputFolder + outputName + "xml";
        try {
            JAXBContext.newInstance(FuzzySystemType.class).createMarshaller().marshal(fuzzySystemType, new FileOutputStream(path));
        } catch (Exception e) {}
    }
}
