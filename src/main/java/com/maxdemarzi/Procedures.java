package com.maxdemarzi;

import org.neo4j.graphdb.Label;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;
import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import java.util.List;
import java.util.stream.Stream;

public class Procedures {

    @Context
    public GraphDatabaseAPI db;

    @Context
    public Log log;

    @Procedure(name = "com.maxdemarzi.multiple_label_count", mode = Mode.DEFAULT)
    @Description("CALL com.maxdemarzi.multiple_label_count([labels]")
    public Stream<LongResult> MultipleLabelCount(@Name("labels") List<String> labels) {
        RoaringBitmap[] bitmaps = new RoaringBitmap[labels.size()];

        int count = 0;
        for (String label : labels) {
            int finalCount = count;
            RoaringBitmap bitmap = new RoaringBitmap();
            bitmaps[count] = bitmap;
            db.findNodes(Label.label(label)).stream().forEach(node -> bitmaps[finalCount].add(((int) node.getId())));
            count++;
        }

        for (int i = 1; i < bitmaps.length; i++) {
            bitmaps[0].and(bitmaps[i]);
        }

        return Stream.of(new LongResult(bitmaps[0].getLongCardinality()));

    }

    @Procedure(name = "com.maxdemarzi.multiple_label_count2", mode = Mode.DEFAULT)
    @Description("CALL com.maxdemarzi.multiple_label_count2([labels]")
    public Stream<LongResult> MultipleLabelCount2(@Name("labels") List<String> labels) {
        RoaringBitmap[] bitmaps = new RoaringBitmap[labels.size()];

        int count = 0;
        for (String label : labels) {
            int finalCount = count;
            RoaringBitmap bitmap = new RoaringBitmap();
            bitmaps[count] = bitmap;
            db.findNodes(Label.label(label)).stream().forEach(node -> bitmaps[finalCount].add(((int) node.getId())));
            count++;
        }

        return Stream.of(new LongResult(FastAggregation.and(bitmaps).getLongCardinality()));

    }

}
