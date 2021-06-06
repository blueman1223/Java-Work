package persion.blueman.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean(name = "configurationBean")
    public ConfigurationBean configurationBean() {
        ConfigurationBean bean = new ConfigurationBean();
        bean.setDesc("defined in BeanConfiguration");
        return bean;
    }
}
