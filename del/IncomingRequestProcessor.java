//package com.thinking.machines.tcp.common;
//import com.thinking.machines.tcp.common.event.*;
//import com.thinking.machines.tcp.common.pojo.*;
import java.net.*;
import java.io.*;
public class IncomingRequestProcessor implements Runnable
{
private Client client;
private ProcessListener processListener;
private Socket socket;
private Thread thread;
private TCPListener tcpListener;
private InputStream inputStream;
private OutputStream outputStream;
public IncomingRequestProcessor(Socket socket,ProcessListener processListener,TCPListener tcpListener,Client client,InputStream inputStream,OutputStream outputStream)
{
this.socket=socket;
this.processListener=processListener;
this.tcpListener=tcpListener;
this.client=client;
this.inputStream=inputStream;
this.outputStream=outputStream;
}
public Client getClient()
{
return this.client;
}
public void start()
{
thread=new Thread(this);
thread.start();
}
public void run()
{
ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
byte header[]=new byte[10];
int e,f,lengthOfRequestBytes,byteCount,bytesRead,bytesSent,lengthOfResponseBytes,numberOfBytesToWrite,bufferSize;
byte requestBytes[],responseBytes[],ack[];
byte temp[]=new byte[1024];
bufferSize=1024;
ack=new byte[1];
try
{
while(true)
{
// read bytes[] that contains length of request bytes
if(inputStream.read(header)==-1) continue;

lengthOfRequestBytes=0;
f=1;
e=9;
while(e>=0)
{
lengthOfRequestBytes=lengthOfRequestBytes+(f*header[e]);
f=f*10;
e--;
}
ack[0]=1;
outputStream.write(ack,0,ack.length);
outputStream.flush();
// read request bytes
byteCount=0;
bytesRead=0;
while(true)
{
byteCount=inputStream.read(temp);
if(byteCount==-1) continue;
bytesRead+=byteCount;
byteArrayOutputStream.write(temp,0,byteCount);
ack[0]=1;
outputStream.write(ack,0,ack.length);
outputStream.flush();
if(bytesRead==lengthOfRequestBytes) break;
}
requestBytes=byteArrayOutputStream.toByteArray();
// invoke onBytesReceived through tcpListener and send responseBytes 
responseBytes=this.tcpListener.onData(client,requestBytes);
//send header that contains length of reponseBytes to be sent
lengthOfResponseBytes=responseBytes.length;
e=9;
f=lengthOfResponseBytes;
while(e>=0)
{
header[e]=(byte)(f%10);
f=f/10;
e--;
}
outputStream.write(header,0,10);
outputStream.flush();
while(true)
{
if(inputStream.read(ack)!=-1) break;
}
// send the responseBytes
bytesSent=0;
ack[0]=0;
while(bytesSent<lengthOfResponseBytes)
{
numberOfBytesToWrite=bufferSize;
if(bytesSent+bufferSize>lengthOfResponseBytes)
{
numberOfBytesToWrite=lengthOfResponseBytes-bytesSent;
}
outputStream.write(responseBytes,bytesSent,numberOfBytesToWrite);
outputStream.flush();
while(true)
{
if(inputStream.read(ack)!=-1) break;
}
bytesSent+=bufferSize;
}
} // infinite loop ends
}catch(Exception exception)
{
System.out.println(exception); // serious problem
}
processListener.onCompleted(client);
}
}