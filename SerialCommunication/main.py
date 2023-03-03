import time
from datetime import datetime
import serial
from getModel import getModel
import chardet

# 串口配置
port = "COM6"
bps = 9600  # 波特率，标准值之一：50,75,110,134,150,200,300,600,1200,1800,2400,4800,9600,19200,38400,57600,115200
time = 1  # 超时设置,None：永远等待操作，0为立即返回请求结果，其他值为等待超时时间(单位为秒）
ser = serial.Serial(port=port, baudrate=bps, bytesize=serial.EIGHTBITS, timeout=time)


# 关闭串口
def port_close():
    ser.close()
    if ser.isOpen():
        print("串口关闭失败！")
    else:
        print("串口关闭成功！")


# 从串口读取数据
def read_data_from_serial():
    while True:
        now = datetime.now()
        every_time = now.strftime('%Y-%m-%d %H:%M:%S')  # 时间戳
        data = ser.readline()
        if data == "exit":  # 退出标志
            break
        else:
            print(every_time, data)


# 向串口发送数据
def send_model_to_serial(send_data):
    if ser.isOpen():
        print(ser.portstr)
        ser.write(send_data.encode('utf-8'))


if __name__ == '__main__':
    while True:
        now = datetime.now()
        every_time = now.strftime('%Y-%m-%d %H:%M:%S')  # 时间戳
        data = ser.readline()


        # 如果data有特殊编码就进行转换
        if chardet.detect(data)["encoding"] == 'Windows-1252':
            data = data.decode('Windows-1252')
            print(every_time, data)
        else:
            data = data.decode()
            print(every_time, data)

        # 代表视觉传回了点位
        if "spot" in data:
            openMvNumString = data[12:18]
            print(openMvNumString)
            for i in range(len(openMvNumString)):  # 循环遍历判断错点
                if openMvNumString[i] == '0':  #
                    # 则获取模型
                    get_model = getModel()
                    data_attr = get_model.get_model()
                    spare_dot = [j['attrValue'] + ',' + j['attrLabel'][-1] for j in data_attr['userAttr'] if
                                 '备用焊点' in j['attrLabel'] and str(i + 1) in j['attrLabel']][0]
                    n = data_attr['userAttr']  # 备用焊点，字符串类型的list,例如[10,-195,170,0,-25,-90,2]
                    print(spare_dot)
                    send_model_to_serial(spare_dot)

        if data == "get model done\r\n":
            send_model_to_serial("done")

    port_close()
