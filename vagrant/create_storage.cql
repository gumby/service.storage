CREATE KEYSPACE IF NOT EXISTS storage WITH replication = {'class':'SimpleStrategy', 'replication_factor':1} AND DURABLE_WRITES = true;

USE storage;

CREATE TABLE IF NOT EXISTS objects(id text PRIMARY KEY, data blob, object_size bigint, chunk_size bigint, chunk_count int);
