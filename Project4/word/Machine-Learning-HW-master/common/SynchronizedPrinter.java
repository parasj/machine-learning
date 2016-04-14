import java.io.PrintStream;


public class SynchronizedPrinter {

	private PrintStream printer;

	public SynchronizedPrinter(PrintStream printer) {
		this.printer = printer;
	}
	
	public synchronized void println(String line) {
		printer.println(line);
	}
}
