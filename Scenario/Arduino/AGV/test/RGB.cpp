#include "RGB.h"

Adafruit_NeoPixel rgb(PIXEL_COUNT, PIXEL_PIN, NEO_GRB + NEO_KHZ800);

void rgb_init(){
  rgb.begin();
  rgb.clear();
  rgb.show();
}
void setColor(uint8_t r,uint8_t g,uint8_t b){  //设置灯组全部灯珠颜色
  rgb.clear();
  for( int i = 0 ;i < PIXEL_COUNT; i++)  {
    rgb.setPixelColor(i, rgb.Color(r,g,b));
    rgb.show();
  }
}
