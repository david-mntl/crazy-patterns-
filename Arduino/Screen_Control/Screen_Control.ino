#include <stdint.h>
#include <TFTv2.h>
#include <SPI.h>

void setup()
{
    TFT_BL_ON;      //luz Trasera
    Tft.TFTinit();  //Iniciar Bibliotecas
    
    Tft.drawString("Hello",10,100,3,CYAN);       // draw string: "hello", (0, 180), size: 3, color: CYAN
    Tft.drawString("World",60,140,4,WHITE);    // draw string: "world!!", (80, 230), size: 4, color: WHITE

    Tft.drawString("By: Fabian Solano",20, 260,2,GREEN);
    
    String a = 'A';
}
void loop()
{
  
}
