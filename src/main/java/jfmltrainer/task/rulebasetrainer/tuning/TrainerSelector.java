package jfmltrainer.task.rulebasetrainer.tuning;

import jfmltrainer.args.Args;

public abstract class TrainerSelector {
    protected abstract void doTrain(Args args);
}
