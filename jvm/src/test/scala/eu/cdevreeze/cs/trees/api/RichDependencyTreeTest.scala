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
import coursier.graph.DependencyTree
import org.scalatest.LoneElement._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * RichDependencyTree test. It may require internet access, due to Coursier Resolve calls.
 *
 * @author Chris de Vreeze
 */
class RichDependencyTreeTest extends AnyFunSuite with Matchers {

  test("testDepthFirst") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomDepTree: RichDependencyTree =
      RichDependencyTree(DependencyTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val allModules: Seq[Module] = yaidomDepTree.findAllDescendantsOrSelf.map(_.dependency.module)

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

    val yaidomDepTree: RichDependencyTree =
      RichDependencyTree(DependencyTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val scalaLibraryTrees: Seq[RichDependencyTree] =
      yaidomDepTree.filterDescendants(_.dependency.module == mod"org.scala-lang:scala-library")

    (scalaLibraryTrees.map(_.dependency.moduleVersion) should have).size(2)
    scalaLibraryTrees.map(_.retainedVersion).toSet.loneElement shouldBe "2.13.2"
  }

  test("testFindVersionEvictions") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomDepTree: RichDependencyTree =
      RichDependencyTree(DependencyTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val depTreesWithEviction: Seq[RichDependencyTree] =
      yaidomDepTree.filterDescendantsOrSelf(t => !t.dependencyVersionEqualsRetainedVersion)

    (depTreesWithEviction should have).size(1)
    depTreesWithEviction.head.dependency.module shouldBe mod"org.scala-lang:scala-library"
    depTreesWithEviction.head.dependency.version shouldBe "2.13.0"
    depTreesWithEviction.head.reconciledVersion shouldBe "2.13.2"
    depTreesWithEviction.head.retainedVersion shouldBe "2.13.2"

    yaidomDepTree
      .findDescendant(_.dependency.moduleVersion == (mod"org.scala-lang:scala-library", "2.13.2")) should not be empty
  }
}
