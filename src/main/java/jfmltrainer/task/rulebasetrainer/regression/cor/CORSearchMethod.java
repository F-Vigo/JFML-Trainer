package jfmltrainer.task.rulebasetrainer.regression.cor;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

public enum CORSearchMethod {
    EXPLICIT_ENUMERATION("EXP"),
    SA("SA");

    @Getter
    String name;

    CORSearchMethod(String name) {
        this.name = name;
    }

    public static Optional<CORSearchMethod> fromString(String name) {
        return Stream.of(CORSearchMethod.values())
                .filter(value -> value.getName().equals(name))
                .findAny();
    }
}
