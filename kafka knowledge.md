```What is Kafka?
Kafka is a distributed messaging system providing fast, highly scalable and redundant messaging through a pub-sub model. 
Kafka’s distributed design gives it several advantages. 
First, Kafka allows a large number of permanent or ad-hoc consumers. 
Second, Kafka is highly available and resilient to node failures and supports automatic recovery. 
In real world data systems, these characteristics make Kafka an ideal fit for communication and integration between components of large scale data systems.

```Kafka Terminology
All Kafka messages are organized into topics. 
If you wish to send a message you send it to a specific topic and if you wish to read a message you read it from a specific topic. 
A consumer pulls messages off of a Kafka topic while producers push messages into a Kafka topic.
Lastly, Kafka, as a distributed system, runs in a cluster. 
Each node in the cluster is called a Kafka broker.

```Anatomy of a Kafka Topic
Kafka topics are divided into a number of partitions. 
Partitions allow you to parallelize a topic by splitting the data in a particular topic across multiple brokers — each partition can be placed on a separate machine to allow for multiple consumers to read from a topic in parallel. 
Consumers can also be parallelized so that multiple consumers can read from multiple partitions in a topic allowing for very high message processing throughput.
