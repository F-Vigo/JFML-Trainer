package jfmltrainer.trainer.rulebasetrainer.regression.thrift;

import jfml.term.FuzzyTermType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class Chromosome {
    List<Optional<FuzzyTermType>> geneList;
    @Setter
    Float fitness;
}
