https://sookocheff.com/post/kafka/kafka-in-a-nutshell/

# What is Kafka?
Kafka is a distributed messaging system providing fast, highly scalable and redundant messaging through a pub-sub model. 
Kafka’s distributed design gives it several advantages. 
First, Kafka allows a large number of permanent or ad-hoc consumers. 
Second, Kafka is highly available and resilient to node failures and supports automatic recovery. 
In real world data systems, these characteristics make Kafka an ideal fit for communication and integration between components of large scale data systems.

# Kafka Terminology
All Kafka messages are organized into topics. 
If you wish to send a message you send it to a specific topic and if you wish to read a message you read it from a specific topic. 
A consumer pulls messages off of a Kafka topic while producers push messages into a Kafka topic.
Lastly, Kafka, as a distributed system, runs in a cluster. 
Each node in the cluster is called a Kafka broker.

# Anatomy of a Kafka Topic
Kafka topics are divided into a number of partitions. 
Partitions allow you to parallelize a topic by splitting the data in a particular topic across multiple brokers — each partition can be placed on a separate machine to allow for multiple consumers to read from a topic in parallel. 
Consumers can also be parallelized so that multiple consumers can read from multiple partitions in a topic allowing for very high message processing throughput.

Each message within a partition has an identifier called its offset. 
The offset the ordering of messages as an immutable sequence. Kafka maintains this message ordering for you. 
Consumers can read messages starting from a specific offset and are allowed to read from any offset point they choose, allowing consumers to join the cluster at any point in time they see fit. 
Given these constraints, each specific message in a Kafka cluster can be uniquely identified by a tuple consisting of the message’s topic, partition, and offset within the partition.

![alt text](https://sookocheff.com/post/kafka/kafka-in-a-nutshell/log-anatomy.png)

# Log Anatomy
Another way to view a partition is as a log. 
A data source writes messages to the log and one or more consumers reads from the log at the point in time they choose. 

# Data Log
Kafka retains messages for a configurable period of time and it is up to the consumers to adjust their behaviour accordingly. 
For instance, if Kafka is configured to keep messages for a day and a consumer is down for a period of longer than a day, the consumer will lose messages. 
However, if the consumer is down for an hour it can begin to read messages again starting from its last known offset. From the point of view of Kafka, it keeps no state on what the consumers are reading from a topic.

# Partitions and Brokers
Each broker holds a number of partitions and each of these partitions can be either a leader or a replica for a topic. 
All writes and reads to a topic go through the leader and the leader coordinates updating replicas with new data. 
If a leader fails, a replica takes over as the new leader.

# Producers
Producers write to a single leader, this provides a means of load balancing production so that each write can be serviced by a separate broker and machine. 
In the first image, the producer is writing to partition 0 of the topic and partition 0 replicates that write to the available replicas.

# Consumers and Consumer Groups
Consumers read from any single partition, allowing you to scale throughput of message consumption in a similar fashion to message production. 
Consumers can also be organized into consumer groups for a given topic — each consumer within the group reads from a unique partition and the group as a whole consumes all messages from the entire topic. 
If you have more consumers than partitions then some consumers will be idle because they have no partitions to read from. 
If you have more partitions than consumers then consumers will receive messages from multiple partitions. 
If you have equal numbers of consumers and partitions, each consumer reads messages in order from exactly one partition.

