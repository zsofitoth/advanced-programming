sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree{
  def size[A](t: Tree[A]): Int = t match {
    case Leaf(_) => 1
    case Branch(l, r) => 1 + size(l) + size(r)
  }
}

val tree = Branch(
  Branch(
    Leaf(12),
    Branch(
      Leaf(3),
      Leaf(4))),
  Leaf(8))

Tree.size(tree)