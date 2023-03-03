#ifndef _MIROBOT_H_
#define _MIROBOT_H_

#include <Arduino.h>

#define MOVJ 1
#define MOVL 2
#define JUMP 3

#define ABS 1
#define INC 0

#define CW 1
#define CCW 0

#define ON 1
#define OFF 0
#define GRAB 2
#define BLOW 2

#define Offline 0
#define Idle 1
#define Alarm 2
#define Run 5
#define Busy 4

class UART_Mirobot{
  public:
    UART_Mirobot();
    void serialInit(int serialPort = 0);    //设置与Mirobot通信的串口（0/Serial; 1/Serial1; 2/Serial2; 3/Serial3)
    void setSerialMonitoring(bool mode);    //设置开启串口监视，通过Arduino串口监视器查看
    void setCmdTimeout(long cmdTimeout);   //设置每条指令发送后超时等待的时间（单位：毫秒ms）
    void homing(int homingMode = 0);    //控制机械臂归零（0/$H：快速归零；1/$H1：1轴归零；2/$H2：2轴归零；3/$H3：3轴归零；4/$H4：4轴归零；
                                        //5/$H5：5轴归零；6/$H6：6轴归零（需对应硬件支持）；7/$H7：滑轨归零；
    void zero();    //机械臂初始位置
    void setMoveSpeed(int velocity);    //设置机械臂运动速度
    void setJumpPara(int height);   //设置JUMP门型轨迹抬起高度
    void timedPause(float seconds);   //暂停等待（单位：秒s）
    
    void moveJoints(float j1, float j2, float j3, float j4, float j5, float j6, bool motionMode = ABS);   //角度模式运动
    void movePose(float x, float y, float z, float rx, float ry, float rz, bool motionMode = ABS, int pathMode = MOVJ);   //坐标模式运动
    void moveArc(float x, float y, float z, float r, bool motionMode = ABS, int pathMode = CW);   //圆弧插补运动
    
    void gripper(int gripperStatus);    //舵机夹爪状态
    void softGripper(int softGripperStatus);    //三指柔爪状态
    void pump(int pumpStatus);    //气泵状态
    void pwmWrite(int pwm);   //pwm（控制器黄色接口）输出，范围：0~1000

    void moveConveyor(float d, bool motionMode = ABS);    //传送带运动控制
    void moveRail(float d, bool motionMode = ABS);    //滑轨运动控制

    int getStatus();  //获取机械臂工作状态，0/1/2/3/4/5： 0机械臂离线 1正常 2锁定 3复位中 4设备忙 5运行中 
    void runFile(String fileName);  //运行控制器中存储的Gcode文件
    void sendMsg(String gcode, bool check = ON);

  protected:
    bool commMode;
    int mirobotSerialPort = 0;
    bool serialMonitoringMode = 1;
    int mirobotVelocity = 2000;
    long mirobotCmdTimeout = 10000L;
    void mirobotReceived();
    String strMsg = "";
    String address;
};

class RS485_Mirobot: public UART_Mirobot{
  public:
    void rs485Init(int add = 1, long baudRate = 115200);

};



#endif
