package jfmltrainer.fileparser;

public interface FileParser<T> {
    public T read(String filePath);
}
