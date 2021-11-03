package org.springframework.boot.context.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * copied from https://github.com/spring-projects/spring-boot/blob/2.3.x/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/context/properties/ConfigurationBeanFactoryMetadata.java
 * Utility class to memorize {@code @Bean} definition metadata during initialization of
 * the bean factory.
 *
 * @author Dave Syer
 * @since 1.1.0
 * deprecated since 2.2.0 for removal in 2.4.0 in favor of
 * {@link ConfigurationPropertiesBean}
 */
public class ConfigurationBeanFactoryMetadata implements ApplicationContextAware {

    /**
     * The bean name that this class is registered with.
     */
    public static final String BEAN_NAME = ConfigurationBeanFactoryMetadata.class.getName();

    private ConfigurableApplicationContext applicationContext;

    public <A extends Annotation> Map<String, Object> getBeansWithFactoryAnnotation(Class<A> type) {
        Map<String, Object> result = new HashMap<>();
        for (String name : this.applicationContext.getBeanFactory().getBeanDefinitionNames()) {
            if (findFactoryAnnotation(name, type) != null) {
                result.put(name, this.applicationContext.getBean(name));
            }
        }
        return result;
    }

    public <A extends Annotation> A findFactoryAnnotation(String beanName, Class<A> type) {
        Method method = findFactoryMethod(beanName);
        return (method != null) ? AnnotationUtils.findAnnotation(method, type) : null;
    }

    public Method findFactoryMethod(String beanName) {
        ConfigurableListableBeanFactory beanFactory = this.applicationContext.getBeanFactory();
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
            if (beanDefinition instanceof RootBeanDefinition) {
                return ((RootBeanDefinition) beanDefinition).getResolvedFactoryMethod();
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    static void register(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.setBeanClass(ConfigurationBeanFactoryMetadata.class);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(ConfigurationBeanFactoryMetadata.BEAN_NAME, definition);
        }
    }

}