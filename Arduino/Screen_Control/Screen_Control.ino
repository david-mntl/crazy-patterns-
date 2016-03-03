#include <stdint.h>
#include <TFTv2.h>
#include <SPI.h>
#include <TouchScreen.h> 

#define YP A2
#define XM A1
#define YM 14
#define XP 17

#define TS_MINX 116*2
#define TS_MAXX 890*2
#define TS_MINY 83*2
#define TS_MAXY 913*2

TouchScreen ts = TouchScreen(XP, YP, XM, YM);

const char* numbers[12] = {"1","2","3","<","4","5","6",">","7","8","9","0"};
const char* letters[27] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
const char* letvis[12] = {"ABC","DEF","GHI","<","JKL","MNO","PQR",">","STU","VWX","YZ"};
const char* PORT[6] = {"_","_","_","_","_","_"};
const char* HOST[8] = {"_","_","_","_","_","_","_","_"};
byte PORT_IND = 0;
byte HOST_IND = 0;
byte LETT_IND = 0;
byte configParam = 0;
byte keyMode = 0;
byte screen = 0;


void setup(){
    Serial.begin(9600);
    Tft.TFTinit();
    pinMode(2,OUTPUT);
    welcomeScreen();
}
void loop(){
  Point p = ts.getPoint();                    //Obtener punto presionado
  p.x = map(p.x, TS_MINX, TS_MAXX, 0, 240);   //Map Px al tamaño de pantalla
  p.y = map(p.y, TS_MINY, TS_MAXY, 0, 320);   //Map Py al tamaño de pantalla
  if (p.z > __PRESURE) {                      //Si la presión a la pantalla es minima
    //Tecla Back en cualquier pantalla
    if(screen!=0 && ((p.x >= 175 && p.x <= 235)&&(p.y >=275 && p.y <=315))){ 
      screen=0;
      configParam=0;
      beep(50);
      welcomeScreen();
    }
    else{
      if(screen == 0){
        if((p.x >= 5 && p.x <= 78)&&(p.y >=240 && p.y <=290)){
          screen=1;
          beep(50);
          playScreen();
        }
        else if((p.x >= 83 && p.x <= 156)&&(p.y >=240 && p.y <=290)){
          screen=2;
          beep(50);
          configScreen();
        }
        else if((p.x >= 161 && p.x <= 234)&&(p.y >=240 && p.y <=290)){
          screen=3;
          beep(50);
          aboutScreen();
        }
      }
      else{
        manageInputsAtScreens(p.x,p.y);
      }
    }
  }
}
void manageInputsAtScreens(int x, int y){
  if(screen == 1){     //PLAY SCREEN
    
  }
  else if(screen == 2){//CONFIG SCREEN
    if((x >= 18 && x <= 70)&&(y >=10 && y <=20)){ //SELECT PORT
      beep(50);
      configParam=1;
      Tft.fillScreen(0, 16, 0, 42,BLACK);
      Tft.drawString((char*)">",3,14,1,GREEN);
      delay(40);
    }
    else if((x >= 18 && x <= 70)&&(y >=30 && y <=40)){ //SELECT HOST
      beep(50);
      configParam=2;
      Tft.fillScreen(0, 16, 0, 42,BLACK);
      Tft.drawString((char*)">",3,34,1,GREEN);
      delay(40);
    }
    else if((x >= 185 && x <= 227)&&(y >=168 && y <=210)){ //CHANGE KEYBOARD MODE
      beep(50);
      if(keyMode == 0){
        keyMode=1;
        Tft.fillScreen(186, 226, 169, 209,BLACK);
        Tft.drawString((char*)"01",194,182,2,WHITE);
        drawKeyboard(1);
      }
      else if(keyMode == 1){
        keyMode=0;
        Tft.fillScreen(186, 226, 169, 209,BLACK);
        Tft.drawString((char*)"AB",194,182,2,WHITE);
        drawKeyboard(0);
      }
      
      delay(40);
    }
    else{
      manageKeyBoardPress(x,y);
    }
    
  }
  else if(screen == 3){//ABOUT SCREEN
    
  }
}
void welcomeScreen(){
  Tft.fillScreen(0, 240, 0, 320,BLACK);
  Tft.drawString((char*)"Crazy",10,20,2,RED);
  Tft.drawString((char*)"Patterns",50,50,3,WHITE);
  Tft.drawString((char*)"Version 1.0",1,305,1,WHITE);
  Tft.fillRectangle(5, 240, 73,50,RED);
  Tft.fillRectangle(83, 240, 73,50,GREEN);
  Tft.fillRectangle(161, 240, 73,50,BLUE);
  Tft.drawString((char*)"Play",18,258,2,WHITE);
  Tft.drawString((char*)"Config",83,258,2,WHITE);
  Tft.drawString((char*)"About",168,258,2,WHITE);
}
void playScreen(){
  Tft.fillScreen(0, 240, 0, 320,BLACK);
  Tft.fillRectangle(175, 275, 60,40,RED);
  Tft.drawString((char*)"Back",182,288,2,WHITE);
}
void configScreen(){
  Tft.fillScreen(0, 240, 0, 320,BLACK);
  Tft.fillRectangle(175, 275, 60,40,RED);
  Tft.drawString((char*)"Back",182,288,2,WHITE);
  Tft.drawString((char*)"Port:",18,10,2,RED);
  Tft.drawString((char*)"Host:",18,30,2,RED);
  Tft.drawRectangle(185, 168, 42,42,GREEN);
  Tft.drawString((char*)"AB",194,182,2,WHITE);
  drawKeyboard(0);
  
}
void aboutScreen(){
  Tft.fillScreen(0, 240, 0, 320,BLACK);
  Tft.fillRectangle(175, 275, 60,40,RED);
  Tft.drawString((char*)"Back",182,288,2,WHITE);
  Tft.drawString((char*)"Crazy",10,20,2,RED);
  Tft.drawString((char*)"Patterns",50,50,3,WHITE);
  Tft.drawString((char*)"Developers:",5,78,1,BLUE);
  Tft.drawString((char*)"Abraham Arias",5,90,2,WHITE);
  Tft.drawString((char*)"David Monestel",5,110,2,WHITE);
  Tft.drawString((char*)"Fabian Solano",5,130,2,WHITE);
  Tft.drawString((char*)"Lenin Torres",5,150,2,WHITE);
}
void drawKeyboard(byte mode){
  Tft.fillScreen(0, 170, 168, 320,BLACK);
  drawPortAndHost();
  if(mode == 0){       //DIBUJA TECLADO NUMERICO
    for(int i=0;i<4;i++){ 
      for(int j=0;j<3;j++){
        Tft.drawRectangle(1+(42*i), 168+(52*j), 42,42,YELLOW);
        Tft.drawString((char*)numbers[i+(4*j)],15+(42*i),182+(52*j),2,WHITE);
        }
    }
  }
  else if(mode == 1){   //DIBUJA TECLADO LETRAS
    for(int i=0;i<4;i++){ 
      for(int j=0;j<3;j++){
        Tft.drawRectangle(1+(42*i), 168+(52*j), 42,42,YELLOW);
        byte z = i+(4*j);
        if((z==3) || (z==7)){
          Tft.drawString((char*)numbers[i+(4*j)],15+(42*i),182+(52*j),2,WHITE);
        }
        else{
          Tft.drawString((char*)letvis[i+(4*j)],3+(42*i),182+(52*j),2,WHITE);
        }
      }
    }
  }
}
void manageKeyBoardPress(int x,int y){
  for(int i=0;i<4;i++){ 
    for(int j=0;j<3;j++){
      if(((x >= (1+(42*i))) && (x <= (43+(42*i)))) && ((y >= (168+(52*j))) && (y <= (210+(52*j))))){
        byte z = i+(4*j);
        if(configParam==1 && keyMode==0){ //DIGITA EL PUERTO CON TECLADO NUMERICO
          if(z==3){
            PORT[PORT_IND-1]="_";
            if(PORT_IND>0)
              PORT_IND--;
            goto EndB;
          }
          else if(z==7){
            if(PORT_IND<6){
              PORT[PORT_IND]="0";
              PORT_IND++;
            }
            goto EndB;
          }
          else{
            if(PORT_IND<6){
              PORT[PORT_IND]=numbers[i+(4*j)];
              PORT_IND++;
            }
            goto End;
          }
        }
        else if(configParam==2 && keyMode==1){ //DIGITA EL NOMBRE DEL HOST
          if(z==3){
            HOST[HOST_IND]="_";
            if(HOST_IND>0)
              HOST_IND--;
            goto EndB;
          }
          else if(z==7){
            if(HOST_IND<6){
              HOST_IND++;
            }
            LETT_IND = 0;
            goto EndB;
          }
          else{
            Serial.println(((LETT_IND)+3*z)-3*j);
            if(LETT_IND<2){
              HOST[HOST_IND]=letters[((LETT_IND)+3*z)-3*j];
              LETT_IND++;
            }
            else if(LETT_IND>=2){
              HOST[HOST_IND]=letters[((LETT_IND)+3*z)-3*j];
              LETT_IND=0;
            }
            goto End;
          }
        }
      }
    }
  }
  End:
    drawPortAndHost();
    delay(100);
    return;
  EndB:
    beep(50);
    drawPortAndHost();
    delay(100);
    return;
}
void drawPortAndHost(){
  Tft.fillScreen(80, 200, 5, 50,BLACK);
  for(int i=0;i<6;i++){
    if(PORT[i] != "_"){
      Tft.drawString((char*)PORT[i],80+(15*i),10,2,WHITE);
    }
  }
  for(int j=0;j<8;j++){
    if(HOST[j] != "_"){
      Tft.drawString((char*)HOST[j],80+(15*j),30,2,WHITE);
    }
  }
}
//***********************************
//MISCELANEUS METHODS
//***********************************
void beep(byte ms){
  digitalWrite(2,HIGH);
  delay(ms);
  digitalWrite(2,LOW);
  delay(ms);
}

