hive-delegation-token
=====================

### Introduction
Simple standalone application to get delegation token from Hive.

### Build the app
To build, you need Java 1.8, git and maven on the box.
Do a git clone of this repo and then run:
```
cd hive-delegation-token
mvn clean package
```

### Running the app
```
kinit ...
java -jar target/hive-delegation-token-1.0-SNAPSHOT.jar
```
