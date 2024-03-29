package adrianhitz.adventofcode

import scala.annotation.tailrec

object Day10 extends AdventIO {
  override def main(args: Array[String]): Unit = {
    write1(part1.toString)
    println(s"The best position for the monitoring station is at $stationPosition\n")
    write2(part2.toString)
  }

  def part1(implicit s: String): Int = {
    val asteroidPositions: Set[(Int, Int)] = parseInput(s)
    val station = asteroidPositions
        .map(asteroid => (asteroid, (asteroidPositions - asteroid).map(x => reduce(subtract(x, asteroid))).size))
        .maxBy(_._2)
    stationPosition = station._1
    station._2
  }

  def part2(implicit s: String): Int = {
    case class Asteroid(position: (Int, Int), relativePosition: (Int, Int), distance: Double, reduced: (Int, Int), angle: Double)

    val asteroidPositions: Set[(Int, Int)] = parseInput(s) - stationPosition
    var asteroids = asteroidPositions.map(position => {
      val relative = subtract(position, stationPosition)
      val distance = Math.pow(relative._1, 2) + Math.pow(relative._2, 2)
      Asteroid(position, relative, distance, reduce(relative), angle(relative))
    })

    var shot = Vector[Asteroid]()
    while(asteroids.nonEmpty) {
      var shooting = Map[(Int, Int), Asteroid]()
      for(asteroid <- asteroids) {
        if(!shooting.contains(asteroid.reduced)) {
          shooting += asteroid.reduced -> asteroid
        } else if(asteroid.distance < shooting(asteroid.reduced).distance) {
          shooting += asteroid.reduced -> asteroid
        }
      }
      shot ++= shooting.values.toVector.sortBy(a => a.angle)
      asteroids --= shooting.values
    }

    val (x, y) = shot(199).position
    x * 100 + y
  }

  var stationPosition: (Int, Int) = (0, 0)

  private def parseInput(s: String): Set[(Int, Int)] =
    s.split('\n').zipWithIndex.flatMap(
      row => row._1.zipWithIndex.filter(elem => elem._1 == '#').map(elem => (elem._2, row._2))
    ).toSet

  @tailrec
  private def gcd(a: Int, b: Int): Int = {
    if(b == 0) return a
    gcd(b, a % b)
  }

  private def reduce(position: (Int, Int)): (Int, Int) = position match {
    case (0, 0) => position
    case (a, 0) => (a / Math.abs(a), 0)
    case (0, b) => (0, b / Math.abs(b))
    case (a, b) =>
      val g = Math.abs(gcd(a, b))
      (a / g, b / g)
  }

  private def subtract(fraction1: (Int, Int), fraction2: (Int, Int)): (Int, Int) =
    (fraction1._1 - fraction2._1, fraction1._2 - fraction2._2)

  private def angle(position: (Int, Int)): Double = {
    var angle = position match {
      case (0, y) if y > 0 => Math.PI / (-2)
      case (0, _) => Math.PI / 2
      case (x, y) => Math.atan(y.toDouble / x.toDouble)
    }
    if(position._1 < 0) angle += Math.PI
    angle
  }
}
