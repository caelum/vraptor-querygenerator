package br.com.vraptor.querygenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.vraptor.model.House;
import br.com.vraptor.model.Person;

public class RepositoryTest {

	private Session session;
	private PersonRepositoryHibernate repository;
	private RepositoryHibernateProxyFactory factory;

	@Before
	public void setUp() {
		session = mock(Session.class);
		this.factory = new RepositoryHibernateProxyFactory(session, new ParameterNameExtractors());
		repository = factory.getInstance(PersonRepositoryHibernate.class);
	}

	@Test
	public void shouldGenerateAFindByIdImplementation() {
		when(session.load(Person.class, 1L)).thenReturn(new Person(1L, Collections.<House> emptyList()));

		Person person = repository.findById(1L);

		verify(session).load(Person.class, 1L);
		assertNotNull(person);
	}

	@Test
	public void shouldFindACollectionWithoutGeneric() {
		fail();
	}

	@Test
	public void shouldFindAllEntities() {
		// extrair persons e garantir order
		Criteria criteria = mock(Criteria.class);
		when(criteria.list()).thenReturn(Arrays.asList(new Person(1L), new Person(2L)));
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		Collection<Person> findAll = repository.findAll();

		verify(session).createCriteria(Person.class);
		verify(criteria).list();
		assertEquals(2, findAll.size());
	}

	@Test
	public void shouldSupportFindQueriesWithTwoParameters() {
		Criteria criteria = mock(Criteria.class);
		Person guilherme = new Person("guilherme", 18);
		when(criteria.uniqueResult()).thenReturn(guilherme);
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		Person found = repository.findByNameAndAge("Guilherme", 18);

		verify(criteria).add(Mockito.argThat(new RestrictionEq("age", 18)));
		verify(criteria).add(Mockito.argThat(new RestrictionEq("name", "Guilherme")));
		assertEquals(guilherme, found);
	}

	@Test
	public void shouldSupportQueriesWithLike() {
		Criteria criteria = mock(Criteria.class);
		Person guilherme = new Person("guilherme", 18);
		when(criteria.uniqueResult()).thenReturn(guilherme);
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		Person found = repository.findBySimilarName("Guilherme");

		verify(criteria).add(Mockito.argThat(new RestrictionLike("name", "Guilherme")));
		assertEquals(guilherme, found);
	}

	@Test
	public void shouldSupportFindAllQueriesWithTwoParameters() {
		Criteria criteria = mock(Criteria.class);
		Person guilherme = new Person("guilherme", 18);
		when(criteria.list()).thenReturn(Arrays.asList(guilherme, guilherme));
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		List<Person> found = repository.findAllByNameAndAge("Guilherme", 18);

		verify(criteria).add(Mockito.argThat(new RestrictionEq("age", 18)));
		verify(criteria).add(Mockito.argThat(new RestrictionEq("name", "Guilherme")));
		assertEquals(2, found.size());
	}

	@Test
	public void shouldSupportFindQueriesWithSubQuery() {
		Criteria criteria = mock(Criteria.class);
		Criteria cityCriteria = mock(Criteria.class, "cityCriteria");
		Person guilherme = new Person("guilherme", 18);
		when(criteria.uniqueResult()).thenReturn(guilherme);
		when(criteria.createCriteria("city")).thenReturn(cityCriteria);
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		Person found = repository.findByNameAndCity("Guilherme",
				and(CityRepository.class).findByName("Sao Paulo"));

		verify(cityCriteria).add(Mockito.argThat(new RestrictionEq("name", "Sao Paulo")));
		verify(criteria).add(Mockito.argThat(new RestrictionEq("name", "Guilherme")));
		assertEquals(guilherme, found);
	}

	@Test
	public void shouldSupportFindQueriesWithSubSubQuery() {
		Criteria criteria = mock(Criteria.class);
		Criteria cityCriteria = mock(Criteria.class, "cityCriteria");
		Criteria stateCriteria = mock(Criteria.class, "stateCriteria");
		Person guilherme = new Person("guilherme", 18);
		when(criteria.uniqueResult()).thenReturn(guilherme);
		when(criteria.createCriteria("city")).thenReturn(cityCriteria);
		when(cityCriteria.createCriteria("state")).thenReturn(stateCriteria);
		when(session.createCriteria(Person.class)).thenReturn(criteria);

		Person found = repository.findByNameAndCity("Guilherme",
				and(CityRepository.class).findByNameAndState("Sao Paulo", and(StateRepository.class).findByName("SP")));

		verify(stateCriteria).add(Mockito.argThat(new RestrictionEq("name", "SP")));
		verify(cityCriteria).add(Mockito.argThat(new RestrictionEq("name", "Sao Paulo")));
		verify(criteria).add(Mockito.argThat(new RestrictionEq("name", "Guilherme")));
		assertEquals(guilherme, found);
	}

	private static <T> T and(Class<T> type) {
		InvocationHandler handler = new SubQueryCreator();

		return (T) Proxy.newProxyInstance(
				RepositoryTest.class.getClassLoader(),
				new Class<?>[] { type },
				handler);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotSupportNotDefinedQueries() {
		repository.unfindByNameAndAge("Guilherme", 18);
	}
}

interface City {

}

interface CityRepository {
	City findByName(String name);

	City findByNameAndState(String name, State state);
}

interface State {

}

interface StateRepository {
	State findByName(String name);
}