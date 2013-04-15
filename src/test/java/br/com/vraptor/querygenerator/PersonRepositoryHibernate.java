package br.com.vraptor.querygenerator;

import java.util.Collection;
import java.util.List;

import br.com.vraptor.model.Person;

public interface PersonRepositoryHibernate {

	public Collection<Person> findAll();

	public Person findById(Long id);

	public Person findByNameAndAge(String name, int age);

	public void unfindByNameAndAge(String name, int age);

	public List<Person> findAllByNameAndAge(String name, int age);

	public Person findByNameAndCity(String name, City city);
}