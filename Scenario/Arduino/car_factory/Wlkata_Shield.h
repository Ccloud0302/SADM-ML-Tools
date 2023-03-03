#ifndef WLKATA_SHIELD_H
#define WLKATA_SHIELD_H

#include <Arduino.h>

/***********扩展接口***********/
#define S1  2
#define S2  3
#define S3  A0
#define S4  A1
#define S5  A2
#define S6  A3
#define S7  A4
#define S8  A5
#define S9  A6
#define S10 A7
#define S11 A10
#define S12 A11
#define S13 A12
#define S14 A13
#define S15 A14
#define S16 A15
/************按键*************/
#define KEY1 11
#define KEY2 10
#define KEY3 12
/************RGB*************/
#define OFF 0
#define RED 1
#define GREEN 2
#define BLUE 3

#define ON 1

void shieldInit(void);
void shieldRgb(int mode = OFF);
void shieldBuzzer(bool mode);

bool shieldKeyValue(int keyNum);

#endif
