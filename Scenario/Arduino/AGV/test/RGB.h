#ifndef __RGB_H__
#define __RGB_H__

#include "Arduino.h"
#include "Adafruit_NeoPixel.h"

#define PIXEL_PIN    25  // RGB灯条连接的数字引脚
#define PIXEL_COUNT 8  // 灯珠数量

void rgb_init();
void setColor(uint8_t r,uint8_t g,uint8_t b);

#endif
