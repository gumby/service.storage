package com.cleo.clarify.storage.grpc;

import com.cleo.clarify.storage.grpc.DeleteResponse;
import com.cleo.clarify.storage.grpc.ObjectData;
import com.cleo.clarify.storage.grpc.ObjectId;
import com.cleo.clarify.storage.grpc.ObjectInfo;
import com.cleo.clarify.storage.grpc.StorageServiceGrpc.StorageServiceImplBase;
import com.cleo.clarify.storage.writer.ObjectWriter;
import com.google.inject.Inject;

import io.grpc.stub.StreamObserver;

public class StorageGrpcService extends StorageServiceImplBase {
  
  private final ObjectWriter writer;
  
  @Inject
  public StorageGrpcService(ObjectWriter writer) {
    this.writer = writer;
  }

  @Override
  public void delete(ObjectId request, StreamObserver<DeleteResponse> responseObserver) {
    // TODO Auto-generated method stub
    super.delete(request, responseObserver);
  }

  @Override
  public void retrieve(ObjectId request, StreamObserver<ObjectData> responseObserver) {
    // TODO Auto-generated method stub
    super.retrieve(request, responseObserver);
  }

  @Override
  public void retrieveInfo(ObjectId request, StreamObserver<ObjectInfo> responseObserver) {
    // TODO Auto-generated method stub
    super.retrieveInfo(request, responseObserver);
  }

  @Override
  public StreamObserver<ObjectData> store(StreamObserver<ObjectInfo> responseObserver) {
    System.out.println("Begin");
    return new ObjectDataStreamObserver(writer, responseObserver);
  }

}
