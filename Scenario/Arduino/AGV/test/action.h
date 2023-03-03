#ifndef __action_H__
#define __action_H__

#include "Arduino.h"

struct carpos {
  String dir;
  uint16_t dis;
};

const carpos car1PosC1[]={
  {"back",2000},
  {"right",2200},
  {"advance",2500},
  {"",NULL}
};
const carpos car1Pos3[]={
  {"back",4800},
  {"ccw",3050},
  {"",NULL}
};
const carpos car1Pos4[]={
  {"advance",5500},
  {"",NULL}
};
const carpos car1Pos5[]={
  {"back",1720},
  {"cw",3150},
  {"advance",5000},
  {"",NULL}
};
const carpos car1PosD1[]={
  {"back",2000},
  {"left",2200},
  {"advance",2500},
  {"",NULL}
};
const carpos car1PosD2[]={
  {"back",2000},
  {"right",4400},
  {"advance",2500},
  {"",NULL}
};
const carpos car2PosB1[]={
  {"back",13000},
  {"left",700},
  {"ccw",6300},
  {"advance",2500},
  {"",NULL}
};
const carpos car2PosB2[]={
  {"back",2700},
  {"cw",3300},
  {"advance",6000},
  {"",NULL}
};

struct armpos {
  float x;
  float y;
  float z;
  float a;
  float b;
  float c;
};
const armpos arm1Pos0[]={
  {140, 0, 150, 0, 0, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm1PosC1[]={
  {118.9, 13, 40, 0, -90, 0},
  {118.9, 13, 15, 0, -95, 0},
  {155.9, 13, 15, 0, -90, 0},
  {198.9, 13, 15, 0, -90, 0},
  {180, 0, 80, 0, -105, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm1PosD1[]={
  {118.9, 43, 40, 0, -90, 0},
  {118.9, 43, 15, 0, -95, 0},
  {155.9, 43, 15, 0, -90, 0},
  {198.9, 43, 15, 0, -90, 0},
  {180, 0, 80, 0, -105, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm1Pos2[]={
  {154.5, 0, 222.9, 0, -100, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm1Pos3[]={//c1
  {145, 0, 210, 0, -95, 0},
  {240, 0, 210, 0, -95, 0},
  {240, 0, 195, 0, -95, 0},
  {200, 0, 195, 0, -95, 0},
  {145, 0, 195, 0, -95, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm2Pos0[]={
  {155, 0, 120, 0, 0, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm2Pos1[]={
  {153.6, 0, 120.7, 0, 0, 0},
  {125.2, 0, 40, 0, -90, 0},
  {120.2, 0, -20, 0, -90, 0},
  {198.2, 0, -20, 0, -90, 0},
  {205.2, 0, 0, 0, -95, 0},
  {205.2, 0, 50, 0, -110, 0},
  {160, 0, 130, 0, -95, 0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm2Pos2[]={
  {160 ,0 ,130 ,0 ,-95 ,0},
  {255 ,0 ,130 ,0 ,-95 ,0},
  {255 ,0 ,112 ,0 ,-90 ,0},
  {200 ,0 ,112 ,0 ,-90 ,0},
  {160 ,0 ,112 ,0 ,-90 ,0},
  {165 ,0 ,160 ,0 ,-90 ,0},
  {160 ,0 ,190 ,0 ,-90 ,0},
  {200 ,0 ,190 ,0 ,-90 ,0},
  {255 ,0 ,190 ,0 ,-90 ,0},
  {255 ,0 ,215 ,0 ,-105 ,0},
  {155 ,0 ,200 ,0 ,-95 ,0},
  {155 ,0 ,160 ,0 ,-95 ,0},
  {155 ,0 ,120 ,0 ,-95 ,0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};
const armpos arm2Pos3[]={
  {200 ,0 ,0 ,0 ,-95 ,0},
  {195 ,0 ,-20 ,0 ,-90 ,0},
  {120 ,0 ,-20 ,0 ,-90 ,0},
  {120 ,0 ,40 ,0 ,-90 ,0},
  {150 ,5 ,120.7 ,0 ,0 ,0},
  {NULL,NULL,NULL,NULL,NULL,NULL}
};

#endif
