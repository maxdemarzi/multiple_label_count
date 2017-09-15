package com.maxdemarzi;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.server.rest.domain.JsonParseException;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class MultipleLabelCountTest {

    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withProcedure(Procedures.class);

    @Test
    public void shouldGetMultipleLabelCount() throws JsonParseException {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY);
        Number count = response.get("results").get(0).get("data").get(0).get("row").get(0).asLong();
        assertEquals(2L, count);
    }

    private static final HashMap<String, Object> QUERY = new HashMap<String, Object>(){{
        put("statements", new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("statement", "CALL com.maxdemarzi.multiple_label_count2(['Label1','Label2', 'Label3']) YIELD value RETURN value");
            }});
        }});
    }};
    private static final String MODEL_STATEMENT =
            "CREATE (c1:Label1:Label2:Label3 {id:'c1'})" +
                    "CREATE (c2:Label1:Label2:Label3 {id:'c2'})" +
                    "CREATE (c3:Label2 {id:'c3'})" +
                    "CREATE (c4:Label1:Label3 {id:'c4'})" +
                    "CREATE (c5:Label2:Label3 {id:'c5'})" +
                    "CREATE (c6:Label1:Label2 {id:'c6'})";
}
