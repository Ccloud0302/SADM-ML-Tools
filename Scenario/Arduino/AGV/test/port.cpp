#include "port.h"

//转字符串
String toString(uint8_t *buffer, uint8_t buffer_size){
  String str = "\0";
  for (uint32_t i=0; i<buffer_size; i++) str+=(char)buffer[i];
  return str;
}
//处理接收到的数据 端口，数据
void data_deal(uint8_t _port,uint8_t *_buffer,uint8_t _len){
  String data = toString(_buffer, _len);
  if (data.endsWith("\r\n")) {
    Serial.print(data);
    if(data.startsWith("car:")){
      String order;
      uint16_t distance=100;
      uint8_t speed = 13;
      int _addr1,_addr2,_addr3,_addr4;

      _addr1 = 4;
      _addr2 = data.indexOf(',');
      _addr3 = data.indexOf(',',_addr2+1);
      _addr4 = data.indexOf("\r\n");
      order = data.substring(_addr1, _addr2);  // 获取命令
      if (_addr3 == (-1)){
        distance = data.substring(_addr2+1, _addr4).toInt();  // 获取距离
      }
      else{
        distance = data.substring(_addr2+1, _addr3).toInt();  // 获取距离
        speed = data.substring(_addr3+1, _addr4).toInt();  // 获取速度
      }
      
      Car_do(order,distance);
    }
    else if(data.startsWith("car1PosC1")){
			move_car(car1PosC1);
    }
    else if(data.startsWith("car1Pos3")){
			move_car(car1Pos3);
    }
    else if(data.startsWith("car1Pos4")){
			move_car(car1Pos4);
    }
    else if(data.startsWith("car1Pos5")){
			move_car(car1Pos5);
    }
    else if(data.startsWith("car1PosD1")){
			move_car(car1PosD1);
    }
    else if(data.startsWith("car1PosD2")){
			move_car(car1PosD2);
    }
    else if(data.startsWith("car2PosB1")){
			move_car(car2PosB1);
    }
    else if(data.startsWith("car2PosB2")){
			move_car(car2PosB2);
    }
    else if(data.startsWith("arm1Pos0")){
			move_arm(arm1Pos0);
    }
    else if(data.startsWith("arm1PosC1")){
			move_arm(arm1PosC1);
    }
    else if(data.startsWith("arm1PosD1")){
			move_arm(arm1PosD1);
    }
    else if(data.startsWith("arm1Pos2")){
			move_arm(arm1Pos2);
    }
    else if(data.startsWith("arm1Pos3")){
			move_arm(arm1Pos3);
    }
    else if(data.startsWith("arm2Pos0")){
			move_arm(arm2Pos0);
    }
    else if(data.startsWith("arm2Pos1")){
			move_arm(arm2Pos1);
    }
    else if(data.startsWith("arm2Pos2")){
			move_arm(arm2Pos2);
    }
    else if(data.startsWith("arm2Pos3")){
			move_arm(arm2Pos3);
    }
    else if(data.startsWith("C")){
      wifi_client_send("ok");
      uint8_t fool = data[1]-'0';
      move_arm(arm1Pos0);
      move_car(car1PosC1);
      move_arm(arm1PosC1);  // arm
      delay(6000);
      move_car(car1Pos3);
      move_arm(arm1Pos2);  // arm
      move_car(car1Pos4);
      arm_error_yz(110,-83*(fool-1));
      move_arm(arm1Pos3);  // arm
      arm_error_yz(0,0);
      delay(5000);
      move_car(car1Pos5);
      move_arm(arm1Pos0);
    }
    else if(data.startsWith("D")){
      wifi_client_send("ok");
      uint8_t fool = data[1]-'0';
      move_arm(arm1Pos0);
      move_car(car1PosD1);
      move_arm(arm1PosD1);  // arm
      delay(6000);
      move_car(car1PosD2);
      move_car(car1Pos3);
      move_arm(arm1Pos2);  // arm
      move_car(car1Pos4);
      arm_error_yz(-107,-83*(fool-1));
      move_arm(arm1Pos3);  // arm
      arm_error_yz(0,0);
      delay(5000);
      move_car(car1Pos5);
      move_arm(arm1Pos0);
    }
    else if(data.startsWith("B")){
      wifi_client_send("ok");
//      move_arm(arm2Pos0);
      move_car(car2PosB1);
//      move_arm(arm2Pos1);  // arm
//      delay(2000);
//      move_car(car2PosB2);
//      move_arm(arm2Pos2);  // arm
//      delay(2000);
//      move_car(car2PosB1);
//      move_arm(arm2Pos3);  // arm
//      delay(2000);
//      move_car(car2PosB2);
//      move_arm(arm2Pos0);
    }
    else if(data.startsWith("H")){
      wifi_client_send("ok");
      arm_homing(); //机械臂快速归零
    }
    else if(data.startsWith("test")){
      wifi_client_send("ok");
    }
    else if(data.startsWith("rgb")){
      wifi_client_send("ok");
      setColor(0,0,50);
      delay(1000);
    }


  }
}
//串口接收
void refresh_USB() {
  uint8_t _buffer[256];
  uint8_t _len;
  if(Serial.available()){
	  //Serial.setTimeout(10);
	  _len = Serial.readBytes(_buffer, sizeof(_buffer));
	  if (_len) {
		data_deal(USB, _buffer, _len);
	  }
  }
}
//WiFi接收
void refresh_WIFI() {  
  uint8_t _buffer[256];
  uint8_t _len;
  _len = wifi_receive(_buffer, sizeof(_buffer), 100);//接收数据，并获取长度，超时时间
  if (_len) {
    data_deal(WIFI, _buffer, _len);
  }

  static uint32_t _time;
  if(millis()-_time>500){
    _time = millis();
    if(wifi_ConnectToSerive()) setColor(0,50,0);
    else setColor(50,0,0);
  }
}
