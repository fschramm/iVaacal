package edu.hm.cs.ivaacal.exception;

/**
 * iVaaCal DataSource Exception.
 */
public class DataSourceException extends Exception{

	/**
	 * DataSourceException with custom message.
	 * @param message The custom message.
	 */
	public DataSourceException(String message) {
		super(message);
	}

	/**
	 * DataSourceException with throwable.
	 * @param throwable The custom message.
	 */
	public DataSourceException(Throwable throwable) {
		super(throwable);
	}
}
