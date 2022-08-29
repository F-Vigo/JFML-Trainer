package jfmltrainer.fileparser.frbs;

import jfml.FuzzyInferenceSystem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class FISParser extends FRBSParser<FuzzyInferenceSystem> {

    private static FISParser instance = new FISParser();

    private FISParser(){}

    public static FISParser getInstance() {
        return instance;
    }

    @Override
    protected JAXBContext getContent() throws JAXBException {
        return JAXBContext.newInstance(FuzzyInferenceSystem.class);

    }
}
