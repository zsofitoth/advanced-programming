# Advanced Programming
Functional Programming in Scala taught at ITU, Fall of 2018.

**Sources**
- Paul Chiusano, Runar Bjarnason; _Functional Programming in Scala_ (Manning 2014) [Chiusano, Bjarnason 2014] 
- Koen Claessen, John Hughes; _QuickCheck: a lightweight tool for random testing of Haskell programs_ [Claessen, Hughes 2000]

## Weekly Exercise Sets
### 01 - Intro
#### Topics and Exercises
- pure functions
- referential transparency (RT)
- higher order functions (HFOs)
- parametric polymorphism 
- recursion, tail recursion
- algebraic data types (Lists, Trees)
- currying, curried functions, compose
- pattern matching
- folding (foldRight, foldLeft)
- map, flatMap, filter, append, concat, zipWith
- hasSubsequence and Pascal triangle
- Chapters 1, 2, 3 from [Chiusano, Bjarnason 2014]

### 02 - Error Handling
#### Topics and Exercises
- throwing exceptions is not RT
- Option (Some, None)
- map, getOrElse, flatMap
- map2
- for-yield comprehension 
- Either
- Chapter 4 from [Chiusano, Bjarnason 2014]

### 03 - Laziness
#### Topics and Exercises
- Strictness vs Laziness
- Finite/Infinite Streams
- Chapter 5 from [Chiusano, Bjarnason 2014]

### 04 - State Monad
#### Topics and Exercises
- Purely functional RNG
- Making stateful APIs pure
- State Action data type
- State APU
- Chapter 6 from [Chiusano, Bjarnason 2014]

### 05 - Parallel Computations
#### Topics and Exercises
- Functional library design (Part 1)
- Side effect free Par library
- Executor Service, Future Task
- unit, lazyUnit, fork, asyncF, map2, parMap, parFilter, parForall, map3, chooser, flatMap, join
- Chapter 7 from [Chiusano, Bjarnason 2014]

### 06 - Property Testing
#### Topics and Exercises
- Functional library design (Part 2)
- Gen trait, State
- &&, ||, flatMap, union, map, choose, listOfN, map
- Chapter 8 from [Chiusano, Bjarnason 2014]

### 08 - Parser Combinators
#### Topics and Exercises
- Functional library design (Part 3)
- Algebraic Design
- Full Abstraction
- Type Constructor
- Higher Kind, Higher Kinded Polymorphism
- Structure-Preserving Map (only changes values produced, with identity there is no change at all)
- Internal DSL, fluid interface
- Chapter 9 from [Chiusano, Bjarnason 2014]

### 09 - Functional Design
#### Topics and Exercises
- Design Patterns in Functional Programming
  - Monoids 
    - closed
    - associativity
    - identity
    - endomonoid
    - homomorphism
    - isomorphism
    - product of a Monoid 
  - Foldables
    - foldMap
  - Functors
  - Monads
- Chapter 10 and 11 from [Chiusano, Bjarnason 2014]

### 10 - Monadic Evaluators
#### Topics and Exercises
- Interpreters
- Abstract Haskell
- Self-study based on reading [Claessen, Hughes 2000] (Research Paper)

### 11 - Assymetric Data Lenses
#### Topics and Exercises
- How to update deeply nested rich structures in a pure and persistent setting

### 13 - Finger Trees
#### Topics and Exercises
- Finger Trees 
- Persistent Immutable Data Structures 
- Polymorphic Recursion.

### 14 - Probabilistic Programming
#### Topics and Exercises
- Figaro
- No homework
- Basic Inference Techniques in AI

## Mini Projects

### 07 - Property Testing
#### Topics and Exercises
- Test Suite for Stream library (headOption, take(n), drop(n), map, append)
- Combination of property tests and scenario tests
- Gen, Arbitrary, forAll, &&
- Chapter 9 from [Chiusano, Bjarnason 2014]

### 12 - Sentiment Analysis
#### Topics and Exercises
- Apache Spark
- Automatic Classification of Natural Language Texts
- Perceptron Classifier
