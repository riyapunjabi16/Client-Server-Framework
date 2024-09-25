package com.thinking.machines.tcp.common.pojo;
public class Client
{
private String ip;
private String _id;

public void setId(String _id)
{
this._id=_id;
}
public void setIP(String ip)
{
this.ip=ip;
}
public String getIP()
{
return this.ip;
}
public String getId()
{
return this._id;
}
} 