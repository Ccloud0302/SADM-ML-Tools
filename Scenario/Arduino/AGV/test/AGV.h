#ifndef __AGV_H__
#define __AGV_H__

#include "Arduino.h"
#include "action.h"

void tim5_begin();
void tim5_end();
void Motor_init();
void Motor_PWM(uint8_t _num, int _speed);
void motor_PID();
void AGV_motor_PWM(int v0,int v1,int v2,int v3);
void AGV_motor_pid(int v0,int v1,int v2,int v3);
void ADVANCE();
void BACK();
void LEFT();
void RIGHT();
void CCW();
void CW();
void STOP();
void Car_do(String _str, uint16_t _distance);
void move_car(carpos *action);

#endif
