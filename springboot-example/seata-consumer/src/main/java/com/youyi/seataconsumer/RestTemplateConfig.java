package com.youyi.seataconsumer;

import io.seata.core.context.RootContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {
    // 配置 RestTemplate，需要注册过滤器到 RestTemplate 中
    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder
                // 向 RestTemplate 中添加过滤器
                .additionalInterceptors(new RestTemplateInterceptor())
                .build();
    }

    static class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // 将 TX_XID 放入请求头
            request.getHeaders().add(RootContext.KEY_XID, RootContext.getXID());
            return execution.execute(request, body);
        }
    }
}
