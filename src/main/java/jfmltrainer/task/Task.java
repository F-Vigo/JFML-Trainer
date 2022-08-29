package jfmltrainer.task;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum Task {
    RULE_BASE_TRAINER("RB"),
    KNOWLEDGE_BASE_BUILDER("KB"),
    GRAPHICS("G"),
    METRICS("M");

    private String value;

    Task(String value) {
        this.value = value;
    }

    public static Optional<Task> fromString(String value) {
        return Stream.of(Task.values())
                .filter(task -> task.getValue().equals(value))
                .findAny();
    }
}
