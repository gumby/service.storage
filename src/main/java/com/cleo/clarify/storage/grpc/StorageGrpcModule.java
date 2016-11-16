package com.cleo.clarify.storage.grpc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import io.grpc.BindableService;

public class StorageGrpcModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(BindableService.class).to(StorageGrpcService.class).in(Scopes.SINGLETON);
  }

}
