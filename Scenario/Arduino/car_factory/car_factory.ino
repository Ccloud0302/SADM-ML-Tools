#include "Mirobot.h"
#include "Wlkata_Shield.h"
#include "Adafruit_NeoPixel.h"
#include "ESP8266.h"


#define screenSerial Serial3  //触摸屏接口
#define RS485Serial Serial1 //RS485通信接口

#define beltTime 10000      //传送带启动后，延时等待时间

uint8_t openmvStatus = 0;
uint8_t passNum = 0;//合格品数量
uint8_t failNum = 0;//不合格品数量
char controlBuf[10];

/***********WIFI***********/
#define SSID        "QN"     //热点名称
#define PASSWORD    "12345678"  //热点密码
#define HOST_NAME   "192.168.137.1"   //IP地址
#define HOST_PORT   (8080)    //端口号
#define AGV1        "192.168.137.2"
#define AGV2        "192.168.137.3"

ESP8266 wifi(Serial2,115200);//设置wifi所在串口



/***********RS485对象***********/
RS485_Mirobot arm1;
RS485_Mirobot arm2;
RS485_Mirobot arm3;
RS485_Mirobot arm4;
RS485_Mirobot arm5;

/***********RGB对象***********/
Adafruit_NeoPixel rgb1(3, S1, NEO_GRB + NEO_KHZ800);/*一组LED内小灯个数，一组LED信号引脚，默认参数（不用修改）*/
Adafruit_NeoPixel rgb2(3, S2, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel rgb3(3, S3, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel rgb4(3, S4, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel rgb5(3, S5, NEO_GRB + NEO_KHZ800);

uint8_t yellow[3] = {255, 255, 0};
uint8_t red[3] = {255, 0, 0};
uint8_t green[3] = {0, 255, 0};
uint8_t blue[3] = {0, 0, 255};

/***********传送带控制指令***********/

String conveyorCode[5][4] = {
  {"@21S0", "@21S400", "@21S-400", "@21S400G4P1000"},   //地址21的传送带控制指令
  {"@22S0", "@22S400", "@22S-400", "@22S400G4P1000"},   //地址22的传送带控制指令
  {"@23S0", "@23S400", "@23S-400", "@23S400G4P1000"},   //地址23的传送带控制指令
  {"@24S0", "@24S400", "@24S-400", "@24S400G4P450"},   //地址24的传送带控制指令
  {"@25S0", "@25S400", "@25S-400", "@25S400G4P1000"},   //地址25的传送带控制指令
};

/***********机械臂动作文件***********/

String armFile[5][3]{
  {"arm1_A1", "arm1_A2", "arm1_A3"},
  {"arm2_X1", "arm2_X2", "arm2_X3"},
  {"arm3_weld", "", ""},
  {"arm4_weld", "", ""},
  {"arm5_polish", "", ""}
};

/***********WIFI控制函数***********/
//传输数据
struct wifiData{
  uint8_t mux_id;
  uint8_t buffer[128];
  uint8_t len;
};
//已连接设备
struct wifiDrive {
  String linkID;      //网络连接ID（0~4），用于多连接情况
  String type;        //协议
  String remoteIP;    //远端IP地址
  String remotePort;  //远端端口值
  String localPort;   //本地端口值
  String role;        //角色状态
};
wifiDrive drive_list[5];//设备信息列表0-4
uint8_t drive_num;//设备数量
String stat;//网络状态

//id转ip
String IDToIP(uint8_t id){
  //匹配ip
  for (uint8_t i=0;i<drive_num;i++) {
    if(drive_list[i].linkID==(String)id){
      return drive_list[i].remoteIP;
    }
  }
  return "\0";
}
//转字符串
String toString(uint8_t *buffer, uint32_t buffer_size){
  String str="\0";
  for(uint32_t i=0;i<buffer_size;i++) str+=(char)buffer[i];
  return str;
}
//AP初始化
void wifi_AP_init() {
  Serial.println();
  Serial.println("***AP setup begin***");
//
//  //恢复出厂设置，测试
//  if (wifi.restore()) Serial.println("find esp8266, init...");
//  else Serial.println("no device found");
//
//  //设置AP模式、配置信息
//  if (wifi.setOprToSoftAP()) Serial.println("softap ok");
//  else Serial.println("softap err");
//  if (wifi.setSoftAPParam(SSID,PASSWORD,1,3)) Serial.println("softapparam ok");
//  else Serial.println("softapparam err");
  
  //允许多个设备连接
  if (wifi.enableMUX()) Serial.println("multiple ok");
  else Serial.println("multiple err");

  if(!wifi.setIPAP(HOST_NAME)) return false;//设置本机IP地址
  
  //输出本地ip
  Serial.println("local IP："+wifi.getLocalIP());
  
  Serial.println("***AP setup end***");
}
//配置服务器
void wifi_BuildSerive() {
  //建立tcp服务器
  if (wifi.startTCPServer(HOST_PORT)) Serial.println("start tcp server ok");
  else Serial.println("start tcp server err");

  //设置TCP超时时间 秒
  if (wifi.setTCPServerTimeout(0)) Serial.println("set tcp server timeout");
  else Serial.println("set tcp server timeout err");
}
//更新网络状态信息
bool wifi_updataStatus() {
  int8_t addr_start;
  int8_t addr_stop;
  uint8_t i=0;
  String str1 = wifi.getIPStatus();//获取网络信息
  
  addr_start = str1.indexOf("STATUS:");
  addr_stop = str1.indexOf("\r\n",addr_start);
  if (addr_start == -1 || addr_stop == -1) return false;
  addr_start += 7;
  stat = str1.substring(addr_start, addr_stop);
  Serial.print("状态：");
  Serial.println(stat);
  drive_num=0;
  while(1){
    addr_start = str1.indexOf("+CIPSTATUS:",addr_stop);
    addr_stop = str1.indexOf("\r\n",addr_start);
    if (addr_start == -1 || addr_stop == -1) break;
    String str2 = str1.substring(addr_start+11, addr_stop);
    Serial.println(str2);
    
    str2+=",";
    String _status[6];
    int8_t addr_start1=0;
    int8_t addr_stop1=0;
    Serial.print("设备");
    Serial.print(i);
    Serial.print("：");
    for(uint8_t j=0;j<6;j++){
      addr_stop1 = str2.indexOf(",",addr_start1);
      _status[j] = str2.substring(addr_start1, addr_stop1);
      addr_start1=addr_stop1+1;
      Serial.print(_status[j]);
      Serial.print(",");
    }
    Serial.println();
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
//接收数据
void wifi_receive() {
  uint32_t _time  = millis();
  while(1){ 
	  wifiData data;
	  data.len = wifi.recv(&data.mux_id, data.buffer, sizeof(data.buffer), 100);//接收数据，并获取长度
	  if (data.len) {
		wifi_updataStatus();//更新
		String str=toString(data.buffer,data.len);//转字符串
		if(str=="ack"){
		  wifi.send(data.mux_id,"ack");
		  return;
		}
		
		Serial.print("设备:");
		Serial.println(IDToIP(data.mux_id));
		Serial.print("数据:");
		Serial.println(str);
		break;
	  }
	  else if(millis() - _time>20000) break;
  }
}
//给客户端发送数据
bool wifi_serive_send(String ip,String str){
  str+="\r\n";
  bool ret=false;
  ip="\""+ip+"\"";

  //检查设备数量
  if (drive_num==0) Serial.println("no drive");

  //匹配ip
  for (uint8_t i=0;i<drive_num;i++) {
    if(drive_list[i].remoteIP==ip){
      if(wifi.send(drive_list[i].linkID.toInt(), str)) ret = true;//发送数据
      Serial.println(drive_list[i].linkID);
      Serial.println(drive_list[i].remoteIP);
      break;
    }
  }

  if(ret) Serial.println("send back ok");
  else Serial.println("send back err");
  return ret;
}

/***********传送带控制函数***********/
void conveyorReset(){
  for(int i=0;i<5;i++){
	  RS485Serial.println(conveyorCode[i][0]);
	  delay(200);
  }
  Serial.println("conveyorResetok");
}



/***********中断函数***********/
char hexEND[3] = {0xff,0xff,0xff};
void(* resetFunc) (void) = 0; //制造重启命令

void RESET(){
    screenSerial.print("t1.txt=\"rest\"");
    screenSerial.write(hexEND,3);
    resetFunc();
}

void STOP(){
  RS485Serial.println("@01%");
  RS485Serial.println("@02%");
  RS485Serial.println("@03%");
  RS485Serial.println("@04%");
  RS485Serial.println("@05%");
}

/***********串口屏通信函数***********/
String screenTX(){
  //while(screenSerial.available()==0){};
  String comdata = "";
  if(screenSerial.available()>0){
  comdata = "";
  comdata = screenSerial.readString();
  comdata.trim();
  delay(5);
  Serial.println(comdata);
  while(screenSerial.read()>=0){}
  }
  return comdata;
}

void screenRX(String command){
  screenSerial.print(command);
  screenSerial.write(hexEND,3);
  Serial.println(command);
}

/***********RGB控制函数***********/
void rgbInit(){
  rgb1.begin();
  rgb2.begin();
  rgb3.begin();
  rgb4.begin();
  rgb5.begin();
}

void rgbSet(Adafruit_NeoPixel &rgb,uint8_t r,uint8_t g, uint8_t b){
  for(int i=0;i<3;i++){
    rgb.setPixelColor(i, rgb.Color(r, g, b)); 
  }
  rgb.show();
}

void rgbOff(Adafruit_NeoPixel &rgb){ 
  rgb.clear();
  rgb.show();
}
 
void rgbAllOff(){
  rgbOff(rgb1);
  rgbOff(rgb2);
  rgbOff(rgb3);
  rgbOff(rgb4);
  rgbOff(rgb5);
}
 
void rgbAllSet(uint8_t r,uint8_t g, uint8_t b){
  for(int i=0;i<3;i++) {rgb1.setPixelColor(i, rgb1.Color(r,g,b));}rgb1.show();
  for(int i=0;i<3;i++) {rgb2.setPixelColor(i, rgb1.Color(r,g,b));}rgb2.show();
  for(int i=0;i<3;i++) {rgb3.setPixelColor(i, rgb1.Color(r,g,b));}rgb3.show();
  for(int i=0;i<3;i++) {rgb4.setPixelColor(i, rgb1.Color(r,g,b));}rgb4.show();
  for(int i=0;i<3;i++) {rgb5.setPixelColor(i, rgb1.Color(r,g,b));}rgb5.show();
}

uint8_t ri = 0;
int8_t rj ;
uint8_t rgbMode = 0;
uint32_t previousMillis = 0;   //存储上次更新RGB的时间
uint32_t currentMillis;  //单片机运行以来经过的毫秒数

void rgbAllWater(uint8_t r,uint8_t g,uint8_t b, int wait){
  currentMillis = millis();
  if(ri > 2) ri = 0;
  rj = ri - 1;
  if(rj == -1) rj = 2;
  if(currentMillis - previousMillis >= wait){
	previousMillis = currentMillis;
	rgb1.clear();rgb1.setPixelColor(ri,r/10,g/10,b/10);rgb1.setPixelColor(rj,r,g,b);rgb1.show();
	rgb2.clear();rgb2.setPixelColor(ri,r/10,g/10,b/10);rgb2.setPixelColor(rj,r,g,b);rgb2.show();
	rgb3.clear();rgb3.setPixelColor(ri,r/10,g/10,b/10);rgb3.setPixelColor(rj,r,g,b);rgb3.show();
	rgb4.clear();rgb4.setPixelColor(ri,r/10,g/10,b/10);rgb4.setPixelColor(rj,r,g,b);rgb4.show();
	rgb5.clear();rgb5.setPixelColor(ri,r/10,g/10,b/10);rgb5.setPixelColor(rj,r,g,b);rgb5.show();
	ri ++;
  }
  //Serial.println(currentMillis);
}
/***********机械臂初始化***********/
void armInit(){
  rgbAllSet(red[0], red[1], red[2]); //所有灯红色
  
  
  uint8_t armStatus = 0;
  while(armStatus != Alarm && armStatus != Idle) armStatus = arm1.getStatus(); arm1.sendMsg("$H",OFF); rgbSet(rgb1, blue[0], blue[1], blue[2]); armStatus = 0; delay(500); //arm1归零，关闭机械臂返回信息的校验
  while(armStatus != Alarm && armStatus != Idle) armStatus = arm2.getStatus(); arm2.sendMsg("$H",OFF); rgbSet(rgb2, blue[0], blue[1], blue[2]); armStatus = 0; delay(500);
  while(armStatus != Alarm && armStatus != Idle) armStatus = arm3.getStatus(); arm3.sendMsg("$H",OFF); rgbSet(rgb3, blue[0], blue[1], blue[2]); armStatus = 0; delay(500);
  while(armStatus != Alarm && armStatus != Idle) armStatus = arm4.getStatus(); arm4.sendMsg("$H",OFF); rgbSet(rgb4, blue[0], blue[1], blue[2]); armStatus = 0; delay(500);
  while(armStatus != Alarm && armStatus != Idle) armStatus = arm5.getStatus(); arm5.sendMsg("$H",OFF); rgbSet(rgb5, blue[0], blue[1], blue[2]); armStatus = 0; delay(500);
  while(arm5.getStatus() != Idle){};
  arm1.setMoveSpeed(3000); //设置机械臂最大运动速度3000
  arm2.setMoveSpeed(3000);
  arm3.setMoveSpeed(3000);
  arm4.setMoveSpeed(3000);
  arm5.setMoveSpeed(3000);
  rgbAllSet(green[0], green[1], green[2]); //所有灯绿色
}

/***********初始化程序***********/

void setup() {
  Serial.begin(9600); //监视串口
  screenSerial.begin(9600);
  screenSerial.print("t1.txt=\"rest\"");
  screenSerial.write(hexEND,3);

  for(int i=0;i<3;i++){
    shieldBuzzer(ON);
    delay(200);
    shieldBuzzer(OFF);
    delay(200);
  }
  
  arm1.rs485Init(1,38400);  //配置地址
  arm2.rs485Init(2,38400);
  arm3.rs485Init(3,38400);
  arm4.rs485Init(4,38400);
  arm5.rs485Init(5,38400);
  arm1.setSerialMonitoring(ON); //设置开启串口监视，通过Arduino串口监视器打印信息
  wifi_AP_init();//设置AP信息，如果模块已经完成配置，请屏蔽该行
  wifi_BuildSerive();//配置服务器
  screenRX("t1.txt=\"rest\"");
  rgbInit();
  rgbAllOff();
  conveyorReset();
//  delay(10000);
//  for(int i=0;i<10;i++){
//    wifi_updataStatus();//检测设备
//    if(wifi_serive_send(AGV1,"C1")) Serial.println("发送成功");//发数据
//    else Serial.println("发送失败");
//    delay(5000);
//  }
}

/***********主程序***********/
void loop() {
  String screenCmd = "";
  screenCmd = screenTX();
  
  if(screenCmd.equals("open")) openmvStatus = 1;
  if(screenCmd.equals("close")) openmvStatus = 0;
  if(screenCmd.equals("auto")) AUTO();
  if(screenCmd.equals("control")) CONTROL();
  rgbAllWater(blue[0], blue[1], blue[2], 100);
}

/***********自动组装汽车函数***********/
void AUTO(){
  armInit();
  int finish = 0;
  int finishSolding = 0;

  passNum = 0;//合格品数量
  failNum = 0;//不合格品数量
  screenRX("t1.txt=\"over\"");  //复位完成，向屏幕发送数据
  while(screenSerial.available()==0){
    String screenCmd1 = "";
    screenCmd1 = screenTX();
    Serial.println(screenCmd1);
  };  //等待串口屏指令

  for(int i=0;i<3;i++){
    
    rgbAllOff();
    //搬运车架
    rgbSet(rgb1, yellow[0], yellow[1], yellow[2]);
    arm1.runFile(armFile[0][i]);   //01机械臂搬运车架
    rgbSet(rgb1, green[0], green[1], green[2]);
    screenRX("panel1.t0.txt=\"finish1\"");
    RS485Serial.println(conveyorCode[0][1]);  //启动21传送带
    delay(1000);
    RS485Serial.println(conveyorCode[1][3]);//启动22传送带
    delay(beltTime);
    
    //装配顶盖
    if(finish == 1){
      RS485Serial.println(conveyorCode[2][0]);  //停止23传送带
      delay(500);
      RS485Serial.println(conveyorCode[3][0]);  //停止24传送带
      delay(500);
      RS485Serial.println(conveyorCode[0][0]);  //停止21传送带
      //对焊接失败的打磨
      arm5.runFile(armFile[4][0]);   //05机械臂打磨
      passNum ++;
      String agvCmd = "D" + String(passNum);
      Serial.println(agvCmd);
      wifi_updataStatus();//检测设备
    
      if(wifi_serive_send(AGV1,agvCmd)) Serial.println("发送成功");//发数据
      else Serial.println("发送失败");
  
      wifi_receive();
    }
    screenRX("panel1.t0.txt=\"start2\"");
    rgbSet(rgb2, yellow[0], yellow[1], yellow[2]);
	  delay(500);
    arm2.runFile(armFile[1][i]);   //02机械臂装配顶盖
    rgbSet(rgb2, green[0], green[1], green[2]);
    screenRX("panel1.t0.txt=\"finish2\"");
    RS485Serial.println(conveyorCode[1][1]);  //启动22传送带
    delay(1000);
    RS485Serial.println(conveyorCode[2][3]);//启动23传送带
    delay(beltTime);
    
    //模拟焊接
    RS485Serial.println(conveyorCode[0][0]);  //停止21传送带
    delay(500);   
    RS485Serial.println(conveyorCode[1][0]);  //停止22传送带
    screenRX("panel1.t0.txt=\"start3\"");
    rgbSet(rgb3, yellow[0], yellow[1], yellow[2]);
	  delay(1000);
    arm3.sendMsg("o111arm3_weld",OFF); 	//03机械臂模拟焊接
	  delay(1500);
    arm4.runFile(armFile[3][0]);		//04机械臂模拟焊接
    rgbSet(rgb3, green[0], green[1], green[2]);
    screenRX("panel1.t0.txt=\"finish3\"");
    RS485Serial.println(conveyorCode[2][1]);  //启动23传送带
    delay(1000);
    RS485Serial.println(conveyorCode[3][3]);//启动24传送带
    delay(beltTime);
    
    //视觉检测
    RS485Serial.println(conveyorCode[2][0]);  //停止23传送带
    rgbSet(rgb4, yellow[0], yellow[1], yellow[2]);
    screenRX("panel1.t0.txt=\"start4\"");
    finish = 0;
    finishSolding = 0;
    delay(1000);
    if(openmvStatus == 1){
      while(RS485Serial.read()>=0){};
  	  RS485Serial.println("@10?");
  	  String openmvData = "";
  	  while(openmvData.indexOf("spot")<0) openmvData = RS485Serial.readString();
//  	  Serial.println(openmvData);
  	  while(openmvData.indexOf("111111")<0){
  		  for(int i=0;i<3;i++){
  			  rgbAllSet(red[0], red[1], red[2]); //所有灯红色
  			  shieldBuzzer(ON);
  			  delay(300);
  			  rgbAllOff();
  			  shieldBuzzer(OFF);
  			  delay(300);
  		  }
  		  failNum ++;
        Serial.println("fail dot");
        //反向运行
        RS485Serial.println(conveyorCode[3][2]);
        delay(500); 
        RS485Serial.println(conveyorCode[2][2]); 
        delay(8270);
        RS485Serial.println(conveyorCode[3][0]);
        delay(500);
        RS485Serial.println(conveyorCode[2][0]);
        Serial.println(openmvData);
        delay(500);
        while(Serial.available()>0){
          String spareDot = "";
          spareDot = Serial.readString();
          
          Serial.println(spareDot);
          if ( spareDot == "30,195,165,0,-25,90" ){
            Serial.println("get model success");
          }
          
          rgbAllSet(red[0], red[1], red[2]); //所有灯红色
          
          //字符串转int数组
          int index;
          int spareDotInts[6];
          int wz=0;
          int n;
          do{
           index = spareDot.indexOf(',');
           if(index != -1){
            spareDotInts[wz]=spareDot.substring(0, index).toFloat();
            spareDot = spareDot.substring(index+1, spareDot.length());
           }
           else{
            if(spareDot.length() > 0){
              n = spareDot.toInt();
            }
           }
           wz++;
          }while(index >= 0);
          delay(500);
          //执行补焊
          
          if (n == 1 || n == 2 || n == 3){
            arm3.movePose(spareDotInts[0], spareDotInts[1], spareDotInts[2], spareDotInts[3], spareDotInts[4], spareDotInts[5] ,ABS, MOVL);
            arm3.setMoveSpeed(500);
            arm3.movePose(0,0,-20,0,0,0,INC, MOVJ);
            arm3.pump(ON);
            arm3.setMoveSpeed(150);
            arm3.movePose(0,0,-8,0,0,0,INC, MOVJ);
            arm3.pump(OFF);
            arm3.setMoveSpeed(500);
            arm3.movePose(198.67 ,0 ,230.72 ,0 ,0 ,0 ,ABS, MOVJ);
            finishSolding = 1;
            finish = 1;
          }
          else if(n == 4 || n == 5 || n == 6){
            arm4.movePose(spareDotInts[0], spareDotInts[1], spareDotInts[2], spareDotInts[3], spareDotInts[4], spareDotInts[5] ,ABS, MOVL);
            arm4.setMoveSpeed(500);
            arm4.movePose(0,0,-20,0,0,0, INC, MOVJ);
            arm4.pump(ON);
            arm4.setMoveSpeed(150);
            arm4.movePose(0,0,-8,0,0,0, INC, MOVJ);
            arm4.pump(OFF);
            arm4.setMoveSpeed(500);
            arm4.movePose(198.67 ,0 ,230.72 ,0 ,0 ,0 ,ABS, MOVJ);
            finishSolding = 1;
            finish = 1;
          }
          
        }
//  		  String agvCmd = "C" + String(failNum);
//  		  wifi_updataStatus();//检测设备
//  		  Serial.println(agvCmd);
//  		  if(wifi_serive_send(AGV1,agvCmd)) Serial.println("发送成功");//发数据
//  		  else Serial.println("发送失败");
//  		  wifi_receive();
        //补焊完成
        Serial.println("get model done");
        openmvData = "111111";
        if(finishSolding == 0){
          
          rgbAllOff();
          RS485Serial.println(conveyorCode[2][1]);  //启动23传送带
          delay(500);
          RS485Serial.println(conveyorCode[3][3]);//启动24传送带
          delay(beltTime);
          //视觉检测
          RS485Serial.println(conveyorCode[2][0]);  //停止23传送带
          rgbSet(rgb4, yellow[0], yellow[1], yellow[2]);
          screenRX("panel1.t0.txt=\"start4\"");
          delay(1000);
          RS485Serial.println("@10?");
          String openmvData = "";
//          while(openmvData.indexOf("spot")<0){
//           openmvData = RS485Serial.readString();
//          }
          openmvData = "111111";
        }
  	  }
    }
    if(finish == 1){
      screenRX("panel1.t0.txt=\"finish6\"");
      RS485Serial.println(conveyorCode[2][1]);  //启动23传送带
      delay(500);
    }
    rgbSet(rgb4, green[0], green[1], green[2]);
    if(finish == 0){
      screenRX("panel1.t0.txt=\"finish4\"");
    }
    RS485Serial.println(conveyorCode[3][1]);  //启动24传送带
    delay(500);
    RS485Serial.println(conveyorCode[4][3]);//启动25传送带
    if(finish == 1){
      continue;
    }
    delay(beltTime);
    
    // 打磨
    RS485Serial.println(conveyorCode[3][0]);  //停止24传送带
    screenRX("panel1.t0.txt=\"start5\"");
    rgbSet(rgb5, yellow[0], yellow[1], yellow[2]);
	  delay(1000);
    arm5.runFile(armFile[4][0]);   //05机械臂打磨
    rgbSet(rgb5, green[0], green[1], green[2]);
    if(finish == 0){
      screenRX("panel1.t0.txt=\"finish5\"");
    }
  	for(int i=0;i<3;i++){
  		shieldBuzzer(ON);
  		delay(300);
  		shieldBuzzer(OFF);
  		delay(300);
  	}
    passNum ++;
  	String agvCmd = "D" + String(passNum);
  	Serial.println(agvCmd);
  	wifi_updataStatus();//检测设备
    if(finish == 0){
      screenRX("panel1.t0.txt=\"start6\"");
    }
  	if(wifi_serive_send(AGV1,agvCmd)) Serial.println("发送成功");//发数据
  	else Serial.println("发送失败");
	
  	wifi_receive();
    if(finish == 0){
      screenRX("panel1.t0.txt=\"finish6\"");
    }
    }
  screenRX("t1.txt=\"rest\"");
  Serial.println("All DONE WITH MODEL");
}

/***********串口屏调试控制函数***********/
void CONTROL(){
  String screenCmd = "";
  while(!screenCmd.equals("return")){
	  screenCmd = "000000";
	  screenCmd = screenTX();
	  
	  if(!screenCmd.equals("000000")){
		  while(RS485Serial.read()>=0){}
		  screenCmd.getBytes(controlBuf,8);
		  char i, j;
		  String _Str = "";
		  char _str[6];
		  switch(controlBuf[0]){
			  case 'A':	//机械臂控制指令
			  i = controlBuf[1];
			  j = controlBuf[2];
			  
			  if(j == 'H')	RS485Serial.println("@0" + String(i) + "$H");
			  else{_Str = "@0" + String(i) + "o111" + armFile[(int)i-49][(int)j-49]; RS485Serial.println(_Str);}
			  break;
			  
			  case 'C':	//传送带控制指令
			  i = controlBuf[1];
			  j = controlBuf[2];
			  
			  RS485Serial.println(conveyorCode[(int)i-49][(int)j-48]);
			  break;
			  
			  case 'V':	//AGV控制指令
			  i = controlBuf[1];
			  j = controlBuf[2];
			  char _str[] = {controlBuf[2], controlBuf[3],'\0'};
			  
			  if(i=='1'){
				  wifi_updataStatus();//检测设备
				  if(wifi_serive_send(AGV1,_str)) Serial.println("发送成功");//发数据
				  else Serial.println("发送失败");
			  }
			  else{
				  wifi_updataStatus();//检测设备
				  if(wifi_serive_send(AGV2,_str)) Serial.println("发送成功");//发数据
				  else Serial.println("发送失败");
			  }
			  break;
			  default:
			  break;
		  }
	  }
	  rgbAllWater(yellow[0], yellow[1], yellow[2], 100);
  }
}
