package br.com.vraptor.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	@Transient
	private List<House> houses;

	public String nome;

	private String name;

	private int age;

	public Person(Long id) {
		this.id = id;
	}

	public Person(Long id, List<House> houses) {
		this.id = id;
		this.houses = houses;
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public List<House> getHouses() {
		return houses;
	}
}