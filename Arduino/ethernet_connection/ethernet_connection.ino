/*
  Web Server Demo
  thrown together by Randy Sarafan
 
 Allows you to turn on and off an LED by entering different urls.
 
 To turn it on:
 http://your-IP-address/$1
 
 To turn it off:
 http://your-IP-address/$2
 
 Circuit:
 * Ethernet shield attached to pins 10, 11, 12, 13
 * Connect an LED to pin D2 and put it in series with a 220 ohm resistor to ground
 
 Based almost entirely upon Web Server by Tom Igoe and David Mellis
 
 Edit history: 
 created 18 Dec 2009
 by David A. Mellis
 modified 4 Sep 2010
 by Tom Igoe
 
 */

#include <SPI.h>
#include <Ethernet.h>
#include <string.h>

boolean incoming = 0;

// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDA, 0x02 };
IPAddress ip(192,168,1,103); //<<< ENTER YOUR IP ADDRESS HERE!!!

// Initialize the Ethernet server library
// with the IP address and port you want to use 
// (port 80 is default for HTTP):
EthernetServer server(2222);

void setup()
{
  pinMode(8, OUTPUT);

  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip);
  server.begin();
  Serial.begin(9600);
}

void loop(){
  // listen for incoming clients
  EthernetClient client = server.available();
  if (client) {
    String newMsg = "";
    while(client.connected()){
     if(client.available()){
      char c = client.read();
      if( c == '\n'){
       break; 
      }
      else{
        newMsg += c;
      }
     } 
    }
    Serial.println(newMsg);
    delay(1);
    client.stop();
  }
}

void manageInput(String str){
  String slash1;
  String slash2;
  String slash3;
  String space2;
   
  slash1 = strstr(str, "/") + 1; // Look for first slash
  slash2 = strstr(slash1, "/") + 1; // second slash
  slash3 = strstr(slash2, "/") + 1; // third slash
  space2 = strstr(slash2, " ") + 1; // space after second slash (in case there is no third slash)
  if (slash3 > space2) slash3=slash2;
  
  PrintString("slash1",slash1);
  PrintString("slash2",slash2);
  PrintString("slash3",slash3);
  PrintString("space2",space2);
   
  // strncpy does not automatically add terminating zero, but strncat does! So start with blank string and concatenate.
  cmd[0] = 0;
  param1[0] = 0;
  param2[0] = 0;
  strncat(cmd, slash1, slash2-slash1-1);
  strncat(param1, slash2, slash3-slash2-1);
  strncat(param2, slash3, space2-slash3-1);
   
  PrintString("cmd",cmd);
  PrintString("param1",param1);
  PrintString("param2",param2);
}

void PrintString(char* label, char* str){
 Serial.print(label);
 Serial.print("=");
 Serial.println(str);
}

