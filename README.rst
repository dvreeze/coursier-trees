==============
Coursier-trees
==============


Coursier-trees is a small library that adds querying support for Coursier dependency trees and module trees.
This API is inspired by the yaidom XML query API.

Usage
=====

Coursier-trees versions can be found in the Maven central repository. Assuming version 0.1.0, coursier-trees can be added as
dependency as follows (in an SBT or Maven build):

**SBT**::

    libraryDependencies += "eu.cdevreeze.coursier-extra" %%% "coursier-trees" % "0.1.0"

**Maven2**::

    <dependency>
      <groupId>eu.cdevreeze.coursier-extra</groupId>
      <artifactId>coursier-trees_2.13</artifactId>
      <version>0.1.0</version>
    </dependency>

This library only depends on coursier-core and its dependencies.

