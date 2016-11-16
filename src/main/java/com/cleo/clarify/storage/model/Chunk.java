package com.cleo.clarify.storage.model;

import java.nio.ByteBuffer;

public class Chunk {
	
	private final int chunkIndex;
	private final ByteBuffer data;
	
	private Chunk(int chunkIndex, ByteBuffer data) {
		this.chunkIndex = chunkIndex;
		this.data = data;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public ByteBuffer data() {
		return data.asReadOnlyBuffer();
	}
	
	public int index() {
		return chunkIndex;
	}
	
	@Override
	public String toString() {
	  return "Chunk(index=" + chunkIndex + ",dataLength=" + data.array().length + ")";
	}
	
	public static final class Builder {
		
		private int index;
		private ByteBuffer data;
		
		public Builder withIndex(int index) {
			this.index = index;
			return this;
		}
		
		public Builder withData(ByteBuffer data) {
			this.data = data;
			return this;
		}
		
		public Chunk build() {
			return new Chunk(index, data);
		}
	}

}
