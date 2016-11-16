package com.cleo.clarify.storage.writer;

import com.cleo.clarify.storage.repository.StorageRepository;
import com.google.inject.Inject;

import rx.Observable;

public class ObjectWriter {
  
  private final StorageRepository storageRepo;
  
  @Inject
  public ObjectWriter(StorageRepository storageRepo) {
    this.storageRepo = storageRepo;
  }
  
  public void write(Observable<Byte> chunks) {
    
  }

  public void store(String id, byte[] data) {
     System.out.println(String.format("Storing(length=%d)", data.length));
  }

}
