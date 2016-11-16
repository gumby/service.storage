package com.cleo.clarify.storage.service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.cleo.clarify.storage.grpc.ObjectData;
import com.cleo.clarify.storage.grpc.ObjectInfo;
import com.cleo.clarify.storage.grpc.StorageServiceGrpc;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rx.Observable;

public class ClientServiceTest {  

  private static final int numChunks = 100_900;
  private static final int maxBytes = 65_572;
  
  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build();
    AtomicBoolean shutdown = new AtomicBoolean();
    StreamObserver<ObjectData> data = StorageServiceGrpc.newStub(channel).store(new StreamObserver<ObjectInfo>() {

      @Override
      public void onCompleted() {
        System.out.println("Response completed.");
        shutdown.set(true);
      }

      @Override
      public void onError(Throwable t) {
        t.printStackTrace();
      }

      @Override
      public void onNext(ObjectInfo info) {
        System.out.println(info);
        System.out.println("To gb: " + FileUtils.byteCountToDisplaySize(info.getObjectSize()));
      }
    });
    
    IntStream stream = new Random().ints(numChunks, 16384, maxBytes);
    Observable.from(stream::iterator)
    .forEach(
        size -> {
          data.onNext(ObjectData.newBuilder().setData(ByteString.copyFrom(getRandBytes(size))).build());
        },
        error -> { 
          data.onError(error);
          error.printStackTrace();
        },
        () -> {
          data.onCompleted();
        });
    while (!shutdown.get());
  }
  
  private static byte[] getRandBytes(int size) {
    Random rand = new Random();
    byte[] bytes = new byte[size];
    rand.nextBytes(bytes);
    return bytes;
  }
  
}
