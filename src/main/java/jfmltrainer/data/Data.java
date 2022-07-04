package jfmltrainer.data;

import jfmltrainer.data.instance.Instance;
import lombok.Value;

import java.util.List;

@Value
public class Data<T extends Instance>  {
    List<T> instanceList;
}
