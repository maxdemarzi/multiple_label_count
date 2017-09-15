package com.maxdemarzi;


import com.maxdemarzi.util.TestUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MultipleLabelCountBenchmark {
    private GraphDatabaseService db;

    @Param({"100000"})
    private int userCount;

    @Param({"10000"})
    private int actorCount;

    @Param({"2000000"})
    private int personCount;

    @Setup(Level.Trial)
    public void prepare() throws Exception {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        TestUtils.registerProcedure(db, Procedures.class);

        int count = 0;
        Transaction tx = db.beginTx();
        try {
            for (int person = 0; person < personCount; person++) {
                Node personNode = db.createNode(Label.label("Person"));
                personNode.setProperty("id", "person" + person);
                if (count < actorCount) {
                    personNode.addLabel(Label.label("Actor"));
                }
                if (count < userCount) {
                    personNode.addLabel(Label.label("User"));
                }
                if (count++ % 100_000 == 0) {
                    tx.success();
                    tx.close();
                    tx = db.beginTx();
                }
            }
            tx.success();
        } finally {
            tx.close();
        }
    }


    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public long measureMultipleLabelCount() throws IOException {

        try (Transaction tx = db.beginTx()) {

            Map<String, Object> result = db.execute("CALL com.maxdemarzi.multiple_label_count(['Person','User', 'Actor']) YIELD value RETURN value").next();

            return (Long)result.get("value");

        }
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public long measureMultipleLabelCount2() throws IOException {

        try (Transaction tx = db.beginTx()) {

            Map<String, Object> result = db.execute("CALL com.maxdemarzi.multiple_label_count2(['Person','User', 'Actor']) YIELD value RETURN value").next();

            return (Long)result.get("value");

        }
    }
}
