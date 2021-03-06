package com.perspecta.luegimport.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(basePackages = {"com.perspecta.luegimport.business.domain"})
@EnableTransactionManagement
@Configuration
public class JpaConfig {

}