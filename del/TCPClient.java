//package com.thinking.machines.tcp.client;
import java.net.*;
import java.io.*;
//import com.thinking.machines.common.pojo.*;
//import com.thinking.machines.tcp.common.*;
//import com.thinking.machines.tcp.common.pojo.*;
//import com.thinking.machines.tcp.common.event.*;
public class TCPClient  implements ProcessListener
{
private int portNumberForSendingRequest;
private int portNumberForReceivingRequest;
private String ipAddressForSendingRequest;
private String ipAddressForReceivingRequest;
private Socket socketForSendingRequest;
private Socket socketForReceivingRequest;
private TCPListener tcpListener;
private IncomingRequestProcessor incomingRequestProcessor;
private OutgoingRequestProcessor outgoingRequestProcessor;
public TCPClient(int portNumberForSendingRequest,String ipAddressForSendingRequest,int portNumberForReceivingRequest,String ipAddressForReceivingRequest,TCPListener tcpListener) throws IOException
{
this.portNumberForSendingRequest=portNumberForSendingRequest;
this.ipAddressForSendingRequest=ipAddressForSendingRequest;
this.portNumberForReceivingRequest=portNumberForReceivingRequest;
this.ipAddressForReceivingRequest=ipAddressForReceivingRequest;
this.tcpListener=tcpListener;
setupConnection();
}
private void setupConnection()
{
try
{
Thread t=new Thread(){
public void run()
{
try
{
socketForSendingRequest=new Socket(InetAddress.getByName(ipAddressForSendingRequest),portNumberForSendingRequest);
socketForReceivingRequest=new Socket(InetAddress.getByName(ipAddressForReceivingRequest),portNumberForReceivingRequest);
InputStream isForSendingRequest=socketForSendingRequest.getInputStream();
OutputStream osForSendingRequest=socketForSendingRequest.getOutputStream();
byte ack[]=new byte[1];
ack[0]=100;
osForSendingRequest.write(ack);
osForSendingRequest.flush();
byte uuidBytes[]=new byte[1024];
int uuidSize=-1;
while(true)
{
uuidSize=isForSendingRequest.read(uuidBytes);
if(uuidSize!=-1) break;
}
osForSendingRequest.write(ack);
osForSendingRequest.flush();
for(int k=uuidSize;k<1024;k++) uuidBytes[k]=32;
String uuid=new String(uuidBytes).trim();
InputStream isForReceivingRequest=socketForReceivingRequest.getInputStream();
OutputStream osForReceivingRequest=socketForReceivingRequest.getOutputStream();
osForReceivingRequest.write(uuid.getBytes());
osForReceivingRequest.flush();
while(true)
{
if(isForReceivingRequest.read(ack)!=-1) break;
}
Client client=new Client();
client.setId(uuid);
incomingRequestProcessor=new IncomingRequestProcessor(socketForReceivingRequest,TCPClient.this,tcpListener,client,isForReceivingRequest,osForReceivingRequest);
outgoingRequestProcessor=new OutgoingRequestProcessor(socketForSendingRequest,TCPClient.this,client,isForSendingRequest,osForSendingRequest);
incomingRequestProcessor.start();
outgoingRequestProcessor.start();
tcpListener.onOpen(client);
}catch(Exception exception)
{
exception.printStackTrace(); // something else will be required over here
System.exit(0);
}
}
};
t.start();
}catch(Exception exception)
{
exception.printStackTrace(); 
System.exit(0);
}
}
public void onCompleted(Client client)
{
// don't know what to do
}
public void send(byte bytes[],ResponseListener responseListener)
{
System.out.println("Adding : "+new String(bytes)+" to outgoing queue");
outgoingRequestProcessor.add(bytes,responseListener);
System.out.println("Added : "+new String(bytes)+" to outgoing queue");
}
}