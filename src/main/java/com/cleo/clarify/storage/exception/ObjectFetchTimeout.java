package com.cleo.clarify.storage.exception;

public class ObjectFetchTimeout extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ObjectFetchTimeout(String objectName) {
		super("Timeout waiting for object to be fetched: " + objectName);
	}

}
