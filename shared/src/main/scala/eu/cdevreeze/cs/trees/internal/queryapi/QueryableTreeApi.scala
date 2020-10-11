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

package eu.cdevreeze.cs.trees.internal.queryapi

/**
 * Tree query API. The query method names are inspired by XPath axis names.
 *
 * This query API is for trees having one type of element as its nodes. It expects trees of rather limited depths
 * (like is the case for XML trees). Its query methods are typically not stack-safe. Its query methods return
 * the resulting sub-trees in depth-first order (comparable to document order for XML trees).
 *
 * @author Chris de Vreeze
 */
trait QueryableTreeApi extends Any {

  type E <: QueryableTreeApi

  def findAllChildren: Seq[E]

  def findAllDescendantsOrSelf: Seq[E]

  def filterDescendantsOrSelf(p: E => Boolean): Seq[E]

  def findAllDescendants: Seq[E]

  def filterDescendants(p: E => Boolean): Seq[E]

  def findChild(p: E => Boolean): Option[E]

  def findDescendantOrSelf(p: E => Boolean): Option[E]

  def findDescendant(p: E => Boolean): Option[E]
}
