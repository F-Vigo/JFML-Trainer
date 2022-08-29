package jfmltrainer.task.rulebasetrainer.classification.furia.irep;

import jfmltrainer.data.instance.ClassificationInstance;
import lombok.Value;

@Value
class BinaryIREPInstance {
    ClassificationInstance instance;
    Boolean isPositive;
}