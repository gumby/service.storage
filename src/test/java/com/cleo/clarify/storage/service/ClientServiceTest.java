package com.cleo.clarify.storage.service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.cleo.clarify.storage.grpc.ObjectData;
import com.cleo.clarify.storage.grpc.ObjectInfo;
import com.cleo.clarify.storage.grpc.StorageServiceGrpc;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rx.Observable;

public class ClientServiceTest {  

  private static final int numChunks = 105_000;
  private static final int minBytes = 16_384;
  private static final int maxBytes = 65_572;

  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build();
    AtomicBoolean shutdown = new AtomicBoolean();
    StreamObserver<ObjectData> data = StorageServiceGrpc.newStub(channel).store(new ObjectInfoResponseObserver(shutdown));

    IntStream chunkSizes = new Random().ints(numChunks, minBytes, maxBytes);
    IntStream indexes = IntStream.range(0, numChunks);
    
    Observable.from(chunkSizes::iterator)
    .zipWith(Observable.from(indexes::iterator), (chunkSize, index) -> Pair.of(index, chunkSize))
    .forEach(
        indexAndSize -> data.onNext(
            ObjectData.newBuilder()
              .setData(ByteString.copyFrom(randomBytes(indexAndSize.getRight())))
              .setIndex(indexAndSize.getLeft())
              .build()),
        error -> data.onError(error),
        () -> data.onCompleted());

    while (!shutdown.get());
  }

  private static byte[] randomBytes(int size) {
    Random rand = new Random();
    byte[] bytes = new byte[size];
    rand.nextBytes(bytes);
    return bytes;
  }

  private static final class ObjectInfoResponseObserver implements StreamObserver<ObjectInfo> {

    private AtomicBoolean shutdown;
    
    public ObjectInfoResponseObserver(AtomicBoolean shutdown) {
      this.shutdown = shutdown;
    }

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
      System.out.println("Size: " + FileUtils.byteCountToDisplaySize(info.getObjectSize()));
    }
  }

}
