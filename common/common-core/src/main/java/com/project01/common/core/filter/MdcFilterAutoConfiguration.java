package com.project01.common.core.filter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MdcFilterAutoConfiguration {

    /**
     * Chạy ngay sau filter observation của Micrometer (HIGHEST_PRECEDENCE + 1) để đọc được traceId,
     * và trước Spring Security để lỗi 401/403 cũng có path + traceId.
     */
    @Bean
    public FilterRegistrationBean<MdcRequestFilter> mdcRequestFilter() {
        FilterRegistrationBean<MdcRequestFilter> registration = new FilterRegistrationBean<>(new MdcRequestFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registration;
    }
}
