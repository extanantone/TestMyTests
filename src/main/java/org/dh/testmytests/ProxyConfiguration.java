package com.example.CalculadoraMetrosCuadrados.testmytests;

import com.example.CalculadoraMetrosCuadrados.service.CalculateServiceImpl;
import com.example.CalculadoraMetrosCuadrados.service.ICalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan
public class ProxyConfiguration {
    @Bean
    public TemplateResolverCustomizationBeanPostProcessor eventBusBeanPostProcessor() {
        return new TemplateResolverCustomizationBeanPostProcessor();
    }

    public static ICalculateService buildProxy(CalculateServiceImpl bean) {
        return (ICalculateService) Proxy.newProxyInstance(
                ICalculateService.class.getClassLoader(),
                new Class[] { ICalculateService.class },
                new MakeWrongDoubles( bean )
        );
    }

}

class TemplateResolverCustomizationBeanPostProcessor implements BeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        logger.info("postProcessAfterInitialization method invoked");
        if (bean instanceof CalculateServiceImpl) {
            return ProxyConfiguration.buildProxy( (CalculateServiceImpl) bean );
        }
        return bean;
    }
}

class MakeWrongDoubles implements InvocationHandler {

    TestMyTests testMyTests = new TestMyTests();

    private static Logger LOGGER = LoggerFactory.getLogger(
            MakeWrongDoubles.class);

    private Map<String, Method> methods;

    private CalculateServiceImpl target;

    public MakeWrongDoubles(CalculateServiceImpl target) {
        this.target = target;

    }

    private void inicMethods() {
        this.methods = new HashMap<>();
        for(Method method: target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        if(methods == null) inicMethods();
        String methodName = method.getName();
        Method myMethod = methods.get(methodName);
        if(myMethod != null) {
            try {
                Object original = myMethod.invoke(target, args);

                return testMyTests.operate( method.getReturnType(), original );

            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        return null;
    }

}