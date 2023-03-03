'''
project：  Solder joints Detection
function： When the Solder joints is detected, it is sent to the robot arm through a serial port.
           8 Solder joints need to be identified and openmv will return IDs without successful and "ok" if all are successful.
Date:      December 20, 2020
Author:    qin_new
'''

import sensor, image, time,  pyb ,utime
import lcd
from pyb import UART
from pyb import Pin, Timer

#threshold   =  (0, 52, -128, 127, -128, 127)    # 识别颜色阈值
threshold   =  (0, 43, -128, 127, -128, 127)    # 识别颜色阈值
coor_x = [80,160,240,80,160,240] # x坐标
coor_y = [70,70,70,170,170,170] # y坐标
error = 30  # 识别范围
length=5   # 识别点最小体积

sensor.reset() # 摄像头初始化 Initialize the camera sensor.
sensor.set_pixformat(sensor.RGB565) # RGB565
sensor.set_framesize(sensor.QVGA) # QVGA
sensor.skip_frames(10) # Let new settings take affect.
sensor.set_auto_whitebal(False) # 关闭白平衡 turn this off.

uart = UART(3, 38400) # 串口初始化
tim = Timer(4, freq=1000) # led调光定时器
Led = tim.channel(3, Timer.PWM, pin=Pin("P9"), pulse_width_percent=0)   # led初始化
Button = Pin('P6', Pin.IN, Pin.PULL_UP) # 按钮初始化
lcd.init() # LCD初始化 Initialize the lcd screen.

spot_status = [1,1,1,0,0,0]    # 点的状态 0点异常 1点正常
run_flag = True    # 识别状态 0停止 1开始

while(True):
    if Button.value()==0:
        if 1:
            Led.pulse_width_percent(20)
        else:
            Led.pulse_width_percent(0)

    '''获取图像'''
    img = sensor.snapshot() # 获取图像Take a picture and return the image.

    '''比对'''
    if run_flag == True :
        spot_status = [0,0,0,0,0,0] # 列表清空
        for i in range(6) :
            blobs = img.find_blobs([threshold], roi=[coor_x[i]-error, coor_y[i]-error, error*2, error*2]) # 寻找色块 阈值，范围，x最小像素，y最小像素，面积
            if len(blobs)==1:
                for b in blobs:
                    if (b.w() > length) and (b.h() > length) :
                        img.draw_rectangle(b.x(), b.y(), b.w(), b.h()) # 绘制方框
                        spot_status[i] = 1
        print(spot_status)

    if uart.any():
        data = uart.read() # 串口接收数据
        if data[0:3] == b"@10":
            res_uart = data.decode().replace("\r\n","")  # 去掉回车符
            print("Data:"+res_uart)
            if res_uart == "@10start":  # 收到指令，开始检测
                run_flag = True    # 检测标志位清零
                uart.write("ok")
            elif res_uart == "@10stop":  # 收到指令，开始检测
                run_flag = False    # 检测标志位清零
                uart.write("ok")
            elif res_uart == "@10?" :
                if run_flag == False :  # 检测未完成，返回"等待"
                    uart.write("error")
                else :
                    for i in range(3) :
                        Led.pulse_width_percent(50)
                        pyb.delay(500)
                        Led.pulse_width_percent(0)
                        pyb.delay(500)
                    uart.write("spot_status:"+''.join(str(i) for i in spot_status)+"\r\n") # 检测完成，返回检测信息

    '''绘制辅助线'''
    img.draw_rectangle(10, 10, length, length, color = (255, 0, 0)) # 绘制方框
    img.draw_arrow(40, 15, 100, 15, color = (0, 255, 0), size = 15, thickness = 2)  # 绘制箭头
    for i in range(6) :
        img.draw_rectangle(coor_x[i]-error, coor_y[i]-error, error*2, error*2, color = (0, 0, 255)) # 绘制方框
        img.draw_string(coor_x[i]-error, coor_y[i]-error, str(i+1), color = (0, 0, 255), scale = 2)

    lcd.display(img) # Take a picture and display the image.
