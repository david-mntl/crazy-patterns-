'''--------------------------------------------------------------------------
Fabian
Lenin
David
Abraham
 ----------------------------------------------------------------------------'''
import socket
from threading import Thread
import os
import serial
import time
from xml.dom import minidom

import simplejson
import json

'''--------------------------------------------------------------------------
                    Variables globales
 ----------------------------------------------------------------------------'''
global arduinoSerial, NUSUARIOS, DEBUG, PUERTO, PUERTOSERIAL, HOST



'''--------------------------------------------------------------------------
                    Configuracion de XML y Json
 ----------------------------------------------------------------------------'''
def loadXMLParameters():
    global DEBUG,PUERTO, NUSUARIOS, PUERTOSERIAL
    ruta=os.getcwd()
    rutaFinal=str(ruta)+"/configs.xml"
    xmlDocParametrosIni = minidom.parse(rutaFinal)


    pDebug = xmlDocParametrosIni.getElementsByTagName('DEBUG')
    pPuerto = xmlDocParametrosIni.getElementsByTagName('PUERTO')
    pNusuarios = xmlDocParametrosIni.getElementsByTagName('NUSUARIOS')
    pPuertoSerial = xmlDocParametrosIni.getElementsByTagName('PUERTOSERIAL')

    load_DEBUG=pDebug[0].attributes['value'].value
    PUERTOSERIAL = str(pPuertoSerial[0].attributes['value'].value)
    PUERTO=int(pPuerto[0].attributes['value'].value)
    NUSUARIOS = 0

    if(load_DEBUG == "true"):   #Se define vDEBUG
        DEBUG = True
    else:
        DEBUG = False


def putJson(filename, n,s,x,y):
        with open(filename, "a") as outfile:json.dump({'numbers':n, 'strings':s, 'x':x, 'y':y}, outfile, indent=4, skipkeys=True, sort_keys=True)

def getJson(filename):
    with open(filename) as json_file:data = json.load(json_file)
    return data

'''--------------------------------------------------------------------------
                    Configuracion inicial del socket servidor
 ----------------------------------------------------------------------------'''
def iniControladorSerial():
    global PUERTO, PUERTOSERIAL, arduinoSerial, HOST
    arduinoSerial = serial.Serial("/dev/ttyACM5", 9600)
    print("Inicializando")
    while(True):
        txt= arduinoSerial.readline()
        txt=txt.split('\n')
        if(txt[0]== "startx\r"):
            portSize =  int(arduinoSerial.readline());
            hostSize = int(arduinoSerial.readline());

            puerto = ""
            host = ""
            for i in range(0,portSize):
                x=arduinoSerial.readline()
                x=x.split('\n')
                x=x[0].split('\r')
                puerto+=x[0]
            for i in range(0,hostSize):
                z=arduinoSerial.readline()
                z=z.split('\n')
                z=z[0].split('\r')
                host+=z[0]
            print("PUERTO FINAL: " + puerto)
            print("HOST FINAL: " + host)
            #setupServer()
            #listen()
            break
        time.sleep(0.1)

def enviarPorSerial(pData):
    global arduinoSerial
    arduinoSerial.write("ENVIO :D")
    print("ok")


'''--------------------------------------------------------------------------
                    Configuracion inicial del socket servidor
 ----------------------------------------------------------------------------'''
def setupServer():
    global server,PUERTO
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        if(DEBUG== True):
            print("PUERTO: "+str(PUERTO))
        server.bind(('', PUERTO))
        server.listen(5)
    except:
        print("Failed to create socket")


'''--------------------------------------------------------------------------
                    escuchar los clientes
 ----------------------------------------------------------------------------'''
def listen():
    global server,NUSUARIOS

    if(DEBUG== True):
        print("- Server Initialized -")
    while True:
        #print("Waiting...")
        conn, addr = server.accept()
        NUSUARIOS+=1
        if(DEBUG== True):
            print("**User Connected**" + " User:" + str(NUSUARIOS))
        b= Thread(target=handleClient, args=(conn,addr))
        b.start()

        if(NUSUARIOS==0):
            break
    server.close()
    if(DEBUG== True):
        print("- Server Closed -")

'''--------------------------------------------------------------------------
                    thread para cada cliente
 ----------------------------------------------------------------------------'''
def handleClient(conn,addr):
    global NUSUARIOS
    #conn.send("*Connected*\n")
    while True:
        data = conn.recv(1024)
        data = data.decode("utf-8")
        data = data.split('\n')
        try:
            if(data[0] != ""):
                if(data[0] == "exit" and DEBUG):
                    print("usuario desconectado")
                    conn.close()
                    break
                message = "Received: " + data[0] + '\n'
                conn.send(message)
                if(DEBUG== True):
                    print("mensaje recibido: "+data[0]+".   de :"+str(addr[0]))
        except:
            pass
    NUSUARIOS-=1

loadXMLParameters()
#iniControladorSerial()
#setupServer()
#listen()



'''
ruta=os.getcwd()
rutaFinal=str(ruta)+"/configs.xml"
doc = minidom.parse(rutaFinal)

XMLvalues='a'

root = doc.createElement("User")
#root.setAttribute( "id", 'myIdvalue' )
#root.setAttribute( "email", 'blabla@bblabla.com' )

doc.appendChild(root)

for value in XMLvalues:
    # Create Element
    tempChild = doc.createElement(value)
    root.appendChild(tempChild)

# Write Text
nodeText = doc.createTextNode( XMLvalues[value].strip() )
tempChild.appendChild(nodeText)

doc.writexml( open('data.xml', 'w'),indent="  ",addindent="  ",newl='\n')

doc.unlink()'''