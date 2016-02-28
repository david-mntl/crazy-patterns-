#include <stdint.h>
#include <TFTv2.h>
#include <SPI.h>

bool start;

void setup(){
    Serial.begin(9600);
    TFT_BL_ON;      //luz Trasera
    Tft.TFTinit();  //Iniciar Bibliotecas
    welcomeTxt();
    
    
    //Serial.println("Initialized");
    start = false;
}
void loop(){
    if(start == false){
      delay(2000);
      start = true;
    }

    if (Serial.available() > 0) {
      String inc = Serial.readString();
      char toScreenTxt[inc.length()+1];
      inc.toCharArray(toScreenTxt,inc.length()+1);
      Serial.print("Msg: ");
      Serial.println(inc);
      clearSection(0,239,245,319,BLACK);
      Tft.drawString(toScreenTxt,20,250,2,YELLOW);
    }
    delay(10); 
}

void welcomeTxt(){
  Tft.drawString("Hello",10,20,3,BLUE);
  Tft.drawString("World",60,60,4,WHITE);
  Tft.drawString("By: Fabian Solano",20, 110,2,GREEN);
  Tft.drawString("Message:",20,220,2,YELLOW);
}
void clearSection(int x1,int y1, int x2, int y2,int color){
  Tft.fillScreen(x1,y1,x2,y2,color);
}


