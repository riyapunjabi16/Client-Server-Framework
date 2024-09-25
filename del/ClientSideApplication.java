//import com.thinking.machines.tcp.client.*;
//import com.thinking.machines.tcp.common.*;
//import com.thinking.machines.tcp.common.pojo.*;
//import com.thinking.machines.tcp.common.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class ClientSideApplication extends JFrame implements TCPListener
{
private JTextField t1;
private JLabel l1;
private JButton b1;
private JLabel statusLabel;
private TCPClient tcpClient;
ClientSideApplication()
{
t1=new JTextField(50);
l1=new JLabel("                                                        ");
b1=new JButton("Click me");
statusLabel=new JLabel("                                               ");
Container c=getContentPane();
c.setLayout(new FlowLayout());
c.add(t1);
c.add(l1);
c.add(b1);
c.add(statusLabel);
b1.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
tcpClient.send(t1.getText().getBytes(),new ResponseListener(){
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
try
{
tcpClient=new TCPClient(5000,"localhost",6000,"localhost",this);
}catch(Exception exception)
{
exception.printStackTrace();
System.exit(0);
}
setLocation(10,10);
setSize(800,200);
}
public static void main(String gg[]) throws IOException
{
ClientSideApplication t=new ClientSideApplication();
}
public byte[] onData(Client client,byte data[])
{
statusLabel.setText("request received from server");
String request=new String(data);
l1.setText(request);
return "UJJAIN".getBytes();
}
public void onOpen(Client client)
{
setVisible(true);
statusLabel.setText("connection opened");
setTitle(client.getId());
}
public void onClose(Client client)
{
statusLabel.setText("Connection closed");
}
}