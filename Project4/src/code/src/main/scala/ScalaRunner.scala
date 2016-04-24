object ScalaRunner {
	def main(args: Array[String]): Unit = {
		val experiment = new RLSimExperimenter
		experiment.runQLearning()
		experiment.runPSweeping()
	}
}