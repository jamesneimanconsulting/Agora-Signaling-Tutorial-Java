package main;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.agora.signal.Signal;
import io.agora.signal.Signal.LoginSession.Channel;
import model.DialogueRecord;
import model.DialogueStatus;
import model.User;
import tool.Constant;
import tool.PrintToScreen;

public class WorkerThread implements Runnable{

	private boolean mainThreadStatus = false;

	private String token = "_no_need_token";
	private String currentUser;
	private boolean timeOutFlag;
	private DialogueStatus currentStatus = DialogueStatus.UNLOGIN;
	private HashMap<String, User> users;
	private HashMap<String,List<DialogueRecord>> accountDialogueRecords=null;
	private HashMap<String,List<DialogueRecord>> channelDialogueRecords=null;
	List<DialogueRecord> currentAccountDialogueRecords = null;
	List<DialogueRecord> currentChannelDialogueRecords = null;
	private Scanner in;
	private Signal sig;
	public WorkerThread(){
		this.mainThreadStatus = true;
		in= new Scanner(System.in);	
		timeOutFlag = false;
		users = new HashMap<String, User>();
		//if(initRecordFile()){
			initSignal();	
			accountDialogueRecords = new HashMap<String,List<DialogueRecord>>();
			channelDialogueRecords = new HashMap<String,List<DialogueRecord>>();
		//}
	}
	
	public void run() {
		PrintToScreen.printToScreenLine("**************************************************");
		PrintToScreen.printToScreenLine("* Agora Signaling Tutorial  ---SDK version:1.2.0 *");
		PrintToScreen.printToScreenLine("**************************************************");
		while(this.mainThreadStatus){	
				switch(currentStatus)
				{
				//	UNLOGIN, LOGINED, p2p, channle  
				case UNLOGIN:
					dealWithUnLogin();
					break;
				case LOGINED:
					dealWithLogined();
					break;	
				case SINGLE_POINT:
					dealWithP2P();
					break;	
				case CHANNEL:
					dealWithChannel();
					break;	
				default:
					PrintToScreen.printToScreenLine("*************programe in error states***************");
					break;
				}
		}
	}
	
	
	//����û�е�¼�����
	public void dealWithUnLogin()
	{
		while(this.mainThreadStatus&&this.currentStatus==DialogueStatus.UNLOGIN)
		{
			PrintToScreen.printToScreenLine("Please enter your accout to login....");
			PrintToScreen.printToScreen("Account:");
			String accountName = in.nextLine();// ��ȡ���з�Ϊ�����
			if(checkAccountName(accountName)){
				login(accountName);			
			}else{
				PrintToScreen.printToScreenLine("Please recheck the account,it has format error");
				PrintToScreen.printToScreenLine("**************************************************");
			}

		}
	}
	
	//����û�е�¼�����
	public void dealWithLogined()
	{
		while(this.mainThreadStatus&&this.currentStatus==DialogueStatus.LOGINED)
		{
			boolean loginedFlag = true;
			while(this.mainThreadStatus&&loginedFlag){
				PrintToScreen.printToScreenLine("************************************************");	
				PrintToScreen.printToScreenLine("you can input '"+Constant.COMMAND_LOGOUT+"' to logout....");
				PrintToScreen.printToScreenLine("Please chose chart type.....");
				PrintToScreen.printToScreenLine("**********************************************");
				PrintToScreen.printToScreenLine("*input '1' choose single_point chart        *");
				PrintToScreen.printToScreenLine("*input '2' choose channel chart              *");
				PrintToScreen.printToScreenLine("**********************************************");		
				PrintToScreen.printToScreen("choose option:");
				String command = in.nextLine();// ��ȡ���з�Ϊ�����
				if(command.equals(Constant.COMMAND_LOGOUT))
				{
					loginedFlag = false;
					users.get(currentUser).getSession().logout();
					timeOutFlag = false;
					//���ڵǳ���log
			        wait_time(users.get(currentUser).getLogoutLatch(), Constant.TIMEOUT,currentUser);
				}else if(command.equals(Constant.COMMAND_TYPE_SINGLE_POINT)){
					loginedFlag = false;
					currentStatus = DialogueStatus.SINGLE_POINT;
				}else if(command.equals(Constant.COMMAND_TYPE_CHANNEL)){
					loginedFlag = false;
					currentStatus = DialogueStatus.CHANNEL;				
				}else{
					PrintToScreen.printToScreenLine("************************************************");	
					PrintToScreen.printToScreenLine("...your command:"+command+" can't understand...");	
				}
			}
		}
	}
	
	
	//����SINGLE_POINTͨѶ
	public void dealWithP2P(){
		while(this.mainThreadStatus&&this.currentStatus==DialogueStatus.SINGLE_POINT)
		{
			PrintToScreen.printToScreenLine("************************************************");	
			PrintToScreen.printToScreenLine("you can input '"+Constant.COMMAND_LOGOUT+"' to logout....");
			PrintToScreen.printToScreenLine("Please enter the opposite account.....");	
			PrintToScreen.printToScreen("Input Opposite Account:");
			String oppositeAccount = in.nextLine();// ��ȡ���з�Ϊ�����
			if(checkAccountName(oppositeAccount)){
				intoP2PConversation(oppositeAccount);
			}else{
				PrintToScreen.printToScreenLine("Please recheck the oppositeAccount,it has format error");
				PrintToScreen.printToScreenLine("**************************************************");
			}			
		}	
	}
	
