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

<<<<<<< HEAD
=======
import internetChecker as ichecker #Importa el .py encargado de verificar la conexion a internet
import RPi.GPIO as gp #Importa biblioteca para el uso de puertos GPIO

>>>>>>> 866568cfdcd20ce0bb36978a75be8f930b2e88d3
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
    #pPuerto = xmlDocParametrosIni.getElementsByTagName('PUERTO')
    pPuertoSerial = xmlDocParametrosIni.getElementsByTagName('PUERTOSERIAL')

    load_DEBUG=pDebug[0].attributes['value'].value
    PUERTOSERIAL = str(pPuertoSerial[0].attributes['value'].value)
    #PUERTO=int(pPuerto[0].attributes['value'].value)
    PUERTO = 0
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
    global PUERTO, PUERTOSERIAL, arduinoSerial, HOST,DEBUG,LOCAL_IP
    gp.setmode(gp.BCM)               #Iniciar puertos GPIO
    gp.setwarnings(False)
    gp.setup(19,gp.OUT)              #GPIO 19 como output (Luz de inicio)
    gp.setup(26,gp.OUT)              #GPIO 26 como output (Luz de Internet)
    gp.output(19,False)
    gp.output(26,False)
    
    try:
        arduinoSerial = serial.Serial(PUERTOSERIAL, 9600)
        if(DEBUG == True): 
            print("-Initializing-")
    except:
        if(load_DEBUG == "true"): 
            print("Could not find serial port" + str(PUERTOSERIAL))

    gp.output(19,True)
    network = ichecker.checkNetwork() #Verificar si hay conexion a internet
    LOCAL_IP = ichecker.getLocalIP()
    if(network == False):
        enviarPorSerial("nonet")
        for i in range(0,5):
            gp.output(26,True)
            time.sleep(0.5)
            gp.output(26,False)
            time.sleep(0.5)
        gp.cleanup()
        print("Could not set up server")
    elif(network == True):
        gp.output(26,True)
        enviarPorSerial("init")
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
                PUERTO = int(puerto)
                time.sleep(2)
                setupServer()
                listen()
                break
            time.sleep(0.1)

def enviarPorSerial(pData):
    global arduinoSerial
    arduinoSerial.write(pData)


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
        enviarPorSerial("ok");
        time.sleep(1.5)
        enviarPorSerial(str(LOCAL_IP))
    except:
        enviarPorSerial("failed");
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
        try:
            conn, addr = server.accept()
            NUSUARIOS+=1
            if(DEBUG== True):
                print("**User Connected**" + " User:" + str(NUSUARIOS))
            b= Thread(target=handleClient, args=(conn,addr))
            b.daemon = True
            b.start()
        except:
            enviarPorSerial("failed");
            
    server.close()
    if(DEBUG== True):
        print("- Server Closed -")

'''--------------------------------------------------------------------------
                    thread para cada cliente
 ----------------------------------------------------------------------------'''
def handleClient(conn,addr):
    global NUSUARIOS,arduinoSerial
    #conn.send("*Connected*\n")
    kill = False
    while True:
        data = conn.recv(1024)
        data = data.decode("utf-8")
        data = data.split('\n')
        try:
            if(data[0] != ""):
                if(data[0] == "exit"):
                    if(DEBUG== True):
                        print("usuario desconectado")
                    conn.close()
                    break
                elif(data[0] == "kill"):
                    conn.close()
                    kill = True
                    break
                message = "Received: " + data[0] + '\n'
                conn.send(message)
                arduinoSerial.write(data[0])
                if(DEBUG== True):
                    print("mensaje recibido: "+data[0]+".   de :"+str(addr[0]))
        except:
            pass
    if(kill == True):
        print("Kill Program")
        server.close()
        os._exit(0
    NUSUARIOS-=1

loadXMLParameters()
<<<<<<< HEAD
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
=======
iniControladorSerial()
>>>>>>> 866568cfdcd20ce0bb36978a75be8f930b2e88d3
