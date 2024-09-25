//package com.thinking.machines.tcp.server;
import java.io.*;
//import com.thinking.machines.tcp.common.event.*;
//import com.thinking.machines.tcp.common.pojo.*;
//import com.thinking.machines.tcp.common.*;
import java.net.*;
import java.util.*;
//import com.thinking.machines.common.pojo.*;
public class TCPServer implements ProcessListener
{
private ServerSocket incomingRequestServerSocket;
private ServerSocket outgoingRequestServerSocket;
private int incomingRequestPortNumber;
private int outgoingRequestPortNumber;
private TCPListener tcpListener;
private Map<String,Pair<IncomingRequestProcessor,OutgoingRequestProcessor>> connections=new HashMap<>();
public TCPServer(int incomingRequestPortNumber,int outgoingRequestPortNumber)
{
this.incomingRequestPortNumber=incomingRequestPortNumber;
this.outgoingRequestPortNumber=outgoingRequestPortNumber;
this.incomingRequestServerSocket=null;
this.outgoingRequestServerSocket=null;
}
public void start(TCPListener tcpListener)  throws IOException
{
this.tcpListener=tcpListener;
this.incomingRequestServerSocket=new ServerSocket(this.incomingRequestPortNumber);
this.outgoingRequestServerSocket=new ServerSocket(this.outgoingRequestPortNumber);
this.startAccepting();
}
private void startAccepting() throws IOException
{
Thread incomingRequestHandlerThread=new Thread(){
public void run()
{
System.out.println("Incoming requests will be handled on : "+incomingRequestPortNumber);
try
{
while(true)
{
Socket socket=incomingRequestServerSocket.accept();
Thread t=new Thread(){
public void run()
{
OutputStream os;
InputStream is;
byte ack[]=new byte[1];

byte header[]=new byte[10];
int i,j;
IncomingRequestProcessor incomingRequestProcessor;
OutgoingRequestProcessor outgoingRequestProcessor=null;
Client client;
String clientId=null;
try
{
is=socket.getInputStream();
while(true)
{
if(is.read(ack)!=-1) break;
}
clientId=java.util.UUID.randomUUID().toString();
client=new Client();
client.setId(clientId);
client.setIP(socket.getRemoteSocketAddress().toString());
os=socket.getOutputStream();
incomingRequestProcessor=new IncomingRequestProcessor(socket,TCPServer.this,TCPServer.this.tcpListener,client,is,os);
connections.put(clientId,new Pair<IncomingRequestProcessor,OutgoingRequestProcessor>(incomingRequestProcessor,outgoingRequestProcessor));
int k;
os.write(clientId.getBytes());
os.flush();
while(true) 
{
if(is.read(ack)!=-1) break;
}
}catch(Exception exception)
{
exception.printStackTrace(); // remove after testing
if(clientId!=null) connections.remove(clientId);
// do nothing
}
}
};
t.start();
}
}catch(Exception exception)
{
exception.printStackTrace();
System.exit(0);
}
}
};
incomingRequestHandlerThread.start();
Thread outgoingRequestHandlerThread=new Thread(){
public void run()
{
OutgoingRequestProcessor outgoingRequestProcessor;
System.out.println("Outgoing requests will be handled from : "+outgoingRequestPortNumber);
try
{
while(true)
{
Socket socket=outgoingRequestServerSocket.accept();
Thread t=new Thread(){
public void run()
{
try
{
InputStream is=socket.getInputStream();
OutputStream os=socket.getOutputStream();
byte uuidBytes[]=new byte[1024];
int uuidSize=-1;
while(true)
{
uuidSize=is.read(uuidBytes);
if(uuidSize!=-1) break;
}
byte ack[]=new byte[1];
ack[0]=100;
os.write(ack);
os.flush();
for(int k=uuidSize;k<1024;k++) uuidBytes[k]=32;
String uuid=new String(uuidBytes).trim();
Pair<IncomingRequestProcessor,OutgoingRequestProcessor> pair;
pair=connections.get(uuid);
if(pair==null)
{
socket.close();
return;
}
IncomingRequestProcessor irp=pair.getFirst();
OutgoingRequestProcessor orp=new OutgoingRequestProcessor(socket,TCPServer.this,irp.getClient(),is,os);
pair.setSecond(orp);
irp.start();
orp.start();
tcpListener.onOpen(irp.getClient());
}catch(Exception exception)
{
exception.printStackTrace();
// something important is missing over here
}
}
};
t.start();
}
}catch(Exception e)
{
e.printStackTrace();
System.exit(0);
}
}
};
outgoingRequestHandlerThread.start();
}
public void onCompleted(Client client)
{
// need to remove from map<String,Pair>
// need to notify the TCPListener [The Application]
}
public void send(Client client,byte bytes[],ResponseListener responseListener)
{
Pair<IncomingRequestProcessor,OutgoingRequestProcessor> pair;
pair=connections.get(client.getId());
if(pair==null)
{
responseListener.onError("Connection closed");
return;
}
OutgoingRequestProcessor orp=pair.getSecond();
orp.add(bytes,responseListener);
}
}