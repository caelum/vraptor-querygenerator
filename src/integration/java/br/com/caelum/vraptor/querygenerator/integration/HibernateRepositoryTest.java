package br.com.caelum.vraptor.querygenerator.integration;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.vraptor.model.Person;
import br.com.vraptor.querygenerator.PersonRepositoryHibernate;
import br.com.vraptor.querygenerator.QueryExecutor;
import br.com.vraptor.querygenerator.RepositoryHibernateProxyFactory;

public class HibernateRepositoryTest {

	private Session session;
	private SessionFactory sessionFactory;

	@Before
	public void setup() {
		Configuration cfg = new Configuration();
		cfg.configure(getClass().getResource("/hibernate.cfg.test.xml"));
		cfg.addAnnotatedClass(Person.class);

		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		ServiceRegistry serviceRegistry = builder.applySettings(cfg.getProperties()).buildServiceRegistry();
		sessionFactory = cfg.buildSessionFactory(serviceRegistry);

		session = sessionFactory.openSession();
	}

	@Test
	public void shouldFindAllEntities() throws Exception {
		RepositoryHibernateProxyFactory repositoryHibernateProxyFactory = new RepositoryHibernateProxyFactory(new QueryExecutor(session));
		PersonRepositoryHibernate personRepository = repositoryHibernateProxyFactory
				.getInstance(PersonRepositoryHibernate.class);

		Person person = new Person("Nome", 26);
		session.save(person);

		Person person2 = personRepository.findById(1L);
		assertEquals("Nome", person2.getName());

	}

	@After
	public void tearDown() {
		session.close();
		sessionFactory.close();
	}
}
