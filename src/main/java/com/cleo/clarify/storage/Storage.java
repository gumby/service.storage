package com.cleo.clarify.storage;

import com.cleo.clarify.chassis.Service;
import com.cleo.clarify.storage.grpc.StorageGrpcModule;
import com.google.inject.Module;

public class Storage extends Service {
  
  public static void main(String[] args) {
    new Storage().run();
  }
  
  @Override
  public Module[] getModules() {
    return new Module[] {
       new StorageModule(),
       new StorageGrpcModule(),
    };
  }

}
