Aeminium Runtime

This project is part of the Æminium Language (http://aeminium.dei.uc.pt/).

This is a Java library that executes Directed Acyclic Graphs (DAG) of tasks.
The original code is written inside Tasks that are scheduled to the runtime
and executed asynchronously.

Æminium Programmers are not expected to write programs using this API. Instead,
they write programs in the Æminium Language and the compiler generates the Java
code that uses this API. Nevertheless, the API is available for more low-level
tuning.


How to use:

The project uses maven to manage the build process.

"mvn test" will run the tests.

"mvn package" will generate a jar in the target folder.


Source Code organization:

aeminium.runtime => Public API
aeminium.runtime.examples => Some examples of how to use the API. Bigger
	programs are available in the AeminiumBenchmarks project.
aeminium.runtime.implementations => Contains an implementation of the runtime,
	containing a workstealing approach.
aeminium.runtime.tests => Unit tests.


Config File:

Inside each AeminiumRuntime project, you may place a aeminiumrt.config file
containing the configuration for your project. This may contain default values
for debugging (including graphviz output), performance settings as well as
parallelization thresholds.
