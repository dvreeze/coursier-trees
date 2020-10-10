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

import coursier.core.Dependency
import coursier.graph.DependencyTree
import eu.cdevreeze.cs.trees.internal.dependencytrees.DependencyTreeQueryFunctionApi
import eu.cdevreeze.cs.trees.internal.queryapi.TreeQueryApi

/**
 * Wrapper for DependencyTree that enriches it with the tree query API.
 *
 * @author Chris de Vreeze
 */
final case class RichDependencyTree(underlying: DependencyTree) extends AnyVal with TreeQueryApi {

  import RichDependencyTree.delegate
  import RichDependencyTree.unwrapPredicate
  import RichDependencyTree.wrap

  type E = RichDependencyTree

  def findAllChildren: Seq[RichDependencyTree] = {
    delegate.findAllChildren(underlying).map(wrap)
  }

  def findAllDescendantsOrSelf: Seq[RichDependencyTree] = {
    delegate.findAllDescendantsOrSelf(underlying).map(wrap)
  }

  def filterDescendantsOrSelf(p: RichDependencyTree => Boolean): Seq[RichDependencyTree] = {
    delegate.filterDescendantsOrSelf(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findAllDescendants: Seq[RichDependencyTree] = {
    delegate.findAllDescendants(underlying).map(wrap)
  }

  def filterDescendants(p: RichDependencyTree => Boolean): Seq[RichDependencyTree] = {
    delegate.filterDescendants(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findChild(p: RichDependencyTree => Boolean): Option[RichDependencyTree] = {
    delegate.findChild(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findDescendantOrSelf(p: RichDependencyTree => Boolean): Option[RichDependencyTree] = {
    delegate.findDescendantOrSelf(underlying, unwrapPredicate(p)).map(wrap)
  }

  def findDescendant(p: RichDependencyTree => Boolean): Option[RichDependencyTree] = {
    delegate.findDescendant(underlying, unwrapPredicate(p)).map(wrap)
  }

  def dependency: Dependency = underlying.dependency

  def excluded: Boolean = underlying.excluded

  def reconciledVersion: String = underlying.reconciledVersion

  def retainedVersion: String = underlying.retainedVersion

  def dependencyVersionEqualsReconciledVersion: Boolean = dependency.version == reconciledVersion

  def dependencyVersionEqualsRetainedVersion: Boolean = dependency.version == retainedVersion
}

object RichDependencyTree {

  private val delegate: DependencyTreeQueryFunctionApi.type = DependencyTreeQueryFunctionApi

  private def wrap(tree: DependencyTree): RichDependencyTree = RichDependencyTree(tree)

  private def unwrapPredicate(p: RichDependencyTree => Boolean): DependencyTree => Boolean = { (tree: DependencyTree) =>
    p(wrap(tree))
  }
}
