package jfmltrainer.task.knowledgebasebuilder;

import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfmltrainer.data.Data;
import jfmltrainer.data.instance.RegressionInstance;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.QuantitativeVariableDefinitionData;
import jfmltrainer.task.knowledgebasebuilder.variabledefinition.VariableDefinitionData;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class KnowledgeBaseBuilderTest {

    @Test
    public void whenBuild_withVariableDefinitionData_thenKnowledgeBaseIsBuild() {

        QuantitativeVariableDefinitionData variableDefinitionData = new QuantitativeVariableDefinitionData("varName", true, 0F, 10F, 3);
        KnowledgeBaseType knowledgeBase = KnowledgeBaseBuilder.build(
                Optional.of(Collections.singletonList(variableDefinitionData)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Assert.assertEquals(1, knowledgeBase.getKnowledgeBaseVariables().size());
        Assert.assertEquals(variableDefinitionData.getName(), knowledgeBase.getKnowledgeBaseVariables().get(0).getName());

        KnowledgeBaseVariable variable = knowledgeBase.getKnowledgeBaseVariables().get(0);
        Float expectedMin = variableDefinitionData.getMinValue();
        Float expectedMax = variableDefinitionData.getMaxValue();
        Float actualMin = ((FuzzyVariableType) variable).getDomainleft();
        Float actualMax = ((FuzzyVariableType) variable).getDomainright();


        Assert.assertEquals(expectedMin, actualMin);
        Assert.assertEquals(expectedMax, actualMax);

        Integer expectedGranularity = variableDefinitionData.getGranularity();
        Integer actualGranularity = variable.getTerms().size();

        Assert.assertEquals(expectedGranularity, actualGranularity);
    }


    @Test
    public void whenBuild_withInstanceDataAndGranularityList_thenKnowledgeBaseIsBuilt() {

        RegressionInstance instance1 = new RegressionInstance(Collections.singletonList(1F), Collections.singletonList(3F));
        RegressionInstance instance2 = new RegressionInstance(Collections.singletonList(2F), Collections.singletonList(4F));
        Data<RegressionInstance> data = new Data<>(List.of(instance1, instance2));

        KnowledgeBaseType knowledgeBase = KnowledgeBaseBuilder.build(
                Optional.empty(),
                Optional.of(data),
                Optional.of(List.of(3,5)),
                Optional.empty()
        );

        Assert.assertEquals(2, knowledgeBase.getKnowledgeBaseVariables().size());

        KnowledgeBaseVariable variable1 = knowledgeBase.getKnowledgeBaseVariables().get(0);
        KnowledgeBaseVariable variable2 = knowledgeBase.getKnowledgeBaseVariables().get(1);

        Assert.assertEquals(true, variable1.isInput());
        Assert.assertEquals(false, variable2.isInput());

        Float expectedMin1 = 1F;
        Float expectedMax1 = 2F;
        Float actualMin1 = ((FuzzyVariableType) variable1).getDomainleft();
        Float actualMax1 = ((FuzzyVariableType) variable1).getDomainright();

        Assert.assertEquals(expectedMin1, actualMin1);
        Assert.assertEquals(expectedMax1, actualMax1);

        Float expectedMin2 = 3F;
        Float expectedMax2 = 4F;
        Float actualMin2 = ((FuzzyVariableType) variable2).getDomainleft();
        Float actualMax2 = ((FuzzyVariableType) variable2).getDomainright();

        Assert.assertEquals(expectedMin2, actualMin2);
        Assert.assertEquals(expectedMax2, actualMax2);

        Integer expectedGranularity1 = 3;
        Integer expectedGranularity2 = 5;
        Integer actualGranularity1 = variable1.getTerms().size();
        Integer actualGranularity2 = variable2.getTerms().size();

        Assert.assertEquals(expectedGranularity1, actualGranularity1);
        Assert.assertEquals(expectedGranularity2, actualGranularity2);
    }


    @Test
    public void whenBuild_withInstanceDataAndSingleGranularity_thenKnowledgeBaseIsBuilt() {

        RegressionInstance instance1 = new RegressionInstance(Collections.singletonList(1F), Collections.singletonList(3F));
        RegressionInstance instance2 = new RegressionInstance(Collections.singletonList(2F), Collections.singletonList(4F));
        Data<RegressionInstance> data = new Data<>(List.of(instance1, instance2));

        KnowledgeBaseType knowledgeBase = KnowledgeBaseBuilder.build(
                Optional.empty(),
                Optional.of(data),
                Optional.empty(),
                Optional.of(3)
        );

        Assert.assertEquals(2, knowledgeBase.getKnowledgeBaseVariables().size());

        KnowledgeBaseVariable variable1 = knowledgeBase.getKnowledgeBaseVariables().get(0);
        KnowledgeBaseVariable variable2 = knowledgeBase.getKnowledgeBaseVariables().get(1);

        Assert.assertEquals(true, variable1.isInput());
        Assert.assertEquals(false, variable2.isInput());

        Float expectedMin1 = 1F;
        Float expectedMax1 = 2F;
        Float actualMin1 = ((FuzzyVariableType) variable1).getDomainleft();
        Float actualMax1 = ((FuzzyVariableType) variable1).getDomainright();

        Assert.assertEquals(expectedMin1, actualMin1);
        Assert.assertEquals(expectedMax1, actualMax1);

        Float expectedMin2 = 3F;
        Float expectedMax2 = 4F;
        Float actualMin2 = ((FuzzyVariableType) variable2).getDomainleft();
        Float actualMax2 = ((FuzzyVariableType) variable2).getDomainright();

        Assert.assertEquals(expectedMin2, actualMin2);
        Assert.assertEquals(expectedMax2, actualMax2);

        Integer expectedGranularity = 3;
        Integer actualGranularity1 = variable1.getTerms().size();
        Integer actualGranularity2 = variable2.getTerms().size();

        Assert.assertEquals(expectedGranularity, actualGranularity1);
        Assert.assertEquals(expectedGranularity, actualGranularity2);
    }


    @Test
    public void whenBuild_withInstanceDataAndNoGranularity_thenKnowledgeBaseIsBuilt() {

        Integer DEFAULT_GRANULARITY = 5;

        RegressionInstance instance1 = new RegressionInstance(Collections.singletonList(1F), Collections.singletonList(3F));
        RegressionInstance instance2 = new RegressionInstance(Collections.singletonList(2F), Collections.singletonList(4F));
        Data<RegressionInstance> data = new Data<>(List.of(instance1, instance2));

        KnowledgeBaseType knowledgeBase = KnowledgeBaseBuilder.build(
                Optional.empty(),
                Optional.of(data),
                Optional.empty(),
                Optional.empty()
        );

        Assert.assertEquals(2, knowledgeBase.getKnowledgeBaseVariables().size());

        KnowledgeBaseVariable variable1 = knowledgeBase.getKnowledgeBaseVariables().get(0);
        KnowledgeBaseVariable variable2 = knowledgeBase.getKnowledgeBaseVariables().get(1);

        Assert.assertEquals(true, variable1.isInput());
        Assert.assertEquals(false, variable2.isInput());

        Float expectedMin1 = 1F;
        Float expectedMax1 = 2F;
        Float actualMin1 = ((FuzzyVariableType) variable1).getDomainleft();
        Float actualMax1 = ((FuzzyVariableType) variable1).getDomainright();

        Assert.assertEquals(expectedMin1, actualMin1);
        Assert.assertEquals(expectedMax1, actualMax1);

        Float expectedMin2 = 3F;
        Float expectedMax2 = 4F;
        Float actualMin2 = ((FuzzyVariableType) variable2).getDomainleft();
        Float actualMax2 = ((FuzzyVariableType) variable2).getDomainright();

        Assert.assertEquals(expectedMin2, actualMin2);
        Assert.assertEquals(expectedMax2, actualMax2);

        Integer actualGranularity1 = variable1.getTerms().size();
        Integer actualGranularity2 = variable2.getTerms().size();

        Assert.assertEquals(DEFAULT_GRANULARITY, actualGranularity1);
        Assert.assertEquals(DEFAULT_GRANULARITY, actualGranularity2);
    }
}
