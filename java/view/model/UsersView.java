package view.model;

import java.util.List;

public class UsersView {
	private int numberOfUsers;
	private List<UserView> users;
	public int getNumberOfUsers() {
		return numberOfUsers;
	}
	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	public List<UserView> getUsers() {
		return users;
	}
	public void setUsers(List<UserView> users) {
		this.users = users;
	}
}
