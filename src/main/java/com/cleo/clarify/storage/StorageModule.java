package com.cleo.clarify.storage;

import org.mockito.Mockito;

import com.cleo.clarify.storage.repository.CassandraStorageRepository;
import com.cleo.clarify.storage.repository.StorageRepository;
import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

public class StorageModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(StorageRepository.class).to(CassandraStorageRepository.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  public Session sessionProvider() {
    return Mockito.mock(Session.class);
  }
}
