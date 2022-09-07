package jfmltrainer.task.rulebasetrainer.tuning.lateral;

import java.util.ArrayList;
import java.util.List;

public class GrayCoder {

    Integer bitsgene;

    public GrayCoder(Integer bitsgene) {
        this.bitsgene = bitsgene;
    }

    public List<Character> toGrayCode(Chromosome chromosome) {
        double increment = 1. / bitsgene - 1;
        List<Character> result = new ArrayList<>();
        int pos = 0;
        for (int i = 0; i < chromosome.getGeneList().size(); i++) {
            double n = (chromosome.getGeneList().get(i)+0.5) / increment + 0.5;
            List<Character> auxList = intToCharList((int) n);
            result = toGrayCodeAux(auxList, result, pos);
            pos += bitsgene;
        }
        return result;
    }

    private List<Character> toGrayCodeAux(List<Character> inputList, List<Character> outputList, int pos) {
        char last = '0';
        for (int i = 0; i < bitsgene; i++) {
            int aux = inputList.get(i) != last ? 1 : 0;
            char newChar = (char)('0'+aux);
            int outputSizeSoFar = outputList.size();
            for (int j = outputSizeSoFar; j <= pos+i; j++) {
                outputList.add(null);
            }
            outputList.set(pos+i, newChar);
            last = inputList.get(i);
        }
        return outputList;
    }

    private List<Character> intToCharList(int n0) {
        int n = n0;
        List<Character> charList = new ArrayList<>();
        for (int i = bitsgene -1; i >= 0; i--) {
            char newChar = (char) ('0' + (n & 1));
            charList.add(newChar);
            n >>= 1;
        }
        return charList;
    }
}
