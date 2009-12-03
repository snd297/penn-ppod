package edu.upenn.cis.ppod.util;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atomikos.jdbc.AtomikosDataSourceBean;

import edu.upenn.cis.ppod.model.PPodVersionInfoInterceptor;

/**
 * 
 * @author Sam Donnelly
 */
public class HibernateJndiUtil {
	private static Context jndiContext;

	/** Logger. */
	private static Logger logger = LoggerFactory
			.getLogger(HibernateJndiUtil.class);

	/**
	 * Configuration used for building and rebuilding
	 * <code>sessionFactory</code>.
	 */
	private static Configuration configuration;

	static {
		try {

			logger.debug("Initializing Hibernate");

			// Read hibernate.properties, if present
			// Build it and bind it to JNDI
			configuration = new AnnotationConfiguration()
					.configure("/hibernateJndi.cfg.xml");
			// Get a handle to the registry (reads jndi.properties)
			jndiContext = new InitialContext();

			final Properties hibernateProperties = configuration
					.getProperties();

			AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
			ds.setUniqueResourceName("mysql");
			ds
					.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");

			Properties p = new Properties();
			p.setProperty("user", hibernateProperties
					.getProperty(Environment.USER));
			p.setProperty("password", hibernateProperties
					.getProperty(Environment.PASS));
			p.setProperty("URL", hibernateProperties
					.getProperty(Environment.URL));
			ds.setXaProperties(p);

			ds.setPoolSize(5);

			jndiContext.rebind("pPod", ds);

			configuration.buildSessionFactory();

			// Read hibernate.cfg.xml (has to be present)
			configuration.configure().setInterceptor(
					new PPodVersionInfoInterceptor());

			logger.debug("jndiContext.getEnvironment(): {}", jndiContext.getEnvironment());

		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory(String sessionFactoryName) {
		SessionFactory sessionFactory;
		try {
			sessionFactory = (SessionFactory) jndiContext
					.lookup(sessionFactoryName);
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
		return sessionFactory;
	}

	/**
	 * Initialize the transaction manager.
	 */
	public static UserTransaction newUserTransaction() {
		return new com.atomikos.icatch.jta.UserTransactionImp();
	}
}
