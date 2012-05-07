cd ..\data
@java -classpath ../lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:msgstore --dbname.0 msg
