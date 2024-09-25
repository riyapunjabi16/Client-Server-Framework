import com.thinking.machines.tcp.server.*;
import com.thinking.machines.tcp.common.pojo.*;
import com.thinking.machines.tcp.common.event.*;
import java.util.*;
import java.io.*;
class TestTCPHandler implements TCPListener
{
public byte[] onData(Client client,byte bytes[])
{
System.out.println("request received from : "+client.getIP());
String request=new String(bytes);
System.out.println(request);
return "UJJAI".getBytes();
}
public void onOpen(Client client)
{
System.out.println("Connection opened for id :"+client.getId());
}
public void onClose(Client client)
{
System.out.println("Connection closed  for id :"+client.getId());
}
} class Test
{
public static void main(String gg[]) throws IOException
{
TCPServer tcpServer=new TCPServer(5000,5001);
tcpServer.start(new TestTCPHandler());
}}