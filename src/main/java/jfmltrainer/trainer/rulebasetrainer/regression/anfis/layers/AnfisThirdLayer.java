package jfmltrainer.trainer.rulebasetrainer.regression.anfis.layers;

import jfmltrainer.operator.and.AndOperatorPROD;
import jfmltrainer.trainer.rulebasetrainer.regression.anfis.AnfisUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnfisThirdLayer {
    public List<Float> run(List<List<Float>> input) {
        List<Integer> nTermList = input.stream()
                .map(List::size)
                .collect(Collectors.toList());
        Integer nRules = nTermList.stream()
                .reduce((x,y) -> x*y)
                .get();

        List<List<Integer>> combinationList = AnfisUtils.getCombinationList(nTermList);

         return IntStream.range(0, input.size()).boxed()
                .map(i -> getMembershipCombination(input, combinationList.get(i)))
                 .collect(Collectors.toList());
    }





    private Float getMembershipCombination(List<List<Float>> input, List<Integer> termIndexList) {
        List<Float> valuesToCombine = IntStream.range(0, input.size()).boxed()
                .map(i -> input.get(i).get(termIndexList.get(i)))
                .collect(Collectors.toList());
        return valuesToCombine.stream().reduce(new AndOperatorPROD()::apply).get();
    }


}
