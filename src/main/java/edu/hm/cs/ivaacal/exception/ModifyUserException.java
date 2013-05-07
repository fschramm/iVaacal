package edu.hm.cs.ivaacal.exception;


/**
 * iVaaCal Modify User Exception.
 */
public class ModifyUserException extends Exception{

	/**
	 * Modify User Exception with custom message.
	 * @param message The custom message.
	 */
	public ModifyUserException(String message) {
		super(message);
	}

	/**
	 * Modify User Exception for throwables.
	 * @param throwable The throwable.
	 */
	public ModifyUserException(Throwable throwable) {
		super(throwable);
	}

}

