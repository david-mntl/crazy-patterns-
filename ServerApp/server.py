import socket
from threading import Thread
import threading
import os

global init,userCount
puerto = 9898
init = False
userCount = 0
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
    global userCount
    while True:
        data = conn.recv(1024)
        data = data.decode("utf-8")
        try:
            if(data != ""):
                if(data == "exit\n"):
                    print("**User disconnected**")
                    break
                print(data)
        except:
            pass
    userCount-=1
setupServer()
listen()
