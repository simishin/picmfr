package qwr.util;

/**
 * Исключение для передачи результата хода выполнения вызывающему методу
 */
public class QException extends Exception {
	private int x;
	public int value() {
		return x;
	}
	public QException(int x) {
		super("--- Logic exception result: "+x+" --- ");
		this.x = x;
	}
}//QException
