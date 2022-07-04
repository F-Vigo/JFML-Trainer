package jfmltrainer.trainer.rulebasetrainer.regression.anfis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class AnfisUtils {

    public static Float dot(List<Float> v1, List<Float> v2) {
        return IntStream.range(0, v1.size()).boxed()
                .map(i -> v1.get(i) * v2.get(i))
                .reduce(Float::sum)
                .get();
    }

    public static List<List<Integer>> getCombinationList(List<Integer> nTermList) {
        List<Integer> initialCombination = new ArrayList<>(nTermList.size());
        Collections.fill(initialCombination, 0);
        return getCombinationListAux(nTermList, Collections.singletonList(initialCombination));
    }

    private static List<List<Integer>> getCombinationListAux(List<Integer> nTermList, List<List<Integer>> accum) {
        List<Integer> last = accum.get(accum.size()-1);
        if (last.equals(nTermList)) {
            return accum;
        } else {
            accum.add(nextItem(nTermList, last));
            return getCombinationListAux(nTermList, accum);
        }
    }

    private static List<Integer> nextItem(List<Integer> nTermList, List<Integer> last) {
        return nextItemAux(nTermList, last, nTermList.size()-1);
    }

    private static List<Integer> nextItemAux(List<Integer> nTermList, List<Integer> last, int k) {
        if (last.get(k) == nTermList.get(k)-1) {
            List<Integer> newItem = new ArrayList<>();
            for (int i = 0; i < k; i++) newItem.add(last.get(i));
            newItem.add(last.get(k)+1);
            for (int i = k+1; i < nTermList.size(); i++) newItem.add(0);
            return newItem;
        } else {
            return nextItemAux(nTermList, last, k-1);
        }
    }
}
