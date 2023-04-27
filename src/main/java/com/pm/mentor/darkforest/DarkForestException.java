package com.pm.mentor.darkforest;

public class DarkForestException extends RuntimeException {

	private static final long serialVersionUID = 3398433649024475735L;

	public DarkForestException(String msg) {
		super(msg);
	}
	
	public DarkForestException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public DarkForestException(Throwable cause) {
		super(cause);
	}
}
