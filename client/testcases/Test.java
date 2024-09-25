import com.thinking.machines.tcp.client.*;
import com.thinking.machines.tcp.common.pojo.*;
import com.thinking.machines.tcp.common.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
class testFrame extends JFrame implements TCPListener 
{
private JTextField testTextField;
private JButton testButton;
private TCPClient tcpClient;
private ResponseListener responseListener;
testFrame()
{
testTextField=new JTextField("            ");
testButton=new JButton("Click me");
setLayout(new FlowLayout());
try{
tcpClient=new TCPClient(5000,"localhost",5001,"localhost",this);
}catch(Exception e)
{
}
add(testTextField);
add(testButton);
addListeners();
setLocation(10,10);
setSize(400,400);
setVisible(true);
}
public void onClose(Client client)
{
}
public byte[] onData(Client client,byte bytes[])
{
System.out.println("on data of client bytes recieved"+bytes);
return "hi".getBytes();
}
public void onOpen(Client client)
{
System.out.println("on open client");
}
public void addListeners()
{
testButton.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
String text=testTextField.getText();
responseListener=new ResponseListener(){
public void onResponse(byte bytes[])
{
System.out.println("on response chala");
testTextField.setText(new String(bytes));
}
public void onError(String s)
{
System.out.println(s);
}
};
tcpClient.send(text.getBytes(),responseListener);
}
}
);
}
}
class psp
{
public static void main(String gg[])
{
testFrame t=new testFrame();
}
}