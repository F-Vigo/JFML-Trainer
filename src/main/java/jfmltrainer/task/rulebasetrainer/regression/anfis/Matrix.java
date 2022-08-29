package jfmltrainer.task.rulebasetrainer.regression.anfis;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value
@AllArgsConstructor
public class Matrix {
    List<List<Float>> elems;

    Matrix(Integer m, Integer n) {
        this.elems = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            elems.set(i, new ArrayList<>(n));
            Collections.fill(elems.get(i), 0F);
        }
    }

    ImmutablePair<Integer, Integer> getShape() {
        return new ImmutablePair<>(elems.size(), elems.get(0).size());
    }

    Float get(Integer i, Integer j) {
        return elems.get(i).get(j);
    }

    void set(Integer i, Integer j, Float x) {
        elems.get(i).set(j, x);
    }

    Optional<Matrix> sum(Matrix that) {
        if (!that.getShape().equals(getShape())) {
            return Optional.empty();
        }
        List<List<Float>> resultElems = new ArrayList<>();
        for (int i = 0; i < getShape().getLeft(); i++) {
            List<Float> row = new ArrayList<>();
            for (int j = 0; j < getShape().getRight(); j++) {
                row.add(get(i, j) + that.get(i, j));
            }
            resultElems.add(row);
        }
        return Optional.of(new Matrix(resultElems));
    }

    Matrix times(Float x) {
        List<List<Float>> resultElems = new ArrayList<>();
        for (int i = 0; i < getShape().getLeft(); i++) {
            List<Float> row = new ArrayList<>();
            for (int j = 0; j < getShape().getRight(); j++) {
                row.add(get(i, j) * x);
            }
            resultElems.add(row);
        }
        return new Matrix(resultElems);
    }

    Optional<Matrix> dot(Matrix that) {
        if (getShape().getRight() != that.getShape().getLeft()) {
            return Optional.empty();
        }
        List<List<Float>> resultElems = new ArrayList<>();
        for (int i = 0; i < getShape().getLeft(); i++) {
            List<Float> v1 = elems.get(i);
            List<Float> row = new ArrayList<>();
            for (int j = 0; j < that.getShape().getRight(); j++) {
                int finalJ = j;
                List<Float> v2 = IntStream.range(0, that.getShape().getLeft()).boxed()
                        .map(k -> that.get(k, finalJ))
                        .collect(Collectors.toList());
                row.add(AnfisUtils.dot(v1, v2));
            }
            resultElems.add(row);
        }
        return Optional.of(new Matrix(resultElems));
    }
}
