package jfmltrainer.fileparser.frbs;

import jfmltrainer.fileparser.FileParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public abstract class FRBSParser<T> implements FileParser<T> {


    @Override
    public T read(String filePath) {
        try {
            return doRead(filePath);
        } catch (JAXBException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private T doRead(String filePath) throws JAXBException {
        JAXBContext content = getContent();
        Unmarshaller unmarshaller = content.createUnmarshaller();
        Object object = unmarshaller.unmarshal(new File(filePath));
        // TODO - Printing if asked
        return (T) object;
    }

    protected abstract JAXBContext getContent() throws JAXBException;
}