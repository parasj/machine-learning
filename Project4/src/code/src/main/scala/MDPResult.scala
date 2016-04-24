import rl.Policy

trait MDPResult {
  def policy: Policy
  def iterations: Int
  def runtime: Long
  def visualization: String
  def algorithm: String
  def value: Double
  override def toString = {
    "%s: %d iterations and %f ms with value %f\n%s\n".format(algorithm, iterations, runtime / 1000000.0, value, visualization)
  }
}

case class ValueIterationResult(
  override val policy: Policy,
  override val iterations: Int,
  override val runtime: Long,
  override val visualization: String,
  override val value: Double
) extends MDPResult {
  val algorithm = "ValueIteration"
}

case class PolicyIterationResult(
  override val policy: Policy,
  override val iterations: Int,
  override val runtime: Long,
  override val visualization: String,
  override val value: Double
) extends MDPResult {
  val algorithm = "PolicyIteration"
}