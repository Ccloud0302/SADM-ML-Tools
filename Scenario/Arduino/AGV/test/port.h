#ifndef __port_H__
#define __port_H__

#include "Arduino.h"
#include "wifi.h"
#include "AGV.h"
#include "arm.h"
#include "RGB.h"

enum port_type{
  USB,WIFI  
};

String toString(uint8_t *buffer, uint8_t buffer_size);
void data_deal(uint8_t _port,uint8_t *_buffer,uint8_t _len);
void refresh_USB();
void refresh_WIFI();

#endif
