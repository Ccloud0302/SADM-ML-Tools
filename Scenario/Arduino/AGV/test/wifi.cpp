#include "wifi.h"

ESP8266 wifi(Serial3,115200);//设置wifi所在串口

wifiDrive drive_list[5];//设备信息列表0-4
uint8_t drive_num;//设备数量
String stat;//网络状态
uint32_t time_alive;//心跳更新时间

bool wifi_STA_init() { //wifi STA初始化
  //if (!wifi.restore()) return false;  //恢复出厂设置，测试
  if (!wifi.setOprToStation()) return false;//设置STA信息
  if (!wifi.joinAP(SSID, PASSWORD)) return false;
  if (!wifi.disableMUX()) return false;//设置单个设备连接
  if (!wifi.setIPSTA(GUEST_NAME)) return false;//设置本机IP地址
  return true;
}

bool wifi_ConnectToSerive() {  //wifi 客户端初始化
  if (!wifi_updataStatus()) ;//检测状态
  else if (stat=="3") {
    return true;//已经连接到服务器
  }
  else if (stat=="2" || stat=="4") {
    uint32_t _time=millis();
    Serial.println("连接服务器中");
    wifi.createTCP(HOST_NAME, HOST_PORT);//尝试连接服务器
	wifi_updataStatus();//更新状态
	delay(1000);
    if (stat=="3") return true;
  }
  else if (stat=="5") {
    Serial.println("未连接ap");
  } 
  return false;
}

bool wifi_updataStatus() {  //更新网络状态信息
  int8_t addr_start,addr_stop;
  uint8_t i=0;
  String str1 = wifi.getIPStatus();//获取网络信息
  
  addr_start = str1.indexOf("STATUS:");
  addr_stop = str1.indexOf("\r\n",addr_start);
  if (addr_start == -1 || addr_stop == -1) return false;
  addr_start += 7;
  stat = str1.substring(addr_start, addr_stop);

  drive_num=0;
  while(1){
    addr_start = str1.indexOf("+CIPSTATUS:",addr_stop);
    addr_stop = str1.indexOf("\r\n",addr_start);
    if (addr_start == -1 || addr_stop == -1) break;
    String str2 = str1.substring(addr_start+11, addr_stop);
    str2+=",";
    String _status[6];
    int8_t addr_start1=0;
    int8_t addr_stop1=0;

    for(uint8_t j=0;j<6;j++){
      addr_stop1 = str2.indexOf(",",addr_start1);
      _status[j] = str2.substring(addr_start1, addr_stop1);
      addr_start1=addr_stop1+1;
    }
    drive_list[i].linkID=_status[0];
    drive_list[i].type=_status[1];
    drive_list[i].remoteIP=_status[2];
    drive_list[i].remotePort=_status[3];
    drive_list[i].localPort=_status[4];
    drive_list[i].role=_status[5];
    
    drive_num++;
    i++;
  }
  return true;
}
uint32_t wifi_receive(uint8_t *buffer, uint32_t buffer_size, uint32_t timeout) {  //WiFi接收
  return wifi.recv(buffer, buffer_size, timeout);
}
bool wifi_client_send(String _str) { //给服务器发送数据
  return wifi.send(_str);
}
