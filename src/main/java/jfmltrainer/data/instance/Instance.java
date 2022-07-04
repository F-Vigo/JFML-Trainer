package jfmltrainer.data.instance;

import jfmltrainer.method.Problem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
public abstract class Instance<T> {
    Problem problem;
    List<Float> antecedentValueList;
    List<T> consequentValueList;
}
