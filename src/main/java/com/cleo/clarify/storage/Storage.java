package com.cleo.clarify.storage;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.cleo.clarify.chassis.Service;
import com.cleo.clarify.storage.grpc.StorageGrpcModule;
import com.google.inject.Module;

public class Storage extends Service {
  
  public static void main(String[] args) {
    setHostIfAbsent();
    new Storage().run();
  }
  
  private static void setHostIfAbsent() {
    if (System.getProperty("registry.api.host") != null) return;
    try {
      System.setProperty("registry.api.host", Inet4Address.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      System.err.println("Error retrieving host address; using localhost.");
      System.setProperty("registry.api.host", "localhost");
    }
  }
  
  @Override
  public Module[] getModules() {
    return new Module[] {
       new StorageModule(),
       new StorageGrpcModule(),
    };
  }

}
