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

package eu.cdevreeze.cs.trees.internal.moduletrees

import coursier.graph.ModuleTree
import eu.cdevreeze.cs.trees.internal.queryapi.TreeQueryFunctionApi

import scala.util.chaining._

/**
 * Tree query API implementation for ModuleTree.
 *
 * @author Chris de Vreeze
 */
object ModuleTreeQueryFunctionApi extends TreeQueryFunctionApi[ModuleTree] {

  def findAllChildren(tree: ModuleTree): Seq[ModuleTree] = {
    tree.children
  }

  def findAllDescendantsOrSelf(tree: ModuleTree): Seq[ModuleTree] = {
    filterDescendantsOrSelf(tree, _ => true)
  }

  def filterDescendantsOrSelf(tree: ModuleTree, p: ModuleTree => Boolean): Seq[ModuleTree] = {
    // Recursive calls
    findAllChildren(tree)
      .flatMap(e => filterDescendantsOrSelf(e, p))
      .pipe(descendants => if (p(tree)) descendants.prepended(tree) else descendants)
  }

  def findAllDescendants(tree: ModuleTree): Seq[ModuleTree] = {
    filterDescendants(tree, _ => true)
  }

  def filterDescendants(tree: ModuleTree, p: ModuleTree => Boolean): Seq[ModuleTree] = {
    findAllChildren(tree).flatMap(e => filterDescendantsOrSelf(e, p))
  }

  def findChild(tree: ModuleTree, p: ModuleTree => Boolean): Option[ModuleTree] = {
    tree.children.find(p)
  }

  def findDescendantOrSelf(tree: ModuleTree, p: ModuleTree => Boolean): Option[ModuleTree] = {
    if (p(tree)) {
      Some(tree)
    } else {
      // Recursive calls
      tree.children.view.flatMap(e => findDescendantOrSelf(e, p)).headOption
    }
  }

  def findDescendant(tree: ModuleTree, p: ModuleTree => Boolean): Option[ModuleTree] = {
    tree.children.view.flatMap(e => findDescendantOrSelf(e, p)).headOption
  }
}
