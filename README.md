# JFML - Trainer module

This project is part of the JFML and comprises the processes aimed at learning a Fuzzy Rule Based System (from now on, FRBS).

This tool lets the user perform any of these two operations:
- Building a knowledge base (from now on, KB) for a FRBS.
- Learning a rule base (from now on, RB) for a FRBS. The user should provide the KB or have it created.

Let it be reminded that a FRBS is mainly made up of a KB plus a RB.


## Building a KB

A KB consists of a list of variables, each of a domain. Assuming the domain is an interval, it implies a minimum value and a maximum value. Each variable must be split into different fuzzy sets.

For each variable, it is needed the following:
- Name.
- Minimum value.
- Maximum value.
- Granularity (number of fuzzy sets).
- Membership function for each fuzzy set.

The task to build a KB with this module depends on a file with the variable definition information. This file is a .txt file with a row per variable, and each row matching the following pattern:

````
name,minValue,maxValue,granularity,membershipFunctionType
````

Logically, `minValue < maxValue` must hold. `membershipFunctionType` must be one of the following:
- `triangular`.

As of now, only regular partitions are considered (`minValue` and `maxValue` with membership value 1).

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

The result of the execution of this task is an XML file following the FML syntax with the whole FRBS information, combining both the KB provided (or not) as well as the RB learnt.


## How to use

To use this module, JRE 11 is needed. This is the most general console command:

````
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
- `variableDefinitionPath`. Path of the file with the variable definition information. 