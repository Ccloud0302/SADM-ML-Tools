#include "AGV.h"

//电机引脚 pwm dir1 dir2
uint8_t motor_Pin[4][3]={
	{12,34,35},
	{8,37,36},
	{9,43,42},
	{5,A4,A5}
}; 
//编码器引脚 E1 E2
uint8_t encoder_Pin[4][2]={
	{5,31},
	{4,38},
	{1,49},
	{0,A1}
};

uint8_t speed=13;
volatile int encoder_count[4];
volatile long encoder_sum[4];
volatile int v_aim[4];

void tim5_begin() {			// 启动定时器
  TCCR5A = 0;//将整个TCCR1A寄存器设置为0
  TCCR5B = 0;//将整个TCCR1B寄存器设置为0
  TCCR5B |= (1 << WGM52);//设置模式 CTC
  TCCR5B |= (1 << CS52);//设置时钟源 CS52位为1(256倍预分频)
  TCNT5  = 0;//将计数器值初始化为0
  
	OCR5A = 1250*3-1;
	TIMSK5 |= (1 << OCIE5A);//开启定时器
}	
void tim5_end() {				// 关闭定时器
	TIMSK5 &= ~(1<<OCIE5A);//关闭定时器
}
ISR(TIMER5_COMPA_vect) { 
	//digitalWrite(A15,digitalRead(A15) ^ 1);
	motor_PID();
}
void encoder0(){				//外部中断
	if(digitalRead(encoder_Pin[0][1])==0) encoder_count[0]++;
	else encoder_count[0]--;
}
void encoder1(){				//外部中断
	if(digitalRead(encoder_Pin[1][1])==0) encoder_count[1]++;
	else encoder_count[1]--;
}
void encoder2(){				//外部中断
	if(digitalRead(encoder_Pin[2][1])==0) encoder_count[2]++;
	else encoder_count[2]--;
}
void encoder3(){				//外部中断
	if(digitalRead(encoder_Pin[3][1])==0) encoder_count[3]++;
	else encoder_count[3]--;
}
void Motor_init(){			//电机初始化
	for(uint8_t i=0;i<4;i++){
		pinMode(motor_Pin[i][0],OUTPUT);
		pinMode(motor_Pin[i][1],OUTPUT);
		pinMode(motor_Pin[i][2],OUTPUT);

		pinMode(encoder_Pin[i][1],INPUT);
	}
	attachInterrupt(encoder_Pin[0][0], encoder0, RISING);
	attachInterrupt(encoder_Pin[1][0], encoder1, RISING);
	attachInterrupt(encoder_Pin[2][0], encoder2, RISING);
	attachInterrupt(encoder_Pin[3][0], encoder3, RISING);
}
void Motor_PWM(uint8_t _num, int _speed){	//设置电机速度
	uint8_t _M_A, _M_B, _M_PWM;
	
	_M_PWM = motor_Pin[_num][0];
	_M_A = motor_Pin[_num][1];
	_M_B = motor_Pin[_num][2];
	
	if(_speed>0){
		digitalWrite(_M_A, LOW);
		digitalWrite(_M_B, HIGH);
	}
	else if(_speed<0){
		digitalWrite(_M_A, HIGH); 
		digitalWrite(_M_B, LOW);
	}
	else{
		digitalWrite(_M_A, HIGH); 
		digitalWrite(_M_B, HIGH);
	}
	_speed = abs(_speed);						//设置pwm范围 0-255
	analogWrite(_M_PWM,_speed);
}
void motor_PID(){
	static int pwm[4];
	static float Kp=0.5;
	static float Ki=1.2;
	static int lastError[4]={0,0,0,0};

  if(v_aim[0]==0){
    lastError[0]=0;
    lastError[1]=0;
    lastError[2]=0;
    lastError[3]=0;
    pwm[0]=0;
    pwm[1]=0;
    pwm[2]=0;
    pwm[3]=0;
  }
	
	for(uint8_t i=0;i<4;i++){
		int Error = v_aim[i]-encoder_count[i]; //计算误差
		
		pwm[i]+=Kp*(Error-lastError[i])+Ki*Error;
		lastError[i] =	Error;

		if (pwm[i] < -255) pwm[i] = -255;     //限制pwm范围
		else if (pwm[i] > 255) pwm[i] = 255;


//if(i==0){
//	Serial.print(encoder_count[i]),Serial.print(',');
//	Serial.print(pwm[i]),Serial.print(',');
//	Serial.println();
//}

		encoder_count[i]=0;
		Motor_PWM(i,pwm[i]);
	}
}
void AGV_motor_PWM(int v0,int v1,int v2,int v3){
	Motor_PWM(0,v0);
	Motor_PWM(1,v1);
	Motor_PWM(2,v2);
	Motor_PWM(3,v3);
}
void AGV_motor_pid(int v0,int v1,int v2,int v3){
	v_aim[0]=v0;
	v_aim[1]=v1;
	v_aim[2]=v2;
	v_aim[3]=v3;
}
void ADVANCE(){
	AGV_motor_pid(speed,-speed,speed,-speed);
}
void BACK(){
	AGV_motor_pid(-speed,speed,-speed,speed);
}
void LEFT(){
	AGV_motor_pid(-speed,-speed,speed,speed);
}
void RIGHT(){
	AGV_motor_pid(speed,speed,-speed,-speed);
}
void CW(){
	AGV_motor_pid(speed,speed,speed,speed);
}
void CCW(){
	AGV_motor_pid(-speed,-speed,-speed,-speed);
}
void STOP(){
	AGV_motor_pid(0,0,0,0);
}
void Car_do(String _str, uint16_t _distance) {     //小车动作指令
  Serial.print(_str);
  Serial.print(", ");
  Serial.println(_distance);
  if(_str == "advance"){
    ADVANCE();
    delay(_distance);
    STOP();
  }
  else if(_str == "back"){
    BACK();
    delay(_distance);
    STOP();
  }
  else if(_str == "left"){
    LEFT();
    delay(_distance);
    STOP();
  }
  else if(_str == "right"){
    RIGHT();
    delay(_distance);
    STOP();
  }
  else if(_str == "ccw"){
    CCW();
    delay(_distance);
    STOP();
  }
  else if(_str == "cw"){
    CW();
    delay(_distance);
    STOP();
  }
}
void move_car(carpos *action){
  uint8_t i=0;
  while (action[i].dir!=""){
    Car_do(action[i].dir, action[i].dis);
    delay(500);
    i++;
  }
}
