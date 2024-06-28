package it.andrea.test.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
public class RestTemplateConfiguration {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplateBuilder().errorHandler(new RestTemplateErrorHandler()).build();
	}

	private class RestTemplateErrorHandler implements ResponseErrorHandler {
		@Override
		public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
			return clientHttpResponse.getStatusCode() != HttpStatus.OK;
		}

		@Override
		public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
			InputStream inputStream = clientHttpResponse.getBody();
			InputStreamReader isReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(isReader);
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			throw new IOException(sb.toString());
		}
	}

}
