package com.cleo.clarify.storage.grpc;

import java.math.BigDecimal;
import java.util.UUID;

import com.cleo.clarify.storage.repository.StorageRepository;

import io.grpc.stub.StreamObserver;

public class StorageObjectWriter implements StreamObserver<ObjectData> {

  private final StorageRepository storageRepo;
  private final String id;
  private final StreamObserver<ObjectInfo> clientObserver;
  private int chunkCount = 0;
  private BigDecimal objectSize = new BigDecimal(0);
  
  public StorageObjectWriter(StorageRepository storageRepo, StreamObserver<ObjectInfo> responseObserver) {
    this.id = UUID.randomUUID().toString();
    this.clientObserver = responseObserver;
    this.storageRepo = storageRepo;
  }
  
  @Override
  public void onCompleted() {
    storageRepo.writeMetadata(id, chunkCount, objectSize.longValue());
    clientObserver.onNext(ObjectInfo.newBuilder().setId(id).setChunkCount(chunkCount).setObjectSize(objectSize.longValue()).build());
    clientObserver.onCompleted();
  }

  @Override
  public void onError(Throwable t) {
    clientObserver.onError(t);
  }

  @Override
  public void onNext(ObjectData objectData) {
    objectSize = objectSize.add(new BigDecimal(objectData.getData().size()));
    storageRepo.writeChunk(id, objectData.getIndex(), objectData.getData().toByteArray());
    chunkCount += 1;
  }

}
