package jfmltrainer.fileparser.frbs;

import jfml.knowledgebase.KnowledgeBaseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class KnowledgeBaseParser extends FRBSParser<KnowledgeBaseType> {

    private static KnowledgeBaseParser instance = new KnowledgeBaseParser();

    private KnowledgeBaseParser(){}

    public static KnowledgeBaseParser getInstance() {
        return instance;
    }


    @Override
    protected JAXBContext getContent() throws JAXBException {
        return JAXBContext.newInstance(KnowledgeBaseType.class);
    }
}
