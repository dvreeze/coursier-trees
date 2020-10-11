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

package eu.cdevreeze.cs.trees.internal.dependencytrees

import coursier.graph.DependencyTree
import eu.cdevreeze.cs.trees.internal.queryapi.TreeQueryFunctionApi

import scala.util.chaining._

/**
 * Tree query function API implementation for DependencyTree.
 *
 * @author Chris de Vreeze
 */
object RichDependencyTreeFunctions extends TreeQueryFunctionApi[DependencyTree] {

  def findAllChildren(tree: DependencyTree): Seq[DependencyTree] = {
    tree.children
  }

  def findAllDescendantsOrSelf(tree: DependencyTree): Seq[DependencyTree] = {
    filterDescendantsOrSelf(tree, _ => true)
  }

  def filterDescendantsOrSelf(tree: DependencyTree, p: DependencyTree => Boolean): Seq[DependencyTree] = {
    // Recursive calls
    findAllChildren(tree)
      .flatMap(e => filterDescendantsOrSelf(e, p))
      .pipe(descendants => if (p(tree)) descendants.prepended(tree) else descendants)
  }

  def findAllDescendants(tree: DependencyTree): Seq[DependencyTree] = {
    filterDescendants(tree, _ => true)
  }

  def filterDescendants(tree: DependencyTree, p: DependencyTree => Boolean): Seq[DependencyTree] = {
    findAllChildren(tree).flatMap(e => filterDescendantsOrSelf(e, p))
  }

  def findChild(tree: DependencyTree, p: DependencyTree => Boolean): Option[DependencyTree] = {
    tree.children.find(p)
  }

  def findDescendantOrSelf(tree: DependencyTree, p: DependencyTree => Boolean): Option[DependencyTree] = {
    if (p(tree)) {
      Some(tree)
    } else {
      // Recursive calls
      tree.children.view.flatMap(e => findDescendantOrSelf(e, p)).headOption
    }
  }

  def findDescendant(tree: DependencyTree, p: DependencyTree => Boolean): Option[DependencyTree] = {
    tree.children.view.flatMap(e => findDescendantOrSelf(e, p)).headOption
  }
}
