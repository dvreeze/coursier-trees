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
import org.scalatest.funsuite.AnyFunSuite

/**
 * RichDependencyTree test. It may require internet access, due to Coursier Resolve calls.
 *
 * @author Chris de Vreeze
 */
class RichDependencyTreeTest extends AnyFunSuite {

  test("testFindDuplicateModules") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomDepTree: RichDependencyTree =
      RichDependencyTree(DependencyTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val scalaLibraryTrees: Seq[RichDependencyTree] =
      yaidomDepTree.filterDescendants(_.underlying.dependency.module == mod"org.scala-lang:scala-library")

    assert(scalaLibraryTrees.size == 2)
    assert(scalaLibraryTrees.map(_.underlying.retainedVersion).toSet == Set("2.13.2"))
  }

  test("testFindVersionEvictions") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomDepTree: RichDependencyTree =
      RichDependencyTree(DependencyTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val depTreesWithEviction: Seq[RichDependencyTree] =
      yaidomDepTree.filterDescendantsOrSelf(t => t.underlying.dependency.version != t.underlying.retainedVersion)

    assert(depTreesWithEviction.size == 1)
    assert(depTreesWithEviction.head.underlying.dependency.module == mod"org.scala-lang:scala-library")
    assert(depTreesWithEviction.head.underlying.dependency.version == "2.13.0")
    assert(depTreesWithEviction.head.underlying.reconciledVersion == "2.13.2")
    assert(depTreesWithEviction.head.underlying.retainedVersion == "2.13.2")

    assert(
      yaidomDepTree
        .findDescendant(_.underlying.dependency.moduleVersion == (mod"org.scala-lang:scala-library", "2.13.2"))
        .nonEmpty)
  }
}
