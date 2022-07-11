package jfmltrainer.trainer.rulebasetrainer.regression.anfis;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PipeFlow<T> {
    T content;

    public <U> PipeFlow<U> then(Function<T, U> f) {
        return new PipeFlow<>(f.apply(content));
    }

    public <T> T get() {
        return (T) content;
    }
}
