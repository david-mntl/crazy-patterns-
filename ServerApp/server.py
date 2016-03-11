'''--------------------------------------------------------------------------
Fabian
Lenin
David
Abraham
 ----------------------------------------------------------------------------'''
import socket
from _ast import arguments
from threading import Thread
import os
import serial
import time
import datetime
from xml.dom import minidom
import internetChecker as ichecker #Importa el .py encargado de verificar la conexion a internet
#import RPi.GPIO as gp #Importa biblioteca para el uso de puertos GPIO

import simplejson
import json

'''--------------------------------------------------------------------------
                    Variables globales
 ----------------------------------------------------------------------------'''
global arduinoSerial, NUSUARIOS, DEBUG, PUERTO, PUERTOSERIAL, HOST, LISTADEPARTIDAS


'''--------------------------------------------------------------------------
                    Configuracion de XML y Json
 ----------------------------------------------------------------------------'''
def loadXMLParameters():
    global DEBUG,PUERTO, NUSUARIOS, PUERTOSERIAL, arduinoSerial,LISTADEPARTIDAS
    LISTADEPARTIDAS =[]
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


def putJson(filename, nombre, ER, intentos, fecha):
        with open(filename, "a") as outfile:json.dump({'nombre':nombre, 'ER':ER, 'intentos':intentos, 'fecha':fecha}, outfile, indent=4, skipkeys=True, sort_keys=True)

def getJson(filename):
    with open(filename) as json_file:data = json.load(json_file)
    return data

'''--------------------------------------------------------------------------
                    Configuracion inicial del socket servidor
 ----------------------------------------------------------------------------'''
def iniControladorSerial():
    global PUERTO, PUERTOSERIAL, arduinoSerial, HOST,DEBUG,LOCAL_IP
    #gp.setmode(gp.BCM)               #Iniciar puertos GPIO
    #gp.setwarnings(False)
    #gp.setup(19,gp.OUT)              #GPIO 19 como output (Luz de inicio)
    #gp.setup(26,gp.OUT)              #GPIO 26 como output (Luz de Internet)
    #gp.output(19,False)
    #gp.output(26,False)


    try:

        arduinoSerial = serial.Serial(PUERTOSERIAL, 9600)
        if(DEBUG == True): 
            print("-Initializing serial comunication-")
    except:
        if(DEBUG == True):
            print("Could not find serial port" + str(PUERTOSERIAL))

    #gp.output(19,True)
    network = ichecker.checkNetwork() #Verificar si hay conexion a internet
    LOCAL_IP = ichecker.getLocalIP()
    if(network == False):
        enviarPorSerial("nonet")
        for i in range(0,5):
            #gp.output(26,True)
            #time.sleep(0.5)
            #gp.output(26,False)
            time.sleep(0.5)
        #gp.cleanup()
        print("Could not set up server")
    elif(network == True):
        #gp.output(26,True)
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
def verificarComando(command, conn):
    global arduinoSerial, LISTADEPARTIDAS
    resp=True
    ans = command.split("#")
    if(ans[0]=="crear"): #crear#nombre#ER#fecha#intentos#
        timenow= str(datetime.datetime.now().day)+"/"+str(datetime.datetime.now().month)+"/"+str(datetime.datetime.now().year)
        LISTADEPARTIDAS.append([ans[1],ans[2],timenow,"0"])
        enviarPorSerial("Partida creada")
        conn.send("Partida creada")
        print("Partida: "+ans[1]+" creada correctamente")
        resp=False

    if(ans[0]=="verp"):#pidio partidas
        text=""
        for i in LISTADEPARTIDAS:
            text = text+str(i)+"%"
        conn.send(text)
        enviarPorSerial("Alguien quiere jugar")
        resp=False

    if(ans[0]=="jugar"):#
        resp=False
        enviarPorSerial("Alguien juega")
    return resp

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
                else:
                    if(data[0] != ""):
                        if(verificarComando(data[0], conn)):
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
        os._exit(0)
    NUSUARIOS-=1


loadXMLParameters()
iniControladorSerial()