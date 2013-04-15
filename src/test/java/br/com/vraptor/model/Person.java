package br.com.vraptor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

	@Id
	@GeneratedValue
	public Long id;

	private String name;

	private int age;

	public Person() {
	}

	public Person(Long id) {
		this.id = id;
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}