package com.cleo.clarify.storage.metadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cleo.clarify.storage.model.Chunk;
import com.cleo.clarify.storage.reader.ObjectReader;
import com.cleo.clarify.storage.repository.CassandraStorageRepository;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.spotify.cassandra.extra.CassandraRule;

import rx.observers.TestSubscriber;

public class ObjectReaderTest {

  private static final int chunkSize = 1024 * 1024;
  private static final int numChunks = 250;
  private String id;

  @Rule
  public CassandraRule cassandraRule = CassandraRule.newBuilder()
    .withManagedKeyspace()
    .withManagedTable(getClass().getClassLoader().getResource("create_table.cql"))
    .build();

  @Before
  public void setupTable() throws IOException {
    this.id = writeData(numChunks);
  }
  
  @Test
  public void read_object() {
    TestSubscriber<Chunk> testSub = TestSubscriber.create();
    new ObjectReader(
        new CassandraStorageRepository(cassandraRule.getSession()))
    .withConcurrencyLevel(8).readRx(id).subscribe(testSub);
    testSub.awaitTerminalEvent();
    testSub.assertValueCount(numChunks);
  }

  private String writeData(int numRecords) {
    String id = UUID.randomUUID().toString();
    for (int chunkIndex = 0; chunkIndex < numRecords; chunkIndex++) {
      byte[] bytes = new byte[chunkSize];
      new Random().nextBytes(bytes);
      Insert insertQuery = QueryBuilder
        .insertInto("objects")
        .value("id", id + "$" + chunkIndex)
        .value("object_size", chunkSize * numRecords)
        .value("chunk_size", chunkSize)
        .value("chunk_count", numRecords)
        .value("data", ByteBuffer.wrap(bytes));
      cassandraRule.getSession().execute(insertQuery);
    }
    return id;
  }
}
