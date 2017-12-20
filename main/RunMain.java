package main;

import java.util.Scanner;

import model.DialogueStatus;
import tool.PrintToScreen;

public class RunMain {

	public static void main(String[] args) {
		WorkerThread workerThread = new WorkerThread();
		new Thread(workerThread).start();
	}

}
