//package com.thinking.machines.tcp.common.event;
//import com.thinking.machines.tcp.common.pojo.*;
public interface TCPListener
{
public byte[] onData(Client client,byte bytes[]);
public void onOpen(Client client);
public void onClose(Client client);
}