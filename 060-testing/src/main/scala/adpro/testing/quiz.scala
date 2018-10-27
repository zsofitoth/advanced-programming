//find and fix the bug in the second property test below

def sequence[A](aos: List[Option[A]]): Option[List[A]] = {
    aos.foldRight[Option[List[A]]](Some(Nil: List[A])){
        (oa,z) => z flatMap (l => oa map (_::l))
    }
}

behavior of "sequence"

it should "succeed if the list has no failures" in check {
    //specifically interested in a list with no failures 
    implicit def arbList[A] (implicit arb: Arbitrary[List[A]]) = 
        Arbitrary[List[Option[A]]](arb.arbitrary map {_ map (Some(_))})
    //list without Nones
    //override default generators
    
    forAll { (l :List[Option[Int]]) => sequence(l).isDefined }

}

it should "fail if the list has at least one failure" in check {
    
    //we have to make sure l is a failing list. The default generator does not guarantee that we get a list where it will fail
    //list that contains at least one None

    implicit def arbFailingList[A] (implicit arb: Arbitrary[List[Option[A]]]) = 
        Arbitrary[List[Option[A]]](arb.arbitrary filter {_ exists (_.isEmpty)})

    forAll { (l :List[Option[Int]]) => sequence(l).isEmpty }
}
