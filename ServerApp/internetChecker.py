#import RPi.GPIO as gp
import time
import socket
import urllib2
#import picamera

def checkNetwork():
    try:
        response = urllib2.urlopen("http://google.com",timeout=1)
        return True
    except urllib2.URLError as err:
        try:
            response2 = urllib2.urlopen("http://74.125.224.72",timeout=1)
            return True
        except urllib2.URLError as err:
            return False

def getLocalIP():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8",80))
    return s.getsockname()[0]

def startUp():
    # gp.setmode(gp.BCM)
    #gp.setup(19,gp.OUT)
    #gp.setup(26,gp.OUT)
    
    print("Initialized")
    if(checkNetwork() == True):
        print("Got network connection")
       # gp.output(19,True)
    else:
        print("No network")
    
    
    itera=0
    while itera < 5:
        #gp.output(26,True)
        time.sleep(1)
       # gp.output(26,False)
        time.sleep(1)
        itera+=1
        print(itera)
        
    print("--Finished execution--")
    print(getLocalIP())
    #gp.cleanup()

#startUp()
