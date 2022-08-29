package jfmltrainer.aux;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JFMLRandomTest {

    @Test
    public void whenShuffle_thenTheNewListHasTheSameElements() {

        JFMLRandom.createObject(42);

        List<Integer> list = List.of(1,2,3,4);
        List<Integer> newList = JFMLRandom.getInstance().shuffle(list);

        Assert.assertEquals(list.size(), newList.size());

        Boolean soFarSoTrue = true;
        for (int i = 0; i < list.size(); i++) {
            soFarSoTrue = soFarSoTrue && list.contains(newList.get(i)) && newList.contains(list.get(i));
        }
        Assert.assertTrue(soFarSoTrue);
    }
}
