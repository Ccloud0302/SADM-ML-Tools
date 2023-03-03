#ifndef __wifi_H__
#define __wifi_H__

#include "Arduino.h"

#include "ESP8266.h"

//通用
#define SSID        "QN"     //热点名称
#define PASSWORD    "12345678"  //热点密码

//服务器IP及端口
#define HOST_NAME   "192.168.137.1"
#define HOST_PORT   (8080)
//本机IP及端口
//#define GUEST_NAME  "192.168.137.2"    //AGV1
#define GUEST_NAME  "192.168.137.3"    //AGV2

//已连接设备
struct wifiDrive {
  String linkID;      //网络连接ID（0~4），用于多连接情况
  String type;        //协议
  String remoteIP;    //远端IP地址
  String remotePort;  //远端端口值
  String localPort;   //本地端口值
  String role;        //角色状态
};

bool wifi_STA_init(); //wifi STA初始化
bool wifi_ConnectToSerive();  //wifi 客户端初始化
bool wifi_updataStatus();  //更新网络状态信息
uint32_t wifi_receive(uint8_t *buffer, uint32_t buffer_size, uint32_t timeout);  //WiFi接收
bool wifi_client_send(String _str); //给服务器发送数据

#endif
