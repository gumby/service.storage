package com.cleo.clarify.storage.model;

public class ObjectMetadata {
	
	public final String id;
	public final long objectSize;
	public final int chunkCount;
	public final long chunkSize;
	
	private ObjectMetadata(String id, long objectSize, long chunkSize, int chunkCount) {
		this.id = id;
		this.objectSize = objectSize;
		this.chunkSize = chunkSize;
		this.chunkCount = chunkCount;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private String id;
		private long objectSize;
		private long chunkSize;
		private int chunkCount;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder objectSize(long objectSize) {
			this.objectSize = objectSize;
			return this;
		}

		public Builder chunkSize(long chunkSize) {
			this.chunkSize = chunkSize;
			return this;
		}

		public Builder chunkCount(int chunkCount) {
			this.chunkCount = chunkCount;
			return this;
		}

		public ObjectMetadata build() {
			return new ObjectMetadata(id, objectSize, chunkSize, chunkCount);
		}
		
	}

}
