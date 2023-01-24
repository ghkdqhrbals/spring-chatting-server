package chatting.chat.web.filters;

import chatting.chat.web.filters.filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckFilter())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/user","/css/**","/*.ico","/error","/login","/message","/publish");
    }

}