	//��ѯ�û��Ƿ����ߣ�
	public void queryUserStatus(String account){

	}
	
	public void intoP2PConversation(String oppositeAccount){
		boolean p2pFlag = true;
		currentAccountDialogueRecords = initP2PRecord(oppositeAccount);
		PrintToScreen.printToScreenLine("**************************************************");
		if(currentAccountDialogueRecords==null||currentAccountDialogueRecords.size()==0){
			currentAccountDialogueRecords = new ArrayList<DialogueRecord>();
		}else{
			for(int i=0;i<currentAccountDialogueRecords.size();i++){
				PrintToScreen.printToScreenLine(currentAccountDialogueRecords.get(i).getAccount()+":"+currentAccountDialogueRecords.get(i).getDialogue());			
			}
		}
		PrintToScreen.printToScreenLine("****above is history record :"+currentAccountDialogueRecords.size()+"**************");
		PrintToScreen.printToScreenLine("you can send message now and input '"+Constant.COMMAND_LEAVE_CHART+"' to leave this session");
		//TODO ����ж��Ƿ����ߣ�����û��api�ӿ�
		PrintToScreen.printToScreenLine("***************"+oppositeAccount+"******************");
		while(this.mainThreadStatus&&p2pFlag){	
			String command = in.nextLine();// ��ȡ���з�Ϊ�����
			if(command.equals(Constant.COMMAND_LEAVE_CHART))
			{
				p2pFlag = false;
				currentStatus = DialogueStatus.LOGINED;	
				//��¼����
				accountDialogueRecords.put(oppositeAccount, currentAccountDialogueRecords);
			}else{
				sendMsg(command,oppositeAccount);
			}
		}	
	}
	
	public List<DialogueRecord> initP2PRecord(String oppositeAccount)
	{
		return accountDialogueRecords.get(oppositeAccount);
	}
	

	
	
	//����channelͨѶ
	public void dealWithChannel(){
		while(this.mainThreadStatus&&this.currentStatus==DialogueStatus.CHANNEL)
		{
			PrintToScreen.printToScreenLine("************************************************");	
			PrintToScreen.printToScreenLine("you can input '"+Constant.COMMAND_LOGOUT+"' to logout....");
			PrintToScreen.printToScreenLine("Please enter the channel name.....");	
			PrintToScreen.printToScreen("channel name:");
			String channelName = in.nextLine();// ��ȡ���з�Ϊ�����
			if(checkAccountName(channelName)){
				joinChannel(channelName);
				if(this.currentStatus==DialogueStatus.CHANNEL&&timeOutFlag==false){
					intoChannelConversation(channelName);		
				}else{
					PrintToScreen.printToScreenLine("****************join channel error*************");		
				}
			}else{
				PrintToScreen.printToScreenLine("Please recheck the channel name,it has format error");
				PrintToScreen.printToScreenLine("**************************************************");
			}			
		}					
	}
	
