package org.srushingiv.swe

trait GNode {
  def getName: String
  
  /**
   * @return a non-cyclic List of child nodes or Nil, never null. 
   */
  def getChildren: List[GNode]

  /**
   * Performs a top-down, left-to-right traversal over the portions of the
   * graph matching the supplied predicate.
   */
  def fold[T](empty: T, predicate: (T, GNode) => Boolean = (_:T, _:GNode) => true)(accumulate: (T, GNode) => T): T = {
    def helper(accumulated: T, node: GNode): T = {
      if (!predicate(accumulated, node)) accumulated
      else node.getChildren.foldLeft(accumulate(accumulated, node))(helper)
    }
    helper(empty, this)
  }

  /**
   * Derives a set of unique nodes, from the root node and all it's descendants.
   */
  def uniqueNodes: Set[GNode] =
    fold(Set.empty[GNode], (s:Set[GNode], n) => !s.contains(n))(_ + _)

  def paths: List[List[GNode]] = getChildren match {
    case Nil => List(List(this))
    case list => list flatMap ( _.paths ) map ( this :: _ )
  }

}

// This case class will help with writing unit tests.
case class SimpleGNode(name: String, children: List[GNode]) extends GNode {
  // NOTE: idiomatic Scala would indicate returning Optional values when using
  // the "get" prefix, but we'll conform to the trait as specified in the problem
  override def getName = name
  override def getChildren = children
  override def toString = children match {
    case Nil => name
    case _ => name + children.mkString(" (", " ", ")")
  }
}

object SimpleGNode {
  // convenience method for instantiating SimpleGNodes...
  def apply(name: String, children: GNode *): SimpleGNode =
    new SimpleGNode(name, children.toList)
}

object GNode {

  // NOTE: Since the problem definition specifically mentions null values in
  // assumptions about the GNode data type, but not about function calls, then
  // I must conclude that input cannot be assumed to be null-safe
  def walkGraph(node: GNode): List[GNode] = Option(node) map (_.uniqueNodes.toList) getOrElse Nil

  def paths(node: GNode): List[List[GNode]] = Option(node) map (_.paths) getOrElse Nil

}
