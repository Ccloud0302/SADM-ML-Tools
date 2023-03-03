#include "arm.h"

UART_Mirobot arm; //创建机械臂对象

float error_y=0;
float error_z=0;

void arm_init(){
  arm.serialInit(2);  //设置Serial2与机械臂通信，波特率115200；
  arm.homing(); //机械臂快速归零
  arm.setMoveSpeed(3000); //设置机械臂最大运动速度3000
}
void move_arm(armpos *action){
  uint8_t i=0;
  while (action[i].x!=NULL){
    arm.movePose(action[i].x, action[i].y+error_y, action[i].z+error_z, action[i].a, action[i].b, action[i].c);
    i++;
  }
}
void arm_error_yz(float _error_y, float _error_z){
  error_y = _error_y;
  error_z = _error_z;
}
void arm_homing(){
  arm.homing(); //机械臂快速归零
}
