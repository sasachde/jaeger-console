package com.example.serviceb;

import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.opentracing.propagation.Format;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
//import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.Configuration.CodecConfiguration;

@SpringBootApplication
public class DemoApplication {

	@Bean
	public io.opentracing.Tracer initTracer() {
		SamplerConfiguration samplerConfig = new SamplerConfiguration().withType("const").withParam(1);
		//SenderConfiguration senderConfig = new SenderConfiguration().
		//	withEndpoint("http://jaeger-collector.istio-system.svc:14268/api/traces");
		CodecConfiguration codecConfig = new CodecConfiguration();
		B3TextMapCodec b3Codec = new B3TextMapCodec.Builder().build();
		codecConfig.withCodec(Format.Builtin.HTTP_HEADERS, b3Codec);
		ReporterConfiguration reporterConfig =
			ReporterConfiguration.fromEnv().
				withLogSpans(true);
				//.withSender(senderConfig);
		return Configuration.fromEnv("service-v2-b.sps").withCodec(codecConfig).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
