# BTrace scripts for investigate internals of Hadoop.

_Work in progress_

# Usage

## Compile the current project

```
mvn package
```

## Download btrace

Get the latest release from here: https://github.com/btraceio/btrace/releases

```
mkdir btrace
cd btrace
wget https://github.com/btraceio/btrace/releases/download/v1.3.9/btrace-bin-1.3.9.zip
unzip btrace*.zip
```

## Attach to an existing process

Find the process id of your java app:

```
jps -l
18 org.apache.hadoop.hdfs.server.namenode.NameNode
114 sun.tools.jps.Jps
```

Join to the process:

```
./btrace -u 18 target/classes/META-INF/btrace/RpcCalls.class
```
