
#include "action.h"
#include "AGV.h"
#include "arm.h"
#include "port.h"
#include "wifi.h"
#include "RGB.h"
/*
 * 串口0用于打印测试数据，波特率115200
 * 串口2连接机械臂UART2，波特率115200
 * 串口3连接蓝牙，波特率115200
 */
void setup() {
  Serial.begin(115200);
  rgb_init();
  Motor_init();
  tim5_begin();
  arm_init();
  Serial.println("end");
  
  while(!wifi_STA_init()) delay(2000); //wifi STA初始化
  Serial.println("***STA setup end***");
  wifi_ConnectToSerive();  //wifi 客户端初始化
}
void loop() {
  refresh_USB();
  refresh_WIFI();
}
