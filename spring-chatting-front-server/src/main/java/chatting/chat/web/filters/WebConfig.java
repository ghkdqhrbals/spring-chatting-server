package chatting.chat.web.filters;

import chatting.chat.web.filters.filter.URLCheckFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new URLCheckFilter())
                .order(1)
                .addPathPatterns("/**");
    }
}
