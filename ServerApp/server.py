'''--------------------------------------------------------------------------
Fabian
Lenin
David
Abraham
 ----------------------------------------------------------------------------'''
import socket
from threading import Thread
from twisted.protocols.dict import parseParam
from xml.dom import minidom
import os


'''--------------------------------------------------------------------------
                    Variables globales
 ----------------------------------------------------------------------------'''
global NUSUARIOS, DEBUG, PUERTO



'''--------------------------------------------------------------------------
                    Configuracion de XML
 ----------------------------------------------------------------------------'''
def loadXMLParameters():
    global DEBUG,PUERTO, NUSUARIOS
    ruta=os.getcwd()
    rutaFinal=str(ruta)+"/configs.xml"
    xmlDoc = minidom.parse(rutaFinal)


    pDebug = xmlDoc.getElementsByTagName('DEBUG')
    pPuerto = xmlDoc.getElementsByTagName('PUERTO')
    pNusuarios = xmlDoc.getElementsByTagName('NUSUARIOS')

    load_DEBUG=pDebug[0].attributes['value'].value
    PUERTO=int(pPuerto[0].attributes['value'].value)
    NUSUARIOS = 0

    if(load_DEBUG == "true"):   #Se define vDEBUG
        DEBUG = True
    else:
        DEBUG = False



'''--------------------------------------------------------------------------
                        Escribir XML
 ----------------------------------------------------------------------------'''
def escribir():
    print("k")





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
setupServer()
listen()



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