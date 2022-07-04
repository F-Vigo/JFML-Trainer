package jfmltrainer.trainer.tuning.lateral;

import lombok.Value;

import java.util.List;

@Value
public class Chromosome {
    List<Float> geneList;
    List<Boolean> selectedRuleList;
}
