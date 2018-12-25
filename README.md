# spring-kafka-pubsub
Basic Spring Kafka integration for Publish - subscribe model

## Some High Level Concepts..

A Kafka _broker_ cluster consists of one or more servers where each may have one or more broker processes running. Apache Kafka is designed to be highly available; there are no _master_ nodes. All nodes are interchangeable. Data is replicated from one node to another to ensure that it is still available in the event of a failure.

In Kafka, a _topic_ is a category, similar to a JMS destination or both an AMQP exchange and queue. Topics are partitioned, and the choice of which of a topic's partition a message should be sent to is made by the message producer. Each message in the partition is assigned a unique sequenced ID, its  _offset_. More partitions allow greater parallelism for consumption, but this will also result in more files across the brokers.


_Producers_ send messages to Apache Kafka broker topics and specify the partition to use for every message they produce. Message production may be synchronous or asynchronous. Producers also specify what sort of replication guarantees they want.

_Consumers_ listen for messages on topics and process the feed of published messages. As you'd expect if you've used other messaging systems, this is usually (and usefully!) asynchronous.

Like [Spring XD](http://spring.io/projects/spring-xd) and numerous other distributed system, Apache Kafka uses Apache Zookeeper to coordinate cluster information. Apache Zookeeper provides a shared hierarchical namespace (called _znodes_) that nodes can share to understand cluster topology and availability (yet another reason that [Spring Cloud](https://github.com/spring-cloud/spring-cloud-zookeeper) has forthcoming support for it..).

Zookeeper is very present in your interactions with Apache Kafka. Apache Kafka has, for example, two different APIs for acting as a consumer. The higher level API is simpler to get started with and it handles all the nuances of handling partitioning and so on. It will need a reference to a Zookeeper instance to keep the coordination state.

## Configuring Kafka endpoints
### Producer ->

yml or properties file configuration ->
```
kafka:
  producer:
    bootstrap: localhost:9092
    topic: workunits
```

```
@Component
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {

    private String bootstrap;
    private String topic;

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
@Configuration
public class KafkaProducerConfig {

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;
    ...
}
```

And to produce the message (send to Kafka)
```
@Service
public class WorkUnitDispatcher {

    @Autowired
    private KafkaTemplate<String, WorkUnit> workUnitsKafkaTemplate;

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkUnitDispatcher.class);

    public boolean dispatch(WorkUnit workUnit) {
        try {
            SendResult<String, WorkUnit> sendResult = workUnitsKafkaTemplate.sendDefault(workUnit.getId(), workUnit).get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), workUnit);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Consumer ->

yml or properties file changes->
```
kafka:
  consumer:
    bootstrap: localhost:9092
    group: WorkUnitApp
    topic: workunits
```

```
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

    private String bootstrap;
    private String group;
    private String topic;

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
@Configuration
@EnableKafka
public class KafkaListenerConfig {

    @Autowired
    private KafkaConsumerProperties kafkaConsumerProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WorkUnit> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WorkUnit> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WorkUnit> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps(), stringKeyDeserializer(), workUnitJsonValueDeserializer());
    }


    @Bean
    public Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrap());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroup());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        return props;
    }

    @Bean
    public Deserializer stringKeyDeserializer() {
        return new StringDeserializer();
    }

    @Bean
    public Deserializer workUnitJsonValueDeserializer() {
        return new JsonDeserializer(WorkUnit.class);
    }
}

```
And the listener
```
@Service
public class WorkUnitsConsumer {
    private static final Logger log = LoggerFactory.getLogger(WorkUnitsConsumer.class);

    @KafkaListener(topics = "workunits")
    public void onReceiving(WorkUnit workUnit, @Header(KafkaHeaders.OFFSET) Integer offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Processing topic = {}, partition = {}, offset = {}, workUnit = {}",
                topic, partition, offset, workUnit);
    }
}
```


## Getting Started
These instructions will show how to run kafka locally and how to install kafkacat, a tool to display messages in a kafka topic

### Run kafka locally
In a terminal cd to the root of this project and run
```
docker-compose up -d
```
Check both kafka and zookeeper are running
```
docker ps
```
### Install kafkacat
Install kafkacat utility using the local package manager.

```
sudo apt-get install kafkacat
```

# Start up the Producer, from the root of the project

[source, java]
----
cd sample-spring-kafka-producer
../gradlew bootRun
----

# Start up the Consumer

[source, java]
----
cd sample-spring-kafka-consumer
../gradlew bootRun
----


## At this point some sample messages can be generated using this endpoint:

http://localhost:8080/generateWork?id=1&definition=thisIsCool

The producer should print a message on the console that the Work unit has been dispatched and the consumer should receive and process the Work unit
