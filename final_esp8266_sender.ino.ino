#include <SoftwareSerial.h>
#include "ESP8266.h"
#include "ESP8266Client.h"
#include "Timer.h"
#define WARN Serial.println

SoftwareSerial esp8266Serial = SoftwareSerial(10, 11);
ESP8266 wifi = ESP8266(esp8266Serial);
ESP8266Client ESPclient(wifi);
Timer t ;
String lightState[]={"klt","klf"};
void setup()
{
     pinMode(7, OUTPUT);

   // t.every(1000*60*60, updateWeather);  
    esp8266Serial.begin(9600);
    wifi.begin();
    wifi.setTimeout(1000);
    wifi.setMode(ESP8266_WIFI_STATION);
    wifi.joinAP("topnet5504", "OMSPWFSD");
    IPAddress ipAP = IPAddress(192, 168, 1, 202);
   wifi.setIP(ESP8266_WIFI_STATION, ipAP);
   wifi.setMultipleConnections(true);
   wifi.createServer(4444);
}
  bool ok=true;

void loop()
{
    unsigned int id;
    int length;
    int totalRead;
    char buffer[11] = {};

    if ((length = wifi.available()) > 0) {
      id = wifi.getId();
      totalRead = wifi.read(buffer, 10);

      if (length > 0) {   
     /*   if(ok)
          {
            digitalWrite(7,HIGH);
            ok=false;
          }
        else
        {
        digitalWrite(7,LOW);
        ok = true; 
        }*/
          //Message received
         String msg((char*)buffer);
          if ( msg.startsWith(lightState[0])  ){
            //turn on the lights
            digitalWrite(7, HIGH);

          }
          else if ( msg.startsWith(lightState[1]) ){
            //turn off the lights
            digitalWrite(7, LOW);
              
      }
    }
    }
}

String getStatus(bool status)
{
    if (status)
        return "OK";

    return "KO";
}

String getStatus(ESP8266CommandStatus status)
{
    switch (status) {
    case ESP8266_COMMAND_INVALID:
        return "INVALID";
        break;

    case ESP8266_COMMAND_TIMEOUT:
        return "TIMEOUT";
        break;

    case ESP8266_COMMAND_OK:
        return "OK";
        break;

    case ESP8266_COMMAND_NO_CHANGE:
        return "NO CHANGE";
        break;

    case ESP8266_COMMAND_ERROR:
        return "ERROR";
        break;

    case ESP8266_COMMAND_NO_LINK:
        return "NO LINK";
        break;

    case ESP8266_COMMAND_TOO_LONG:
        return "TOO LONG";
        break;

    case ESP8266_COMMAND_FAIL:
        return "FAIL";
        break;

    default:
        return "UNKNOWN COMMAND STATUS";
        break;
    }
}

String getRole(ESP8266Role role)
{
    switch (role) {
    case ESP8266_ROLE_CLIENT:
        return "CLIENT";
        break;

    case ESP8266_ROLE_SERVER:
        return "SERVER";
        break;

    default:
        return "UNKNOWN ROLE";
        break;
    }
}

String getProtocol(ESP8266Protocol protocol)
{
    switch (protocol) {
    case ESP8266_PROTOCOL_TCP:
        return "TCP";
        break;

    case ESP8266_PROTOCOL_UDP:
        return "UDP";
        break;

    default:
        return "UNKNOWN PROTOCOL";
        break;
    }
}

void ESPsendMessage(const char *msg,int id){
  char n[2];
  itoa(id,n,10);
  char buffer[20];
  itoa(strlen(msg),buffer,10);
                                               /* set up tcp */
  String command="AT+CIPSTART=";
  command+=n;
  command+=",\"TCP\",\"192.168.1.201\",4444\r\n";
  esp8266Serial.write(command.c_str());
   Serial.write(command.c_str());
      delay(10);
                                             /* send message */
  String temp="AT+CIPSEND=";
  temp+=n;
  temp+=",";
  temp+=buffer;
  temp+="\r\n";
  esp8266Serial.write(temp.c_str());
  delay(10);
  esp8266Serial.write(msg);
  delay(10);
                                            /* close tcp */
  command="AT+CIPCLOSE=";
  command+=n;
  command+="\r\n";
  esp8266Serial.write(command.c_str());
  delay(10);
}

void updateWeather(void){
  //code to read temperature

  //code to read humidity

  //code to send the values must be of type char*
}

