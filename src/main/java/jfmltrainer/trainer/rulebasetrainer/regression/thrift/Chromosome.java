package jfmltrainer.trainer.rulebasetrainer.regression.thrift;

import jfml.term.FuzzyTermType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chromosome {
    List<Optional<FuzzyTermType>> geneList;
    @Setter
    Float fitness;
}
