import tool.Constant;

public class SingleSignalObjectMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkerThread workerThread =new WorkerThread(Constant.COMMAND_SINGLE_SIGNAL_OBJECT);
		new Thread(workerThread).start();
	}
}