	//channel����ش���intoChannelConversation
	public void intoChannelConversation(String channelName){
		boolean channelFlag = true;
		currentChannelDialogueRecords = initChannelRecord(channelName);
		PrintToScreen.printToScreenLine("*******************channel:"+channelName+"*****************");
		if(currentChannelDialogueRecords==null||currentChannelDialogueRecords.size()==0){
			currentChannelDialogueRecords = new ArrayList<DialogueRecord>();
		}else{
			for(int i=0;i<currentChannelDialogueRecords.size();i++){
				PrintToScreen.printToScreenLine(currentChannelDialogueRecords.get(i).getAccount()+":"+currentChannelDialogueRecords.get(i).getDialogue());			
			}
		}
		PrintToScreen.printToScreenLine("****above is history record :"+currentChannelDialogueRecords.size()+"**************");
		PrintToScreen.printToScreenLine("you can send message now and input '"+Constant.COMMAND_LEAVE_CHART+"' to leave this session");
		//�����һ��channel status
		while(this.mainThreadStatus&&channelFlag){	

			String command = in.nextLine();// ��ȡ���з�Ϊ�����
			if(command.equals(Constant.COMMAND_LEAVE_CHART))
			{
				channelFlag = false;
				currentStatus = DialogueStatus.LOGINED;	
				//�뿪channel
				users.get(currentUser).getChannel().channelLeave();
				//��¼����
				channelDialogueRecords.put(channelName, currentChannelDialogueRecords);
			}else{
				channelDeal(command,channelName);
			}
		}			
		
	}
	
	//channel��ʵ�ʴ���.......
	public void channelDeal(String command,String channelName){
		users.get(currentUser).getChannel().messageChannelSend(command);
	}
	
	//����channel�Ĵ���
	public void joinChannel(String channelName){
        final CountDownLatch channelJoindLatch = new CountDownLatch(1);
		Channel  channel = users.get(currentUser).getSession().channelJoin(channelName, new Signal.ChannelCallback(){
            @Override
            public void onChannelJoined(Signal.LoginSession session, Signal.LoginSession.Channel channel) {
        		//PrintToScreen.printToScreenLine("join channel "+channelName+"  success");
            	channelJoindLatch.countDown();
            }

            @Override
            public void onChannelUserList(Signal.LoginSession session, Signal.LoginSession.Channel channel, List<String> users, List<Integer> uids) {
/*            	for(String u:users){
               		PrintToScreen.printToScreenLine(channelName+" join success"); 		
            	}*/
            }


            @Override
            public void onMessageChannelReceive(Signal.LoginSession session, Signal.LoginSession.Channel channel, String account, int uid, String msg) {

            	if(currentChannelDialogueRecords!=null&&currentStatus == DialogueStatus.CHANNEL){
                	PrintToScreen.printToScreenLine(account +":"+msg);  
                	DialogueRecord dialogueRecord= new DialogueRecord(account, msg, new Date());
                	currentChannelDialogueRecords.add(dialogueRecord);
            	}

            }
            
            //���˼���
            @Override
            public void onChannelUserJoined(Signal.LoginSession session, Signal.LoginSession.Channel channel, String account, int uid) {
            	if(currentStatus == DialogueStatus.CHANNEL){
                	PrintToScreen.printToScreenLine("..."+account +" joined channel... ");  
            	}
            }
            
            //�����뿪
            @Override
            public void onChannelUserLeaved(Signal.LoginSession session, Signal.LoginSession.Channel channel, String account, int uid) {
            	if(currentStatus == DialogueStatus.CHANNEL){
                	PrintToScreen.printToScreenLine("..."+account +" leave channel... ");  
            	}
            }

            @Override
            public void onChannelLeaved(Signal.LoginSession session, Signal.LoginSession.Channel channel, int ecode) {
                //������еļ�¼
            	if(currentStatus == DialogueStatus.CHANNEL){
                    currentStatus = DialogueStatus.LOGINED;
                	//PrintToScreen.printToScreenLine(currentUser+" leave channel successed");        		
            	}

            }
            
        });
		timeOutFlag = false;
		//����channel��timeout
        wait_time(channelJoindLatch, Constant.TIMEOUT,channelName);
        //����Ƶ��
        if(timeOutFlag==false){
    		users.get(currentUser).setChannel(channel);   	
        }

	}
	
	
	public List<DialogueRecord> initChannelRecord(String channelName)
	{
		return channelDialogueRecords.get(channelName);
	}
	
