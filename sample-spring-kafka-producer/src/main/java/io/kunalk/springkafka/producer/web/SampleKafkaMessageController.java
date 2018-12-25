package io.kunalk.springkafka.producer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.kunalk.springkafka.producer.domain.WorkUnit;
import io.kunalk.springkafka.producer.service.WorkUnitDispatcher;

@RestController
public class SampleKafkaMessageController {

    @Autowired
    private WorkUnitDispatcher workUnitDispatcher;

    @GetMapping("/generateWork")
    public boolean sendMessage(WorkUnit workUnit) {
        return this.workUnitDispatcher.dispatch(workUnit);
    }
}
