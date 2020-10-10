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

  test("testFindDuplicateModules") {
    val yaidomResolution = Resolve()
      .addDependencies(dep"eu.cdevreeze.yaidom:yaidom_2.13:1.11.0")
      .run()

    val yaidomModTree: RichModuleTree =
      RichModuleTree(ModuleTree.one(yaidomResolution, yaidomResolution.rootDependencies.head))

    val scalaLibraryTrees: Seq[RichModuleTree] =
      yaidomModTree.filterDescendants(_.module == mod"org.scala-lang:scala-library")

    (scalaLibraryTrees.map(_.module) should have).size(2)
    scalaLibraryTrees.map(_.retainedVersion).toSet.loneElement shouldBe "2.13.2"
  }
}