	public void sendMsg(String msg,String oppositeAccount){
		Signal.LoginSession currentSession = users.get(currentUser).getSession();
		currentSession.messageInstantSend(oppositeAccount, msg, new Signal.MessageCallback(){
            @Override
            public void onMessageSendSuccess(Signal.LoginSession session) {
            	DialogueRecord dialogueRecord= new DialogueRecord(currentUser, msg, new Date());
            	currentAccountDialogueRecords.add(dialogueRecord);
            	PrintToScreen.printToScreenLine(currentUser+":"+msg);
            	//PrintToScreen.printToScreenLine(currentUser+" msg send success");
            }

            @Override
            public void onMessageSendError(Signal.LoginSession session, int ecode) {
            	PrintToScreen.printToScreenLine(currentUser+" msg send error");	
            }
        });
	}
	
	
	//��ʼ���洢���
	//�洢�Ķ���Ϊ����ʼ�����򣬶������´�һ���ļ�
	//ÿ���½�һ���Ի���ʱ�����ȡ�ļ����ݵ�������
	//ÿ�ν���һ���Ի���ʱ����������ݴ浽�ļ���
	//�˳�����ʱ��ɾ���ļ�......
	public boolean initRecordFile(){
		boolean recordFileFlag = false;
        File directory = new File(".");
        String path = null;
        String path_p2p = null;
        String path_channel = null;
        try {
            path = directory.getCanonicalPath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return recordFileFlag;
        } 
        path_p2p = path+"\\"+Constant.RECORD_FILE_P2P;
        path_channel= path+"\\"+Constant.RECORD_FILE_CHANEEL;
        File file_p2p =new File(path_p2p);  
        File file_channel =new File(path_channel); 
        PrintToScreen.printToScreenLine(path);
        if(file_p2p.exists()){
        	file_p2p.delete();
        }
        if(file_channel.exists()){
        	file_channel.delete();
        }
        try {
        	file_p2p.createNewFile();
        	file_channel.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return recordFileFlag; 
		}
        recordFileFlag = true; 
        return recordFileFlag;    
	}
	//��ʼ���ź����
	public void initSignal(){
		sig = new Signal(Constant.APP_ID);
	}
	
	public boolean checkAccountName(String accountName){
		boolean returnFlag = false;
		if(accountName.contains(" ")){
			return returnFlag;
		}else if(accountName.length()<=0||accountName.length()>=128){
			return returnFlag;
		}else if(accountName.equals(currentUser)){
			PrintToScreen.printToScreenLine("...the same as the logining account...");
			return returnFlag;		
		}
		returnFlag = true;
		return returnFlag;
	}
	
    public void login(String accountName){
		//PrintToScreen.printToScreenLine("Hello " + accountName + ".");
        //sig.setDoLog(true);
        final CountDownLatch loginLatch = new CountDownLatch(1);
        sig.login(accountName, this.token, new Signal.LoginCallback() {
            @Override
            public void onLoginSuccess(final Signal.LoginSession session, int uid) {
            	if( timeOutFlag==false){
                	currentUser = accountName;
                	User user = new User(session, accountName, uid);
                	user.setLoginLatch(loginLatch);
                    users.put(currentUser,new User(session, accountName, uid));
                    PrintToScreen.printToScreenLine("account:"+users.get(accountName).getAccount()+" login successd"); 
                    currentStatus = DialogueStatus.LOGINED;
                	user.getLoginLatch().countDown();        		
            	}
            }

            @Override
            public void onLogout(Signal.LoginSession session, int ecode) {
            	if(currentStatus == DialogueStatus.LOGINED&&timeOutFlag == false){
                    PrintToScreen.printToScreenLine("account:"+users.get(accountName).getAccount()+" logout successd");    
                    //������еļ�¼
                    if(accountDialogueRecords!=null){
                        accountDialogueRecords.clear();               	
                    }
                    if(currentChannelDialogueRecords!=null){
                    	currentChannelDialogueRecords.clear();
                    }
                    currentStatus = DialogueStatus.UNLOGIN;
                    users.get(currentUser).getLogoutLatch().countDown();  	
                    currentUser = null;
            	}
            }
            
            @Override
            public void onMessageInstantReceive(Signal.LoginSession session, String account, int uid, String msg) {
            	//PrintToScreen.printToScreenLine(currentUser+" msg received :"+msg);
            	if(currentAccountDialogueRecords!=null&&currentStatus == DialogueStatus.SINGLE_POINT){
                	PrintToScreen.printToScreenLine(account+":"+msg);
                	DialogueRecord dialogueRecord= new DialogueRecord(account, msg, new Date());
                	currentAccountDialogueRecords.add(dialogueRecord);
            	}
            }
        });
    	this.timeOutFlag = false;
    	//����login��time
        wait_time(loginLatch, Constant.TIMEOUT,accountName);
    }
    
    
    public void wait_time(CountDownLatch x, int tInMS,String accountName){
        try {
            x.await(tInMS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            currentStatus = DialogueStatus.UNLOGIN;     
        }
        if(x.getCount()==1){
        	this.timeOutFlag = true;
        	PrintToScreen.printToScreenLine("connect time out ......");
            currentStatus = DialogueStatus.UNLOGIN; 
            if(users.get(accountName)!=null){
            	users.get(accountName).getSession().logout(); 
            }
        }
    }
}
