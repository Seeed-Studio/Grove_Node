package com.seeedstudio.node.data;

import java.util.ArrayList;


public class Task {
	public String name;
	public int    id;
	public ArrayList<Requirement> requirements;
	public ActuatorAction action;
	
	public Task(int id, ArrayList<Requirement> requirements, ActuatorAction action) {
		this.id = id;
		this.requirements = requirements;
		this.action = action;
		this.name = "task " + id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
