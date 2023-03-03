import tkinter
from tkinter import *
from tkinter import messagebox
import time
from datetime import datetime
import serial
from getModel import getModel
import chardet

# 串口配置
port = "COM6"
bps = 9600  # 波特率，标准值之一：50,75,110,134,150,200,300,600,1200,1800,2400,4800,9600,19200,38400,57600,115200
time = 1  # 超时设置,None：永远等待操作，0为立即返回请求结果，其他值为等待超时时间(单位为秒）
ser = ''
try:
    ser = serial.Serial(port=port, baudrate=bps, bytesize=serial.EIGHTBITS, timeout=time)
except:
    print('Sorry, No COM 6 Now')
else:
    print('Yes, Get COM 6')


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


def output():
    print("You click something")


# 定义回调函数，当用户点击窗口x退出时，执行用户自定义的函数
def QueryWindow():
    # 显示一个警告信息，点击确后，销毁窗口
    if messagebox.showwarning("提示", "你将会退出"):
        # 这里必须使用 destory()关闭窗口
        root.destroy()


# 同串口COM6通讯，并返回相应的数组
def ComQuery():
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
                print("hhhhhhhhhhhh")
                # 则获取模型
                get_model = getModel()
                data_attr = get_model.get_model()
                spare_dot = [j['attrValue'] + ',' + j['attrLabel'][-1] for j in data_attr['userAttr'] if
                             '备用焊点' in j['attrLabel'] and str(i + 1) in j['attrLabel']][0]
                n = data_attr['userAttr']  # 备用焊点，字符串类型的list,例如[10,-195,170,0,-25,-90,2]
                print(data_attr)
                send_model_to_serial(spare_dot)
                print("hhhhhhhhhhhh")

    if data == "get model done\r\n":
        send_model_to_serial("done")
    return {}


if __name__ == '__main__':
    root = Tk()

    root.geometry("600x400+790+400")  # 设置窗口长宽和坐标

    # 窗口标题
    root.title('python_arduino串口通信')
    root.iconphoto(True, tkinter.PhotoImage(file="pictures/web.png"))

    # 添加标签
    message = "Arduino焊点"
    spot_arr = {1, 2, 10, 52, -100, 56}

    label1 = Label(root, text=message, font=("宋体", 15), fg="red")
    spot_str = ""
    for i in spot_arr:
        i = str(i)
        spot_str += i
        spot_str += " "

    label1.place(x=35, y=5)
    label2 = Label(root, text=spot_str, font=18, relief="solid", width=20, height=20)
    label2.place(x=5, y=30)

    label3 = Label(root, text="获取model焊点", font=("宋体", 15), fg="red")
    label3.place(x=200, y=5)

    label4 = Label(root, font=18, relief="solid", width=20, height=20)
    label4.place(x=180, y=30)

    label5 = Label(root, text="Arduino控制代码", font=("宋体", 15), fg="red")
    label5.place(x=365, y=5)

    label6 = Label(root, font=18, relief="solid", width=20, height=20)
    label6.place(x=355, y=30)

    root.mainloop()

    data_attr = {}
    while True:
        data_attr = ComQuery()
        # data_attr = {'code': 'car', 'color': '#1E90FF', 'modelVersion': 'V1.0', 'language': 'zh', 'translated': 'true', 'type': '资源', 'uuid': '10914', 'fx': 862.0, 'r': 30, 'fy': 319.0, 'namespace': 'weld.split', 'name': '车模', 'alias': '已拼接车顶车模', 'extendable': 'false', 'userAttr': [{'id': '1665214203945', 'attrValue': '-10,-195,170,0,-25,-90', 'attrLabel': '备用焊点1'}, {'id': '1665214221761', 'attrValue': '10,-195,170,0,-25,-90', 'attrLabel': '备用焊点2'}, {'id': '1665214236264', 'attrValue': '30,-195,170,0,-25,-90', 'attrLabel': '备用焊点3'}, {'id': '1665215327145', 'attrValue': '-10,195,165,0,-25,90', 'attrLabel': '备用焊点4'}, {'id': '1665215337816', 'attrValue': '10,195,165,0,-25,90', 'attrLabel': '备用焊点5'}, {'id': '1665215357033', 'attrValue': '30,195,165,0,-25,90', 'attrLabel': '备用焊点6'}]}
        print(data_attr)
        if len(data_attr) != 0:
            spare_dot = [j['attrValue'] + ',' + j['attrLabel'][-1]
                         for j in data_attr['userAttr']
                            if '备用焊点' in j['attrLabel'] and str(1 + 1) in j['attrLabel']][0]
            print(spare_dot)
            label2 = Label(root, text=spare_dot, font=18, relief="solid", width=20, height=20)
            label2.place(x=5, y=30)
            root.mainloop()


    # 添加输入框
    # entry = Entry(root, font=("宋体", 25), fg="red")
    # entry.grid(row=0, column=1)

    # 添加点击按钮
    # button = Button(root, text="签名设计", font=("宋体", 25), fg="blue", command=output)
    # button.grid(row=1, column=1)
