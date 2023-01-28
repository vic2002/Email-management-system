package view.model;

import java.util.List;

import server.model.User;

/** A model describing how the list of users looks like. */
public class Users {
	private int numberOfUsers;
	private List<User> users;
	public int getNumberOfUsers() {
		return numberOfUsers;
	}
	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
