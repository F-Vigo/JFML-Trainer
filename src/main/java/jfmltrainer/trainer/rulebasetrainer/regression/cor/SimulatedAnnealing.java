package jfmltrainer.trainer.rulebasetrainer.regression.cor;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimulatedAnnealing {

    private static Float COOLING_FACTOR = 0.9F;
    private static Integer COOLING_ITERATIONS = 150;
    private static Float PHI = 0.001F;
    private static Float MU = 0.0037F;

    private static Integer MAX_NEIGHBOURS = 1;
    private static Integer MAX_HITS = 1;


    public static <T> T run(
            List<T> searchSpace,
            Function<T,Predicate<T>> neighbourFilter,
            Function<T, Float> functionToMinimize,
            T initialSolution
            ) {

        ImmutablePair<ImmutablePair<T, Float>, Boolean> currentBestSolutionAndTargetValueAndKeepLooping = new ImmutablePair<>(
                new ImmutablePair<>(initialSolution, functionToMinimize.apply(initialSolution)),
                true
        );
        Float temperature = (float) (-MU / Math.log(PHI) * currentBestSolutionAndTargetValueAndKeepLooping.getLeft().getRight()); // Initial temperature
        int i = 0;
        Boolean keepLooping = true;
        while(keepLooping) {
            currentBestSolutionAndTargetValueAndKeepLooping = runCoolingIteration(
                    searchSpace,
                    neighbourFilter,
                    functionToMinimize,
                    currentBestSolutionAndTargetValueAndKeepLooping.getLeft(),
                    temperature
            );
            temperature = updateTemperature(temperature);
            i++;
            keepLooping = i < COOLING_ITERATIONS && currentBestSolutionAndTargetValueAndKeepLooping.getRight();
        }
        return currentBestSolutionAndTargetValueAndKeepLooping.getLeft().getLeft();
    }

    private static <T> ImmutablePair<ImmutablePair<T, Float>, Boolean> runCoolingIteration(List<T> searchSpace, Function<T, Predicate<T>> neighbourFilter, Function<T, Float> functionToMinimize, ImmutablePair<T, Float> initialBestSolutionAndTargetValue, Float temperature) {
        ImmutablePair<T, Float> currentBestSolutionAndTargetValue = initialBestSolutionAndTargetValue;
        Integer nHits = 0;
        Boolean keepLooping = true;
        Integer i = 0;

        while(keepLooping) {

            T neighbour = getNewSolution(currentBestSolutionAndTargetValue.getLeft(), searchSpace, neighbourFilter);
            Float neighbourTargetValue = functionToMinimize.apply(neighbour);
            Float targetValueIncrement = neighbourTargetValue - currentBestSolutionAndTargetValue.getRight();
            if(isNeighbourAccepted(targetValueIncrement, temperature)) {
                currentBestSolutionAndTargetValue = new ImmutablePair<>(neighbour, neighbourTargetValue);
                nHits++;
            }
            i++;

            if (nHits >= MAX_HITS || i >= MAX_NEIGHBOURS) {
                keepLooping = false;
                // Return _
            }
        }
        return new ImmutablePair<>(currentBestSolutionAndTargetValue, nHits == 0);
    }

    private static Float updateTemperature(Float temperature) {
        return COOLING_FACTOR * temperature;
    }

    private static Boolean isNeighbourAccepted(Float targetValueIncrement, Float temperature) { // Metropolis criterium
        return targetValueIncrement < 0 || Math.random() < Math.exp(-targetValueIncrement)/temperature;
    }

    private static <T> T getNewSolution(T currentSolution, List<T> searchSpace, Function<T, Predicate<T>> neighbourFilter) {
        List<T> neighborList = searchSpace.stream()
                .filter(neighbourFilter.apply(currentSolution))
                .collect(Collectors.toList());
        Collections.shuffle(neighborList);
        return Optional.ofNullable(neighborList.get(0))
                .orElseGet(() -> currentSolution);
    }
}
