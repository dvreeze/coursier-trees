/*
 * Copyright 2020-2020 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.cs.trees.api

import coursier._
import coursier.graph.ModuleTree
import org.scalatest.LoneElement._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * RichModuleTree test. It may require internet access, due to Coursier Resolve calls.
 *
 * @author Chris de Vreeze
 */
class RichModuleTreeTest extends AnyFunSuite with Matchers {

  test("testDepthFirst") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomModTree: RichModuleTree =
      RichModuleTree(ModuleTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val allModules: Seq[Module] = yaidomModTree.findAllDescendantsOrSelf.map(_.module)

    val expectedModules: Seq[Module] = List(
      mod"eu.cdevreeze.yaidom:yaidom_2.13",
      mod"com.github.ben-manes.caffeine:caffeine",
      mod"com.google.errorprone:error_prone_annotations",
      mod"org.checkerframework:checker-qual",
      mod"com.google.code.findbugs:jsr305",
      mod"net.sf.saxon:Saxon-HE",
      mod"org.scala-lang:scala-library",
      mod"org.scala-lang.modules:scala-xml_2.13",
      mod"org.scala-lang:scala-library",
    )

    (allModules should contain).theSameElementsInOrderAs(expectedModules)
  }

  test("testFindDuplicateModules") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomModTree: RichModuleTree =
      RichModuleTree(ModuleTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val scalaLibraryTrees: Seq[RichModuleTree] =
      yaidomModTree.filterDescendants(_.module == mod"org.scala-lang:scala-library")

    (scalaLibraryTrees.map(_.module).distinct should have).size(1)
    (scalaLibraryTrees.map(_.module) should have).size(2)
    scalaLibraryTrees.map(_.retainedVersion).toSet.loneElement shouldBe "2.13.2"
  }

  test("testQuerySameTreeFromDifferentResolutions") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomModTree: RichModuleTree =
      RichModuleTree(ModuleTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val tqaResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.tqa:tqa_2.13:0.8.18")
      .run()

    val tqaModTree: RichModuleTree =
      RichModuleTree(ModuleTree.one(tqaResolution, tqaResolution.rootDependencies.head))

    val foundYaidomModTree: RichModuleTree =
      tqaModTree
        .findDescendant(_.module == mod"eu.cdevreeze.yaidom:yaidom_2.13")
        .ensuring(_.nonEmpty)
        .get

    foundYaidomModTree.findAllDescendantsOrSelf.map(_.module) shouldBe yaidomModTree.findAllDescendantsOrSelf.map(
      _.module)

    yaidomModTree
      .filterDescendants(_.module == mod"org.scala-lang:scala-library")
      .map(_.retainedVersion)
      .toSet
      .loneElement shouldBe "2.13.2"

    foundYaidomModTree
      .filterDescendants(_.module == mod"org.scala-lang:scala-library")
      .map(_.retainedVersion)
      .toSet
      .loneElement shouldBe "2.13.3"

    tqaModTree
      .filterDescendants(_.module == mod"org.scala-lang:scala-library")
      .map(_.retainedVersion)
      .toSet
      .loneElement shouldBe "2.13.3"
  }
}
