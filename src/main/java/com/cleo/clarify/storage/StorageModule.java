package com.cleo.clarify.storage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import com.cleo.clarify.storage.repository.CassandraStorageRepository;
import com.cleo.clarify.storage.repository.StorageRepository;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.typesafe.config.Config;

public class StorageModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(StorageRepository.class).to(CassandraStorageRepository.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  @Inject
  public Session sessionProvider(Config config) {
    List<InetAddress> contacts = config.getStringList("cassandra.contacts").stream()
      .map(contact -> HostAndPort.fromString(contact))
      .map(hostAndPort -> new InetSocketAddress(hostAndPort.getHostText(), hostAndPort.getPort()).getAddress())
      .collect(Collectors.toList());
    Cluster cluster = Cluster.builder().addContactPoints(contacts).build();
    
    return cluster.connect(config.getString("cassandra.keyspace"));
  }
}
