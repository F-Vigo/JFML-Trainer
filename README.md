NOTE: This README is meant to be a brief description of this project's usage. For further details, visit www.jfml.es.


# JFML - Trainer module

This project is part of the JFML and comprises the processes aimed at learning a Fuzzy Rule Based System (from now on, FRBS). Let it be reminded that a FRBS is mainly made up of a KB plus a RB.

This tool lets the user perform any of these four operations, named tasks:
- Building a knowledge base (from now on, KB) for a FRBS.
- Learning a rule base (from now on, RB) for a FRBS. The user should provide the KB or have it created.
- Computing the quality metrics of a FRBS given a dataset.
- Represent a FRBS via a graphic for the KB and another one for the RB.


## Building a KB

As a KB is essentially a set of variables, to build a KB consist of specifying the data for each variable. A variable can be quantitative (numeric) or qualitative, and the information needed for each class is different. For any variable, it is needed to know the following:
- Name.
- Whether it is input or output.
- Whether it is quantitative or qualitative.

Besides, a quantitative variable needs the following:
- Minimum value.
- Maximum value.
- Granularity.

A qualitative one, however, needs just the list of labels.

The task of building a KB uses the following:
- A variable definition data file.
- If not provided, a data file with a granularity list (a value per variable)
- If the granularity list is not provided, a single value for all values.
- If such single value is not provided, a default one will be used.

The variable definition data file consist of a row per variable. If the variable is quantitative, it should match this pattern:

````
name,isInput,isQuantitative,minValue,maxValue,granularity
````

where name is a string, isInput is either 1 (yes) or 0 (no), isQuantitative is 1, minValue and maxValue are floats and granularity is an integer.

A qualitative variable is specified like this:

````
name,isInput,isQuantitative,label1,...,labelN
````
 
where isQuantitative is 0.

If no such file is provided, the program will need to infer the variable specifications from a data set. The granularity must be specified via a separate argument, either as a list (as long as the number of variables) or as a single value, or omitted for default.

The result of the execution of this task is an XML file following the FML syntax with the KB information.


## Learning a RB

An RB is basically a set of fuzzy rules matching the variables defined in the KB. This set is trained via Machine Learning with a data set.

The data file should be a .txt file with a row per instance, and each row matching the following pattern:

````
valueVariable1,valueVariable2,...,valueVariableN,valueOutputVariable
````

These are the methods implemented so far:
- Regression:
  - Wang & Mendel (Standard)
  - Wang & Mendel (Cordón & Herrera)
  - TBD
- Classification:
  - TBD
- Tuning:
  - TBD

This task needs a KB to work.
- If a KB file is not provided, it will try to build it if a variable definition file is specified.
- If it is not either, a default KB should be built using the range of the data values as domain and triangular membership functions. (TBC)

The result of the execution of this task is an XML file following the FML syntax with the whole FRBS information, combining the KB provided (or not) as well as the RB learnt.


## Quality metrics

This task needs a FRBS and a data set (the test data set) to produce a file summarising the main quality metrics of the FRBS. The task adapts depending on whether FRBS is for regression or classification.

- Regression:
  - MSE.
  - RMSE.
  - MAE.
  - GMSE.
- Classification (using an OVA approach):
  - Accuracy.
  - Precision.
  - Sensitivity.
  - Specificity.
  - F1 score.

The metrics will be saved in a folder `metrics`, and its name will depend on the timestamp.


## Plotting a FRBS

This process takes a FRBS, or a KB and a RB separately, as input and produces two graphics: one for the first and another one for the latter. Another FRBS (or another KB and another RB) may be provided to represent both FRBS in the same graphics. This aims at represent the effect of a tuning process.


## How to use

To use this module, JRE 11 is needed. This is the most general console command:

````
java -jar <jarPath> -t <task> <otherArguments>
````

`task` can take the value of `KB` (to build a KB), `RB` (to learn a RB), `G` (to plot a FRBS, or two), `M` (to compute quality metrics).

Here follows an example of a call:

````
java -jar ./JFML-Trainer.jar -t RB -m WM -d ./data.txt -kb ./kb.xml -ao PROD -of results -on result.xml
````

This call would precede an RB learning process via a given dataset and a given KB, by using the method Wang & Mendel and the product as the AND operator. The result will be stored in an output folder `results`, the output name being `result.xml`. 

For further instructions, visit www.jfml.es.






<!--````
java -jar <jarPath> -t <task> -m <method> -d <dataPath> -kb <knowledgeBasePath> -ao <andOperator> -oo <orOperator> -to <thenOperator> -rvf <rvfOperator> -cs <combinatorialSearch> -of <outputFolder> -on <outputName> -vd <variableDefinitionPath>
````

Not all arguments are mandatory, and the order does not have to match the one in the pattern.

The arguments are:
- `task`. `KB` to build a KB, and `RB` to learn an RB. Mandatory.
- `method`. Parametre used when learning an RB (mandatory for learning an RB):
  - `WMS`: Wang & Mendel (Standard).
  - `MWCH`: Wand & Mendel (Cordón & Herrera).
- `dataPath`. Path of the data .txt file (mandatory for learning an RB).
- `knowledgeBasePath`. Path of the knowledge base file.
- `andOperator`. Operator for fuzzy intersection. Its possible values are:
  - `MIN`: Minimum (default operator).
  - `PROD`: Product.
- `orOperator`. Operator for fuzzy union. Its possible values are:
  - `MAX`: Maximum (default operator).
- `thenOperator`. Operator for fuzzy implications. Its possible values are:
  - `MIN`: Minimum (default operator).
- `rvfOperator`. RVF function to be used in Cordon & Herrera's method. Its possible values are:
  - `MAX`: Maximum (default operator).
  - `MEAN`: Mean.
  - `PROD`: Product of the two previous options.
- `combinatorialSearch`. Combinatorial search method used in COR. Its possible values are:
  - `EXP`: Explicit enumeration.
  - `SA`: Approximate search with Simulated Annealing.
  - `ACO`: Approximate search with Ant Colony Optimization.
- `outputFolder`. Folder to save the output file.
- `outputName`. Name for the output file.
- `variableDefinitionPath`. Path of the file with the variable definition information.--> 