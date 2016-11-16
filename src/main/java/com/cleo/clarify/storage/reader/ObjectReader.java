package com.cleo.clarify.storage.reader;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cleo.clarify.storage.model.Chunk;
import com.cleo.clarify.storage.repository.StorageRepository;

import rx.Observable;

public class ObjectReader {

  private final StorageRepository storageRepo;
  private int concurrencyLevel = 4;

  public ObjectReader(StorageRepository repo) {
    this.storageRepo = repo;
  }

  public ObjectReader withConcurrencyLevel(int level) {
    this.concurrencyLevel = level;
    return this;
  }

  public Observable<Chunk> readRx(String objectId) {    
    return Observable.create(s -> {
      storageRepo.readMetadata(objectId)
      .flatMap(metadata -> Observable.from(IntStream.range(0, metadata.chunkCount).boxed().collect(toShuffledList())))
      .flatMap(index -> storageRepo.readChunk(objectId, index), concurrencyLevel)
      .forEach(
          chunk -> s.onNext(chunk),
          error -> s.onError(error),
          () -> s.onCompleted());
    });
  }

  private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
      Collectors.toList(),
      list -> {
        Collections.shuffle(list);
        return list;
      });

  @SuppressWarnings("unchecked")
  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return (Collector<T, ?, List<T>>) SHUFFLER;
  }	

}
