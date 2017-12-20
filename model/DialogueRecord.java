package model;

import java.io.Serializable;
import java.util.Date;

//将一次conversion作为一个DialogueRecord list，放入map对象中，map以account为key，list为value
public class DialogueRecord implements Serializable {
	
	public DialogueRecord(String account, String dialogue, Date time) {
		super();
		this.account = account;
		this.dialogue = dialogue;
		this.time = time;
	}
	private String account;
	private String dialogue;
	private Date time;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDialogue() {
		return dialogue;
	}
	public void setDialogue(String dialogue) {
		this.dialogue = dialogue;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
}
