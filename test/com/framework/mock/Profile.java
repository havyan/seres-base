package com.framework.mock;

public class Profile {

	private int height;

	private int weight;

	private String face;
	
	private Person person;

	public Profile() {
		super();
	}

	public Profile(String face, int height, int weight) {
		super();
		this.face = face;
		this.height = height;
		this.weight = weight;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
