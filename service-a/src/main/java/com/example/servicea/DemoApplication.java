package com.example.servicea;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.opentracing.propagation.Format;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

	@Bean
	public io.opentracing.Tracer initTracer() {
		Configuration.SamplerConfiguration samplerConfig =
			new Configuration.SamplerConfiguration().withType("const").withParam(1);
		//Configuration.SenderConfiguration senderConfig = new Configuration.SenderConfiguration().
		//	withEndpoint("http://jaeger-collector.istio-system.svc:14268/api/traces");
		Configuration.CodecConfiguration codecConfig = new Configuration.CodecConfiguration();
		B3TextMapCodec b3Codec = new B3TextMapCodec.Builder().build();
		codecConfig.withCodec(Format.Builtin.HTTP_HEADERS, b3Codec);
		Configuration.ReporterConfiguration reporterConfig =
			Configuration.ReporterConfiguration.fromEnv()
				.withLogSpans(true);
				//.withSender(senderConfig);
		return Configuration.fromEnv("service-v2-a.sps")
			.withCodec(codecConfig)
			.withSampler(samplerConfig)
			.withReporter(reporterConfig)
			.getTracer();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
