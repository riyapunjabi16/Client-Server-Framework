//import com.thinking.machines.tcp.server.*;
//import com.thinking.machines.tcp.common.pojo.*;
//import com.thinking.machines.tcp.common.event.*;
//import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
class ServerSideApplication implements TCPListener
{
TCPServer tcpServer;
ServerSideApplication()
{
connect();
}
private void connect()
{
tcpServer=new TCPServer(5000,6000);
try
{
tcpServer.start(this);
}catch(Exception exception)
{
exception.printStackTrace();
System.exit(0);
}
}
public byte[] onData(Client client,byte data[])
{
System.out.println("request received from : "+client.getIP());
String request=new String(data);
System.out.println(request);
return "Indore".getBytes();
}
public void onOpen(Client client)
{
System.out.println("onOpen got called because of : "+client.getIP()+","+client.getId());

JFrame frame=new JFrame();
JTextField t1;
JLabel l1;
JButton b1;
JLabel statusLabel;
t1=new JTextField(50);
l1=new JLabel("                                                        ");
b1=new JButton("Click me");
statusLabel=new JLabel("                                               ");
Container c=frame.getContentPane();
c.setLayout(new FlowLayout());
c.add(t1);
c.add(l1);
c.add(b1);
c.add(statusLabel);
b1.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
tcpServer.send(client,t1.getText().getBytes(),new ResponseListener(){
public void onError(String error)
{
statusLabel.setText(error);
}
public void onResponse(byte response[])
{
String res=new String(response);
l1.setText(res);
}
});
}
});
frame.setLocation(400,10);
frame.setSize(200,400);
frame.setTitle("Server side : "+client.getId());
frame.setVisible(true);
}
public void onClose(Client client)
{
System.out.println("onClose got called because of : "+client.getIP()+","+client.getId());
}

public static void main(String gg[]) throws IOException
{
ServerSideApplication ssa=new ServerSideApplication();
}
}