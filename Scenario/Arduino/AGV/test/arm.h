#ifndef __arm_H__
#define __arm_H__

#include "Arduino.h"
#include <Mirobot.h>
#include "action.h"

void arm_init();
void move_arm(armpos *action);
void arm_error_yz(float _error_y, float _error_z);
void arm_homing();

#endif
