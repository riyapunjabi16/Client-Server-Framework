package com.thinking.machines.tcp.common;
import com.thinking.machines.common.pojo.*;
import com.thinking.machines.tcp.common.event.*;
import com.thinking.machines.tcp.common.pojo.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class OutgoingRequestProcessor implements Runnable
{
private Client client;
private ProcessListener processListener;
private Socket socket;
private Thread thread;
private InputStream inputStream;
private OutputStream outputStream;
private LinkedList<Pair<byte [],ResponseListener>> queue=new LinkedList<>();
public OutgoingRequestProcessor(Socket socket,ProcessListener processListener,Client client,InputStream inputStream,OutputStream outputStream)
{
this.socket=socket;
this.processListener=processListener;
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
public void add(byte data[],ResponseListener responseListener)
{
queue.add(new Pair<byte[],ResponseListener>(data,responseListener));
thread.resume();
}
public void run()
{
ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
byte header[]=new byte[10];
InputStream inputStream;
OutputStream outputStream;
int e,f,lengthOfDataBytes,byteCount,bytesRead,bytesSent,lengthOfResponseBytes,numberOfBytesToWrite,bufferSize;
byte responseBytes[],ack[];
byte temp[]=new byte[1024];
bufferSize=1024;
ack=new byte[1];
try
{
inputStream=socket.getInputStream();
outputStream=socket.getOutputStream();

while(true)
{
if(queue.size()==0)
{
System.out.println("Thread is being suspended");
thread.suspend();
}
if(queue.size()==0) continue;
System.out.println("got out of suspended mode and started working");
Pair<byte[],ResponseListener> pair;
pair=queue.remove(0);
byte []data=pair.getFirst();
ResponseListener responseListener=pair.getSecond();
lengthOfDataBytes=data.length;
e=9;
f=lengthOfDataBytes;
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
// send the dataBytes
bytesSent=0;
ack[0]=0;
System.out.println("Header sent");
while(bytesSent<lengthOfDataBytes)
{
numberOfBytesToWrite=bufferSize;
if(bytesSent+bufferSize>lengthOfDataBytes)
{
numberOfBytesToWrite=lengthOfDataBytes-bytesSent;
}
outputStream.write(data,bytesSent,numberOfBytesToWrite);
outputStream.flush();
while(true)
{
if(inputStream.read(ack)!=-1) break;
}
bytesSent+=bufferSize;
}
System.out.println("Data sent");
// read bytes[] that contains length of request bytes
while(true)
{
if(inputStream.read(header)!=-1) break;
}
lengthOfResponseBytes=0;
f=1;
e=9;
while(e>=0)
{
lengthOfResponseBytes=lengthOfResponseBytes+(f*header[e]);
f=f*10;
e--;
}
ack[0]=1;
outputStream.write(ack,0,ack.length);
outputStream.flush();
System.out.println("Response header received");
// read request bytes
byteCount=0;
bytesRead=0;
while(true)
{
byteCount=inputStream.read(temp);
if(byteCount==-1)continue;
bytesRead+=byteCount;
byteArrayOutputStream.write(temp,0,byteCount);
ack[0]=1;
outputStream.write(ack,0,ack.length);
outputStream.flush();
if(bytesRead==lengthOfResponseBytes) break;
}
responseBytes=byteArrayOutputStream.toByteArray();
System.out.println("Response received");
responseListener.onResponse(responseBytes);
} // infinite loop ends
}catch(Exception exception)
{
System.out.println(exception); // serious problem
}
processListener.onCompleted(client);
}
}