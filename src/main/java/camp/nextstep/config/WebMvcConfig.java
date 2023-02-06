package camp.nextstep.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import camp.nextstep.support.ResourceVersion;

import javax.servlet.Filter;
import java.time.Duration;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    public static final String PREFIX_STATIC_RESOURCES = "/resources";


    private final ResourceVersion version;

    @Autowired
    public WebMvcConfig(ResourceVersion version) {
        this.version = version;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor interceptor = new WebContentInterceptor();
        //no-store, no-cache, must-revalidate 를 확인해보려면 인터셉터 구간 설정을 뺴고 테스트해보시면 됩니다.
        interceptor.addCacheMapping(CacheControl.noCache().cachePrivate(), "/**");
        registry.addInterceptor(interceptor);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter etagHeaderFilter = new ShallowEtagHeaderFilter();
        registration.setFilter(etagHeaderFilter);
        registration.addUrlPatterns("/etag", PREFIX_STATIC_RESOURCES + "/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<HttpHeaderFilter> httpHeaderFilter(){
        var filter = new HttpHeaderFilter();
        final FilterRegistrationBean<HttpHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
    }
}
