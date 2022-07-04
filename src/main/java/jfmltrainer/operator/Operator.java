package jfmltrainer.operator;

import java.util.Optional;

public interface Operator {

    String getName();
    <T extends Operator> Optional<T> fromString(String name);
}
