package jfmltrainer.task.metrics.computer;

import jfml.FuzzyInferenceSystem;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rulebase.RuleBaseType;
import jfmltrainer.aux.Utils;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.Instance;
import jfmltrainer.task.metrics.measure.Measures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VariableMetricsComputer<T> {

    public List<Measures> computeMetrics(FuzzyInferenceSystem frbs, Data testSet) {
        List<List<T>> realValueList = (List<List<T>>) testSet.getInstanceList().stream()
                .map(x -> ((Instance) x).getConsequentValueList())
                .collect(Collectors.toList());
        Integer consequentSize = realValueList.get(0).size();
        List<List<T>> predictedValueList = getPredictedValueList(frbs, testSet);
        List<KnowledgeBaseVariable> varList = frbs.getKnowledgeBase().getKnowledgeBaseVariables().stream()
                .filter(KnowledgeBaseVariable::isOutput)
                .collect(Collectors.toList());

        List<Measures> measuresList = new ArrayList<>();
        for (int i = 0; i < consequentSize; i++) {
            int finalI = i;
            measuresList.add(computeMeasures(
                    realValueList.stream()
                            .map(xs -> xs.get(finalI))
                            .collect(Collectors.toList()),
                    predictedValueList.stream()
                            .map(xs -> xs.get(finalI))
                            .collect(Collectors.toList()),
                    varList.get(i)
                    ));
        }
        return measuresList;
    }

    protected abstract Measures computeMeasures(List<T> realValueList, List<T> predictedValueList, KnowledgeBaseVariable varList);

    private List<List<T>> getPredictedValueList(FuzzyInferenceSystem frbs, Data testSet) {
        return ((List<Instance>) testSet.getInstanceList()).stream()
                .map(instance -> predictInstance(frbs, instance))
                .collect(Collectors.toList());
    }

    private List<T> predictInstance(FuzzyInferenceSystem frbs, Instance instance) {
        return Utils.predict(instance, ((RuleBaseType) frbs.getRuleBase(0)));
    }
}
