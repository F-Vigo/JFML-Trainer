package jfmltrainer.task.rulebasetrainer.regression.anfis.layers;

import jfmltrainer.operator.and.AndOperatorPROD;
import jfmltrainer.task.rulebasetrainer.regression.anfis.AnfisUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnfisThirdLayer { // And operator

    public List<Float> run(List<List<Float>> input) {
        List<Integer> nTermList = input.stream()
                .map(List::size)
                .collect(Collectors.toList());
        List<List<Integer>> combinationList = AnfisUtils.getCombinationList(nTermList);
         return IntStream.range(0, combinationList.size()).boxed()
                .map(i -> getMembershipCombination(input, combinationList.get(i)))
                 .collect(Collectors.toList());
    }

    private Float getMembershipCombination(List<List<Float>> input, List<Integer> termIndexList) {
        List<Float> valuesToCombine = IntStream.range(0, input.size()).boxed()
                .map(i -> input.get(i).get(termIndexList.get(i)))
                .collect(Collectors.toList());
        return valuesToCombine.stream().reduce(AndOperatorPROD.getInstance()::apply).get();
    }


}
