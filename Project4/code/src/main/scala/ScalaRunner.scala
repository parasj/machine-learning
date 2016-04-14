object ScalaRunner {
	def main(args: Array[String]): Unit = {
//		val run = new MazeRunner
//		run.run()

		val experiment = new RLSimExperimenter
		experiment.runPSweeping()
	}
}