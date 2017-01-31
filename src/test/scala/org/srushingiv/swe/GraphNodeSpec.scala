package org.srushingiv.swe

import org.scalatest._
import _root_.org.srushingiv.swe.{SimpleGNode => GN}

class GraphNodeSpec extends WordSpec with Matchers {
  val j = GN("J")
  val i = GN("I")
  val h = GN("H")
  val g = GN("G")
  val f = GN("F")
  val e = GN("E")
  val d = GN("D", j)
  val c = GN("C", g, h, i)
  val b = GN("B", e, f)
  val simpleGraph = GN("A", b, c, d)

  class WalkTest(graph: GNode, expected: Set[String]) {
    val result = GNode.walkGraph(graph).map(_.getName)
    result.size shouldBe expected.size
    result should contain theSameElementsAs expected
  }

  "The walkGraph function" should {
    "return an empty list" when {
      "given null" in {
        GNode.walkGraph(null) shouldBe Nil
      }
    }

    "return all nodes" when {
      "all are unique" in new WalkTest(
        graph = simpleGraph,
        expected = Set("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
      )
    }

    "return only unique nodes" when {
      "nodes are repeated in the same level" in new WalkTest(
        graph = GN("X", d, d, d),
        expected = Set("X", "D", "J")
      )

      "nodes are repeated in a later sibling's children" in new WalkTest(
        graph = GN("Y", j, d),
        expected = Set("Y", "D", "J")
      )

      "a previous sibling's child is repeated" in new WalkTest(
        graph = GN("Z", d, j),
        expected = Set("Z", "D", "J")
      )

      "nodes are repeated in a later sibling's descendants" in new WalkTest(
        graph = GN("X", j, GN("Y", d)),
        expected = Set("X", "Y", "D", "J")
      )

      "a previous sibling's descendant is repeated" in new WalkTest(
        graph = GN("X", GN("Y", d), j),
        expected = Set("X", "Y", "D", "J")
      )

      "a previous sibling's descendant is repeated in a later sibling's descendant" in new WalkTest(
        graph = GN("X", GN("Y", GN("Z", j)), GN("A", GN("B", j))),
        expected = Set("X", "Y", "Z", "A", "B", "J")
      )

      "nodes have matching hash values" in {
        val graph = GN("A", GN("A"), GN("A"), GN("A", GN("A"), GN("A", GN("A"))), GN("A", GN("A")))
        val result = GNode.walkGraph(graph).map(_.getName)
        val expected = List("A", "A", "A", "A")
        result shouldBe expected
      }
    }

    "return all nodes with matching names when they are not the same node" in {
      import GraphNodeSpec.{ComplexGNode => N}
      val graph = N("A", 1, N("A", "B"), N("A", false), N("A", Nil, N("A", 10.0)))
      val result = GNode.walkGraph(graph).map(_.getName)
      val expected = List("A", "A", "A", "A", "A")
      result shouldBe expected
    }
  }
  
  "The paths function" should {
    "return an empty list" when {
      "given null" in {
        GNode.paths(null) shouldBe Nil
      }
    }

    "return all paths" when {
      "not given null" in {
        val result = GNode.paths(simpleGraph)
        val printable = result.map(_.map(_.getName).mkString("(", " ", ")")).mkString("( "," ", " )")
        val expected = "( (A B E) (A B F) (A C G) (A C H) (A C I) (A D J) )"
        printable shouldBe expected
      }
    }
  }
}

object GraphNodeSpec {
  case class ComplexGNode[T](name: String, foo: T, children: GNode *) extends GNode {
    def getName = name
    def getChildren = children.toList
  }
}
