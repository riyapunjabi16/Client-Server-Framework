package com.thinking.machines.tcp.error;
public class TCPNetworkError
{
String error;

public TCPNetworkError(String error)
{
this.error=error;
}
public String getError()
{
return this.error;
}
}