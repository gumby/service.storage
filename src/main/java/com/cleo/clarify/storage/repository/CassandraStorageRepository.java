package com.cleo.clarify.storage.repository;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;

import com.cleo.clarify.storage.model.Chunk;
import com.cleo.clarify.storage.model.ObjectMetadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class CassandraStorageRepository implements StorageRepository {

  private final PreparedStatement metadataQuery;
  private final PreparedStatement chunkQuery;
  private final PreparedStatement insertQuery;
  private String defaultKey="%s$%d";
  private Session session;

  @Inject
  public CassandraStorageRepository(Session session) {
    this.session = session;
    this.metadataQuery = session.prepare(QueryBuilder
        .select("object_size", "chunk_size", "chunk_count")
        .from("objects")
        .where(QueryBuilder.eq("id", QueryBuilder.bindMarker("id")))
        .limit(1));
    this.chunkQuery = session.prepare(QueryBuilder
        .select("id", "data")
        .from("objects")
        .where(QueryBuilder.eq("id", QueryBuilder.bindMarker("id"))));
    this.insertQuery = session.prepare(
      QueryBuilder
        .insertInto("objects")
        .value("id", bindMarker("id"))
        .value("object_size", bindMarker("objectSize"))
        .value("chunk_size", bindMarker("chunkSize"))
        .value("chunk_count", bindMarker("chunkCount"))
        .value("data", bindMarker("data")));  
  }

  @Override
  public Observable<ObjectMetadata> readMetadata(String objectId) {
    return Observable.from(session.executeAsync(metadataQuery.bind(getRowKey(objectId))), Schedulers.io())
        .map((rs) -> rs.one())
        .map((metadataRow) -> new ObjectMetadata.Builder()
            .id(objectId)
            .objectSize(metadataRow.getLong("object_size"))
            .chunkSize(metadataRow.getLong("chunk_size"))
            .chunkCount(metadataRow.getInt("chunk_count"))
            .build());
  }
  
  @Override
  public void writeMetadata(String objectId, int chunkSize, long objectSize) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Observable<Chunk> readChunk(String objectId, int chunkIndex) {
    return Observable.from(session.executeAsync(chunkQuery.bind(getRowKey(objectId, chunkIndex))), Schedulers.io())
        .map((rs) -> rs.one())
        .map((chunkRow) -> chunkRow.getBytes("data"))
        .map(data -> Chunk.builder().withIndex(chunkIndex).withData(data).build());
  }
  
  @Override
  public void writeChunk(String id, int chunkIndex, byte[] data) {
    String rowKey = getRowKey(id, chunkIndex);
//    session.executeAsync(insertQuery.bind(rowKey, -1, data.length, -1, data));
    System.out.println("Wrote chunk: " + data.length);
  }
  
  private String getRowKey(String objectId, int chunkIndex) {
    return defaultKey.replace("%s", objectId).replace("%d", Integer.toString(chunkIndex));
  }

  private String getRowKey(String objectId) {
    return getRowKey(objectId, 0);
  }

}
