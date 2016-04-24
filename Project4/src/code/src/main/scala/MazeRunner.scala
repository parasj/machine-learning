import com.proj4._
import rl._
import shared.{FixedIterationTrainer, Trainer, ThresholdTrainer}

/**
 * code
 */
class MazeRunner {
//  val mazes: List[String] = List("small")
//
//  def time[R](block: => R): (R, Long) = {
//    val t0 = System.nanoTime()
//    val result = block    // call-by-name
//    val t1 = System.nanoTime()
//    (result, t1 - t0)
//  }
//
//  def valueIterationExperiment(mdp: MazeMarkovDecisionProcess, mazeVis: MazeMarkovDecisionProcessVisualization, iterations: Int): MDPResult = {
//    val vi = new ValueIteration(.95, mdp)
//    // val tt = new ThresholdTrainer(vi)
//    val (v, runtime) = time {
//      // tt.train
//
//    }
//    val p = vi.getPolicy
//    ValueIterationResult(p, iterations, runtime, mazeVis.toString(p), v)
//  }
//
//  def policyIterationExperiment(mdp: MazeMarkovDecisionProcess, mazeVis: MazeMarkovDecisionProcessVisualization, iterations: Int): MDPResult = {
//    val pi = new PolicyIteration(.95, mdp)
//    val trainer = new ThresholdTrainer(pi)
//    val (v, runtime) = time {
//      trainer.train
//    }
//    val p = pi.getPolicy
//    PolicyIterationResult(p, iterations, runtime, mazeVis.toString(p), v)
//  }
//
//  def run() = {
//    mazes
//      .map { t => (Config.mapPath(t), t) }
//      .map { case (f, t) => (MazeMarkovDecisionProcess.load(f), t) }
//      .map { case (mdp, t) => (mdp, t, new MazeMarkovDecisionProcessVisualization(mdp))}
//      .map { case (mdp, f, mazeVis) => (valueIterationExperiment(mdp, mazeVis, 10), policyIterationExperiment(mdp, mazeVis, 10), f) }
//      .foreach(println)
//  }

}
