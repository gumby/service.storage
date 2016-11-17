package com.cleo.clarify.storage.service;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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

  
  private static final int numChunks = 150_000;
  private static final int minBytes = 16_384;
  private static final int maxBytes = 65_572;

  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build();
    CountDownLatch shutdownLatch = new CountDownLatch(1);
    StreamObserver<ObjectData> data = StorageServiceGrpc.newStub(channel).store(new ObjectInfoObserver(shutdownLatch));

    IntStream chunkSizes = new Random().ints(numChunks, minBytes, maxBytes);
    IntStream indexes = IntStream.range(0, numChunks);
    
    long start = System.nanoTime();
    Observable.from(chunkSizes::iterator)
    .zipWith(Observable.from(indexes::iterator), (chunkSize, index) -> IndexAndSize.from(index, chunkSize))
    .subscribe(
        indexAndSize -> 
          data.onNext(ObjectData.newBuilder()
            .setIndex(indexAndSize.index)
            .setData(ByteString.copyFrom(randomBytes(indexAndSize.size)))
            .build()), 
        error -> error.printStackTrace(), 
        () -> data.onCompleted());
    
    
    shutdownLatch.await();
    long end = System.nanoTime();
    System.out.println("Total time (seconds): "+ TimeUnit.NANOSECONDS.toSeconds(end - start));
  }

  private static byte[] randomBytes(int size) {
    Random rand = new Random();
    byte[] bytes = new byte[size];
    rand.nextBytes(bytes);
    return bytes;
  }

  private static final class IndexAndSize {
    public final int index;
    public final int size;
    
    private IndexAndSize(int index, int size) {
      this.index = index;
      this.size = size;
    }
    
    public static IndexAndSize from(int index, int size) {
      return new IndexAndSize(index, size);
    }
  }
  
  private static final class ObjectInfoObserver implements StreamObserver<ObjectInfo> {

    private CountDownLatch shutdownLatch;
    
    public ObjectInfoObserver(CountDownLatch shutdown) {
      this.shutdownLatch = shutdown;
    }

    @Override
    public void onCompleted() {
      shutdownLatch.countDown();
    }

    @Override
    public void onError(Throwable t) {
      t.printStackTrace();
      shutdownLatch.countDown();
    }

    @Override
    public void onNext(ObjectInfo info) {
      System.out.println(info);
      System.out.println("Size: " + FileUtils.byteCountToDisplaySize(info.getObjectSize()));
    }
  }

}
