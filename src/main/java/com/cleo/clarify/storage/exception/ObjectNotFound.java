package com.cleo.clarify.storage.exception;

public class ObjectNotFound extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public final String objectId;
	
	public ObjectNotFound(String objectId) {
		this.objectId = objectId;
	}

}
