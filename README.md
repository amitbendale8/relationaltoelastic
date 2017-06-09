# relationaltoelastic

1) This is utility for importing Relation (Oracle/Sql) database to elasticsearch.

2) Modify "/importdata/src/main/resources/config.properties" file, give table name, database name, and elasticsearch URL.

3)Go to base directory. (i.e importdata)

4) Run "mvn clean compile assembly:single"

5)Run "java -jar target\importdata-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

6) Check the elastic search index "http://localhost:9200/_cat/indices?v"
