package com.example.servicea;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class HelloController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Tracer tracer;

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name) {

        try (Scope scope = tracer.buildSpan("sayHello-handler").startActive(true)) {
            Span span = scope.span();
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("event", name);
            fields.put("message", "this is a log message for name " + name);
            span.log(fields);
            // you can also log a string instead of a map, key=event value=<stringvalue>
            // span.log("this is a log message for name " + name);
            span.setBaggageItem("my-baggage", name + " from service-v2-a:v2");
            String response = formatGreetingRemote(name, span);
            response += formatGreeting(name, span);
            span.setTag("response", response);
            span.setTag(Tags.HTTP_STATUS.getKey(), 200);
            return response;
        }
    }

    private String formatGreeting(String name, Span parentSpan) {
        try (Scope scope = tracer.buildSpan("function formatGreeting").asChildOf(parentSpan).startActive(true)) {
            Span span = scope.span();
            span.log("formatting message locally for name inside service-v2-a:v2.0.0 " + name);
            String response = "Hello " + name + " from service-v2-a v2!";
            return response;
        }
    }

    private String formatGreetingRemote(String name, Span parentSpan) {
        try (Scope scope = tracer.buildSpan("function formatGreetingRemote")
                .asChildOf(parentSpan).startActive(true)) {
            Span childSpan = scope.span();
            String serviceName = System.getenv("SERVICE_FORMATTER");

            String urlPath = "http://" + serviceName + "/formatGreeting";
            childSpan.setTag("calling service-v2-b url", urlPath + "?name=" + name);
            childSpan.setTag("calling service-v2-b http method", "GET");
            childSpan.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);
            URI uri = UriComponentsBuilder //
                    .fromHttpUrl(urlPath) //
                    .queryParam("name", name).build(Collections.emptyMap());
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            childSpan.setTag("service-b response", response.getBody());
            return response.getBody();
        }
    }

    @GetMapping("/error")
    public ResponseEntity<String> replyError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}