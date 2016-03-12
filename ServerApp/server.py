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
global arduinoSerial, NUSUARIOS, DEBUG, PUERTO, PUERTOSERIAL, HOST, LISTADEPARTIDAS, LISTADEPARTIDASGANADAS


'''--------------------------------------------------------------------------
                    Configuracion de XML y Json
 ----------------------------------------------------------------------------'''
def loadXMLParameters():
    global DEBUG,PUERTO, NUSUARIOS, PUERTOSERIAL, arduinoSerial,LISTADEPARTIDAS,LISTADEPARTIDASGANADAS
    LISTADEPARTIDAS =[]
    LISTADEPARTIDASGANADAS =[]
    ruta=os.getcwd()
    rutaFinal=str(ruta)+"/configs.xml"
    xmlDocParametrosIni = minidom.parse(rutaFinal)


    pDebug = xmlDocParametrosIni.getElementsByTagName('DEBUG')
    pPuerto = xmlDocParametrosIni.getElementsByTagName('PUERTO')
    pPuertoSerial = xmlDocParametrosIni.getElementsByTagName('PUERTOSERIAL')

    load_DEBUG=pDebug[0].attributes['value'].value
    PUERTOSERIAL = str(pPuertoSerial[0].attributes['value'].value)
    PUERTO=int(pPuerto[0].attributes['value'].value)
    #PUERTO = 0
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
        #enviarPorSerial("nonet")
        for i in range(0,5):
            #gp.output(26,True)
            #time.sleep(0.5)
            #gp.output(26,False)
            time.sleep(0.5)
        #gp.cleanup()
        print("Could not set up server")
    elif(network == True):
        #gp.output(26,True)
        #enviarPorSerial("init")
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
        #enviarPorSerial("ok");
        time.sleep(1.5)
        #enviarPorSerial(str(LOCAL_IP))
    except:
        #enviarPorSerial("failed");
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
            #print("1")
            NUSUARIOS+=1
            #print("2")
            if(DEBUG== True):
                print("**User Connected**" + " User:" + str(NUSUARIOS))
            b= Thread(target=handleClient, args=(conn,addr))
            b.daemon = True
            b.start()
        except:
            print("except")
            #enviarPorSerial("failed");
            
    server.close()
    if(DEBUG== True):
        print("- Server Closed -")

'''--------------------------------------------------------------------------
                    thread para cada cliente
 ----------------------------------------------------------------------------'''
def verificarComando(command, conn):
    global LISTADEPARTIDAS,LISTADEPARTIDASGANADAS
    #global arduinoSerial
    resp=True
    commSplit = command.split("#")
    if(commSplit[0]=="crear"): # crear#nombre#ER#L#       desde la app
        # crear#nombre#ER#fecha#intentos#L1#L2#L3#L4#L5     almacenado en la app
        timenow= str(datetime.datetime.now().day)+"/"+str(datetime.datetime.now().month)+"/"+str(datetime.datetime.now().year)
        LISTADEPARTIDAS.append([str(commSplit[1]),str(commSplit[2]),timenow,"0",str(commSplit[5]),str(commSplit[6]),str(commSplit[7]),str(commSplit[8]),str(commSplit[9]),"nadie"])
        #LISTADEPARTIDAS = [nombre, ER, fecha, intentos, L1,L2,L3,L4,L5,Ganador]
        #enviarPorSerial("Partida creada")
        conn.send("Partida creada")
        print("Partida: '"+commSplit[1]+"' creada correctamente")
        resp=False

    if(commSplit[0]=="verp"):# name#fecha#intentos%name2#fecha2#intentos2
        texttotal=""
        for juego in range(0,len(LISTADEPARTIDAS)):
            text=""
            for j in range(0,len(LISTADEPARTIDAS[juego])):
                text += str(LISTADEPARTIDAS[juego][j])+"#"
            texttotal+=text+"%";
        texttotal+="\n"
        conn.send(texttotal)
        #enviarPorSerial("Alguien quiere jugar")
        resp=False

    if(commSplit[0]=="jugar"):# jugar#name           es lo que recibe
        #   ER#ExpresionRegular#L1#L2#L3#L4#L4#ganador      es lo que responde a la app
        #LISTADEPARTIDAS = [nombre, ER, fecha, intentos, L1,L2,L3,L4,L5,ganador]

        for j in range(0,len(LISTADEPARTIDAS)):
            if(str(LISTADEPARTIDAS[j][0]) == str(commSplit[1])):    #Para encontrar la partida con el nombre buscado
                comandoResp ="ER#"+str(LISTADEPARTIDAS[j][1])+"#"+str(LISTADEPARTIDAS[j][4])+"#"+str(LISTADEPARTIDAS[j][5])+"#"+str(LISTADEPARTIDAS[j][6])+"#"+str(LISTADEPARTIDAS[j][7])+"#"+(LISTADEPARTIDAS[j][8])+"#"+(LISTADEPARTIDAS[j][9])+"#\n"
                conn.send(comandoResp)
        resp=False

    if(commSplit[0]=="perdio"):#  perdio#nombrePartida#
        #LISTADEPARTIDAS = [nombre, ER, fecha, intentos, L1,L2,L3,L4,L5,ganador]
        for j in range(0,len(LISTADEPARTIDAS)):
            if(str(LISTADEPARTIDAS[j][0]) == str(commSplit[1])):    #Para encontrar la partida con el nombre buscado
                cont = LISTADEPARTIDAS[j][3]
                cont= int(cont)
                cont+=1
                LISTADEPARTIDAS[j][3]=str(cont)

    if(commSplit[0]=="gano"):#  gano#nombrePartida#nombrejugador
        #LISTADEPARTIDAS = [nombre, ER, fecha, intentos, L1,L2,L3,L4,L5,ganador]
        print("1")
        for j in range(0,len(LISTADEPARTIDAS)):
            print("2")
            if(str(LISTADEPARTIDAS[j][0]) == str(commSplit[1])):    #Para encontrar la partida con el nombre buscado
                print("3")
                LISTADEPARTIDASGANADAS.append(LISTADEPARTIDAS[j])   #agregamos a la lista de ganadas
                print("4")
                LISTADEPARTIDAS.remove(LISTADEPARTIDAS[j])
                print("5")
        resp=False


    return resp

def handleClient(conn,addr):
    global NUSUARIOS#,arduinoSerial
    conn.send("gotconnected\n")
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
#iniControladorSerial()
setupServer()
listen()