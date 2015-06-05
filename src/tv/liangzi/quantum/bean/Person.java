package tv.liangzi.quantum.bean;

import java.io.Serializable;
import java.util.List;

public class Person {
	private String responseCode;

	private String responseMsg;

	private List<PeopleDetails> users;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public List<PeopleDetails> getUsers() {
		return users;
	}

	public void setUsers(List<PeopleDetails> users) {
		this.users = users;
	}


}
