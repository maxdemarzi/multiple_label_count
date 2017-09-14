# multiple_label_count
Count nodes that have multiple labels

This project requires Neo4j 3.2.x

This project uses maven, to build a jar-file with the procedures in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/multiple-labels-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/procedures-1.0-SNAPSHOT.jar neo4j-enterprise-3.2.3/plugins/.


Edit your Neo4j/conf/neo4j.conf file by adding this line:

    dbms.security.procedures.unrestricted=com.maxdemarzi.*
    

Procedure
------    

    CALL com.maxdemarzi.multiple_label_count(['Label1','Label2', 'Label3']) YIELD value RETURN value