package com.cleo.clarify.storage.grpc;

import com.cleo.clarify.storage.grpc.StorageServiceGrpc.StorageServiceImplBase;
import com.cleo.clarify.storage.writer.ObjectWriter;
import com.google.inject.Inject;

import io.grpc.ServerServiceDefinition;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;

public class StorageService extends StorageServiceImplBase {

  private final HealthStatusManager healthManager;
  private final ObjectWriter writer;
  
  @Inject
  public StorageService(ObjectWriter writer, HealthStatusManager healthManager) {
    this.writer = writer;
    this.healthManager = healthManager;
  }
  
  @Override
  public ServerServiceDefinition bindService() {
    ServerServiceDefinition definition = super.bindService();
    healthManager.setStatus(StorageServiceGrpc.SERVICE_NAME, ServingStatus.SERVING);
    return definition;
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
