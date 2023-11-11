package ch.scaille.tcwriter.server.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {

	@Bean
	FactoryBean<EntityManagerFactory> entityManagerFactory(DataSource dataSource) {
		final var em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("ch.scaille.tcwriter.server.model", "ch.scaille.tcwriter.server.repository");
		em.setPersistenceUnitName("TCWriter");

		final var vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		final var transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	@Bean
	PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		final var properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		return properties;
	}
}