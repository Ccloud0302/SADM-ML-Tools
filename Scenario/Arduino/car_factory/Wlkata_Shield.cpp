#include "Wlkata_Shield.h"

void shieldInit(){
  for(int i=7;i<10;i++)  {pinMode(i,OUTPUT);digitalWrite(i,HIGH);}  //RGB
  for(int i=10;i<13;i++)  pinMode(i,INPUT_PULLUP);  //KEY
  pinMode(38,OUTPUT); digitalWrite(38,LOW); //BUZZER
}

void shieldRgb(int mode)
{
  for(int i=7;i<10;i++) digitalWrite(i,HIGH); //OFF
  if(mode == RED) digitalWrite(9,LOW);  //RED
  if(mode == GREEN) digitalWrite(8,LOW);  //GREEN
  if(mode == BLUE) digitalWrite(7,LOW); //BLUE
}
void shieldBuzzer(bool mode)
{
    if(mode)  digitalWrite(38,HIGH);  //OPEN
    else      digitalWrite(38,LOW); //CLOSE
}

bool shieldKeyValue(int keyNum){
  return !digitalRead(keyNum);
}
