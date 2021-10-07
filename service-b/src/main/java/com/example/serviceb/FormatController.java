package com.example.serviceb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FormatController {

    @Autowired
    private Tracer tracer;

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name, HttpServletRequest httpServletRequest) {
        try (Scope scope = tracer.buildSpan("format-greeting").startActive(true)) {
            Span span = scope.span();
            span.setTag(Tags.HTTP_METHOD.getKey(), httpServletRequest.getMethod());
            span.setTag(Tags.HTTP_URL.getKey(), httpServletRequest.getRequestURI());
            span.log("formatting message remotely for name " + name);
            String response = "Hello, from service-v2-b " + name + "!";
            String myBaggage = span.getBaggageItem("my-baggage");
            span.log("this got sent from service-v2-a " + myBaggage);
            return response;
        }
    }
}