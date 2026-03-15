package com.validation.govid.config;

import com.validation.govid.service.GovernmentIdValidatorService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for the {@code gov-id-validator} library.
 *
 * <p>This class is registered via
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * and is picked up automatically by Spring Boot's auto-configuration mechanism.
 * No {@code @Import}, {@code @ComponentScan}, or explicit bean declaration is required
 * in the consuming application.
 *
 * <p>The {@link ConditionalOnMissingBean} annotation on the bean method ensures that
 * consuming applications can override the default service bean by declaring their own
 * {@link GovernmentIdValidatorService} bean — for example, to add custom business rules
 * on top of format validation.
 *
 * <p>Example override:
 * <pre>{@code
 * @Configuration
 * public class MyConfig {
 *     @Bean
 *     public GovernmentIdValidatorService governmentIdValidatorService() {
 *         return new CustomGovernmentIdValidatorService(); // extends the default
 *     }
 * }
 * }</pre>
 *
 * @author Markisio
 * @version 1.0.0
 */
@AutoConfiguration
public class GovIdValidatorAutoConfiguration {

    /**
     * Registers the {@link GovernmentIdValidatorService} as a singleton Spring bean.
     *
     * <p>Only registered if no other bean of type {@link GovernmentIdValidatorService}
     * is already present in the application context, allowing consumer overrides.
     *
     * @return a fully initialised {@link GovernmentIdValidatorService} with all 95
     *         Canadian and US ID patterns loaded
     */
    @Bean
    @ConditionalOnMissingBean
    public GovernmentIdValidatorService governmentIdValidatorService() {
        return new GovernmentIdValidatorService();
    }
}
