import socket
from threading import Thread
import threading
import os
import serial

global init,userCount,arduino
puerto = 9696
init = False
userCount = 0
arduino = serial.Serial('/dev/ttyAMA0',9600)

def setupServer():
    global server,init
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        print("PUERTO: "+str(puerto))
        server.bind(('', puerto))
        init = True
        server.listen(5)
    except:
        print("Failed to create socket")

def listen():
    global server,init,userCount
    if(init == True):
        print("- Server Initialized -")
    while True:
        conn, addr = server.accept()
        userCount+=1
        print("**User Connected**" + " User:"+str(userCount))
        b= Thread(target=handleClient, args=(conn,addr))
        b.setDaemon(True)
        b.start()
        b.join()
        if(userCount == 0):
            break
    server.shutdown(2)
    server.close()
    
    print("- Server Closed -")
def handleClient(conn,addr):
    global userCount,arduino
    while True:
        data = conn.recv(1024)
        data = data.decode("utf-8")
        data = data.split('\n')
        try:
            if(data[0] != ""):
                if(data[0] == "exit"):
                    print("**User disconnected**")
                    break
                print(data[0])
                arduino.write(data[0])
        except:
            pass
    userCount-=1
setupServer()
listen()
