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

import coursier.core.Module
import coursier.graph.ModuleTree
import eu.cdevreeze.cs.trees.internal.moduletrees.ModuleTreeQueryFunctionApi
import eu.cdevreeze.cs.trees.internal.queryapi.TreeQueryApi

/**
 * Wrapper for ModuleTree that enriches it with the tree query API.
 *
 * @author Chris de Vreeze
 */
final case class RichModuleTree(underlying: ModuleTree) extends AnyVal with TreeQueryApi {

  import RichModuleTree.delegate
  import RichModuleTree.unwrapPredicate
  import RichModuleTree.wrap

  type E = RichModuleTree

  def findAllChildren: Seq[RichModuleTree] = {
    delegate.findAllChildren(underlying).map(wrap)
  }

  def findAllDescendantsOrSelf: Seq[RichModuleTree] = {
    delegate.findAllDescendantsOrSelf(underlying).map(wrap)
  }

  def filterDescendantsOrSelf(p: RichModuleTree => Boolean): Seq[RichModuleTree] = {
    delegate.filterDescendantsOrSelf(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findAllDescendants: Seq[RichModuleTree] = {
    delegate.findAllDescendants(underlying).map(wrap)
  }

  def filterDescendants(p: RichModuleTree => Boolean): Seq[RichModuleTree] = {
    delegate.filterDescendants(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findChild(p: RichModuleTree => Boolean): Option[RichModuleTree] = {
    delegate.findChild(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findDescendantOrSelf(p: RichModuleTree => Boolean): Option[RichModuleTree] = {
    delegate.findDescendantOrSelf(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findDescendant(p: RichModuleTree => Boolean): Option[RichModuleTree] = {
    delegate.findDescendant(underlying, unwrapPredicate(p)).map(wrap)
  }

  def module: Module = underlying.module

  def reconciledVersion: String = underlying.reconciledVersion

  def retainedVersion: String = underlying.retainedVersion
}

object RichModuleTree {

  private val delegate: ModuleTreeQueryFunctionApi.type = ModuleTreeQueryFunctionApi

  private def wrap(tree: ModuleTree): RichModuleTree = RichModuleTree(tree)

  private def unwrapPredicate(p: RichModuleTree => Boolean): ModuleTree => Boolean = { (tree: ModuleTree) =>
    p(wrap(tree))
  }
}
