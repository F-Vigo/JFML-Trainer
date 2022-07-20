package jfmltrainer.aux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFMLRandom {

    private int seed;

    public Float randReal() {
        return (float) Math.random();
    }

    public Float randReal(Float a, Float b) {
        Float x = (float) Math.random();
        return (b-a)*x + a;
    }

    public Integer randInt(Integer b) {
        return randInt(0, b);
    }

    public Integer randInt(Integer a, Integer b) {
        return (int) Math.round(randReal((float) a, (float) b));
    }

    public <T> List<T> shuffle(List<T> list) {
        List<T> newList = new ArrayList<>();
        newList.addAll(list);
        Collections.shuffle(newList);
        return newList;
    }
}
