package main;

import java.util.Scanner;

import model.DialogueStatus;
import tool.PrintToScreen;

public class RunMain {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkerThread workerThread =new WorkerThread();
		new Thread(workerThread).start();
	}
	
	

	
	
	

}
