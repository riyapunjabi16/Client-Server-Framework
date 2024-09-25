package com.thinking.machines.tcp.common.event;
public interface ResponseListener 
{
public void onResponse(byte bytes[]);
public void onError(String error);
}