package jfmltrainer.fileparser;

import jfml.knowledgebase.KnowledgeBaseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class KnowledgeBaseParser implements FileParser<KnowledgeBaseType> {

    @Override
    public KnowledgeBaseType read(String filePath) {
        try {
            return doRead(filePath);
        } catch (JAXBException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private KnowledgeBaseType doRead(String filePath) throws JAXBException {

        JAXBContext content = JAXBContext.newInstance(KnowledgeBaseType.class);
        Unmarshaller unmarshaller = content.createUnmarshaller();
        Object knowledgeBase = (JAXBElement<KnowledgeBaseType>) unmarshaller.unmarshal(new File(filePath));
        // TODO - Printing if asked
        return (KnowledgeBaseType) knowledgeBase;
    }



}
