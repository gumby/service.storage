package com.cleo.clarify.storage.metadata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cleo.clarify.storage.model.Chunk;
import com.cleo.clarify.storage.model.ObjectMetadata;
import com.cleo.clarify.storage.repository.CassandraStorageRepository;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.spotify.cassandra.extra.CassandraRule;

import rx.Observable;
import rx.observers.TestSubscriber;

public class CassandraStorageRepositoryTest {
  
  private static String id = "9e8a9e5e-273f-44b7-afa5-e84d209af63e";
  private static long objectSize = 10;
  private static long chunkSize = 2;
  private static int chunkCount = 5;
  private static byte[] data = "Hello, World!".getBytes();

  @Rule
  public CassandraRule cassandraRule = CassandraRule.newBuilder()
  .withManagedKeyspace()
  .withManagedTable(getClass().getClassLoader().getResource("create_table.cql"))
  .build();
  
  @Before
  public void insertData() {
    Session session = cassandraRule.getSession();
    session.execute(insertQuery);
  }

  Insert insertQuery = QueryBuilder
      .insertInto("objects")
      .value("id", id + "$" + 0)
      .value("object_size", objectSize)
      .value("chunk_size", chunkSize)
      .value("chunk_count", chunkCount)
      .value("data", ByteBuffer.wrap(data));
  
  @Test
  public void select_metdata() throws InterruptedException {
    TestSubscriber<ObjectMetadata> testSubscriber = TestSubscriber.create();
    Observable<ObjectMetadata> observable = new CassandraStorageRepository(cassandraRule.getSession())
      .readMetadata(id);
    observable.subscribe(testSubscriber);
    
    testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
    ObjectMetadata metadata = testSubscriber.getOnNextEvents().get(0);
    assertThat(metadata.id, equalTo(id));
    assertThat(metadata.chunkCount, equalTo(chunkCount));
    assertThat(metadata.chunkSize, equalTo(chunkSize));
    assertThat(metadata.objectSize, equalTo(objectSize));
  }
  
  @Test
  public void select_chunk() {
    TestSubscriber<Chunk> s = TestSubscriber.create();
    new CassandraStorageRepository(cassandraRule.getSession())
      .readChunk(id, 0)
      .subscribe(s);
    s.awaitTerminalEvent();
    Chunk chunk = s.getOnNextEvents().get(0);
    byte[] actualBytes = new byte[chunk.data().capacity()];
    chunk.data().get(actualBytes);
    assertThat(actualBytes, equalTo(data));
  }

}
