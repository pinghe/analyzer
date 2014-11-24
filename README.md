analyzer
====

Karaf feature analyser Command

 analyzer - detect unrequired feature entires.

Description:

 analyzer processes Karaf feature file dependencies to determine which
 if any repositories or artifacts are not required for wiring. Note:
 Some resources may be flagged as non-required if they do not become
 wired in the container, users should only remove feature entries after
 testing.

Building from source:
===

To build, invoke:
 
 mvn install

analyzer installation:
===

To install in Karaf, invoke from console:

 install -s mvn:com.savoirtech.karaf.commands/analyzer


To execute command on Karaf, invoke:

 aetos:analyzer FeatureName [-s] [-v]

 -s are resources all available in system repo?

 -v Verbose output.
