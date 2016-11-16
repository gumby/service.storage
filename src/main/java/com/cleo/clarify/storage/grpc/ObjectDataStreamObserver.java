package com.cleo.clarify.storage.grpc;

import java.math.BigDecimal;
import java.util.UUID;

import com.cleo.clarify.storage.grpc.ObjectData;
import com.cleo.clarify.storage.grpc.ObjectInfo;
import com.cleo.clarify.storage.writer.ObjectWriter;

import io.grpc.stub.StreamObserver;

public class ObjectDataStreamObserver implements StreamObserver<ObjectData> {

  private final ObjectWriter writer;
  private final String id;
  private final StreamObserver<ObjectInfo> observer;
  private int chunkCount = 0;
  private BigDecimal objectSize = new BigDecimal(0);
  
  public ObjectDataStreamObserver(ObjectWriter writer, StreamObserver<ObjectInfo> responseObserver) {
    this.writer = writer;
    this.id = UUID.randomUUID().toString();
    this.observer = responseObserver;
  }
  
  @Override
  public void onCompleted() {
    System.out.println("Completed");
    observer.onNext(ObjectInfo.newBuilder().setId(id).setChunkCount(chunkCount).setObjectSize(objectSize.longValue()).build());
    observer.onCompleted();
  }

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onNext(ObjectData objectData) {
    chunkCount += 1;
    objectSize = objectSize.add(new BigDecimal(objectData.getData().size()));
    writer.store(id, objectData.getData().toByteArray());
  }

}
