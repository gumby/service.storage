package com.cleo.clarify.storage.repository;

import com.cleo.clarify.storage.model.Chunk;
import com.cleo.clarify.storage.model.ObjectMetadata;

import rx.Observable;

public interface StorageRepository {
  
  public Observable<Chunk> readChunkRx(String objectId, int chunkIndex);

  public Observable<ObjectMetadata> readMetadataRx(String objectId);
  
}
