package teamredi.retrodiary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/diaries/image/**")
                .setCachePeriod(20);

        registry.addResourceHandler("/uploadFiles/**").addResourceLocations("classpath:/static/uploadFiles/")
                .setCachePeriod(60 * 60 * 24 * 365);
    }
}
