package jfmltrainer.fileparser.frbs;

import jfml.rulebase.RuleBaseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class RuleBaseParser extends FRBSParser<RuleBaseType> {

    private static RuleBaseParser instance = new RuleBaseParser();

    private RuleBaseParser(){}

    public static RuleBaseParser getInstance() {
        return instance;
    }


    @Override
    protected JAXBContext getContent() throws JAXBException {
        return JAXBContext.newInstance(RuleBaseType.class);
    }
}