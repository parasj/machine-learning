import java.io._
import java.util.zip._
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets
import scala.collection.immutable.IndexedSeq
import scala.collection.parallel.immutable.ParSeq

/**
 * code
 */
object RLSimRunner {
  def main(args: Array[String]): Unit = {
    val experiment = new RLSimExperimenter
    experiment.runQLearning()
  }
}

class RLSimExperimenter {
  val mazeVals = List("medium")
  val pVals = (0 to 100).filter(_ % 5 == 0).map(_ * 0.01)
  val episilonVals = (0 to 100).filter(_ % 5 == 0).map(_ * 0.01)
  val iterVals = List(10000)


  lazy val mazes = mazeVals
      .map {f => (f, com.proj4.Config.mapPath(f))}
      .map { case (fname, file) => (fname, new File(file)) }
      .map { case (fname, file) => (fname, readMaze(file)) }

  def readMaze(file: File): Maze = {
    val in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))
    val myMaze = in.readObject.asInstanceOf[Maze]
    in.close()
    myMaze
  }

  lazy val QLearningExperiments =
    mazes.flatMap { case (fname, maze) =>
      pVals.flatMap { p =>
        episilonVals.flatMap { e =>
          iterVals.map(i => (fname, maze, p, e, i)) } } }

  def runTrial(ql: QLearning, i: Int) = {
    val runtime = time { ql.execute(i) }._2
    (ql.evalPolicy, runtime)
  }

  def runTrialPS(ql: PrioritizedSweeping, i: Int) = {
    val runtime = time { ql.execute(i) }._2
    (ql.evalPolicy, runtime)
  }

  lazy val experiment =
    QLearningExperiments.par
      .map { case (fname, maze, p, e, i) => (fname, p, e, i, maze) }
      .map { case (fname, p, e, i, maze) =>
        val trials = (1 to 20).map(_ => new QLearning(maze, p, 0.7, e, true)).map(ql => runTrial(ql, i))
        val score = trials.map(_._1).sum / trials.length
        val runtime = trials.map(_._2).sum.toDouble / trials.length
        (fname, p, e, i, score, runtime)
      }
      .map { case (fname, p, e, i, score, runtime) => s"$fname\t$p\t$e\t$i\t$score\t$runtime" }

  def runQLearning(): Unit = {
    println(s"${QLearningExperiments.length} experiments to run")
    experiment.foreach(s => println(s))
    // Files.write(
    //  Paths.get(com.proj4.Config.outPath("qlearning", "bigmaze")),
    //  experiment.mkString("\n").getBytes(StandardCharsets.UTF_8))
  }

  lazy val psweepingExperiments =
    QLearningExperiments.par
      .map { case (fname, maze, p, e, i) => (fname, p, e, i, maze) }
      .map { case (fname, p, e, i, maze) =>
        println("#" + (fname, p, e, i))
        val trials = (1 to 20).map(_ => new PrioritizedSweeping(maze, p, e, 50, 0.01)).map(ql => runTrialPS(ql, i))
        println("##" + (fname, p, e, i))
        val score = trials.map(_._1).sum / trials.length
        val runtime = trials.map(_._2).sum.toDouble / trials.length
        (fname, p, e, i, score, runtime)
      }
      .map { case (fname, p, e, i, score, runtime) => s"$fname\t$p\t$e\t$i\t$score\t$runtime" }

  def runPSweeping(): Unit = {
    println(s"${QLearningExperiments.length} experiments to run")
    psweepingExperiments.foreach(s => println(s))
  }

  def time[R](block: => R): (R, Long) = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    (result, t1 - t0)
  }
}