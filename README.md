Work In Progress

BTrace scripts for investigate internals of Hadoop.

```
sudo docker-compose exec  namenode /root/hadoop-btrace/btrace/bin/btrace  -u  12  /root/hadoop-btrace/target/classes/META-INF/btrace/RpcCalls.class
```

# Usage

## Compile the current project

```mvn package```

## Download btrace

Get the latest release from here: https://github.com/btraceio/btrace/releases

```
mkdir btrace
cd btrace
wget https://github.com/btraceio/btrace/releases/download/v1.3.9/btrace-bin-1.3.9.zip
unzip btrace*.zip
```
## Attach to an existing process

root@namenode: /btrace/btrace> jps -l
18 org.apache.hadoop.hdfs.server.namenode.NameNode
114 sun.tools.jps.Jps
