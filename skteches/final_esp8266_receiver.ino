#include <SoftwareSerial.h>
#include "ESP8266.h"
#include "ESP8266Client.h"
#include <SPI.h>
#include <Ethernet.h>
#include <IPStack.h>
#include <Countdown.h>
#include <MQTTClient.h>
#define MQTTCLIENT_QOS2 1
#define WARN Serial.println

byte ip[]={192,168,1,203};
int arrivedcount = 0;
MQTT::Message message;
SoftwareSerial esp8266Serial = SoftwareSerial(5, 6);
ESP8266 wifi = ESP8266(esp8266Serial);
ESP8266Client ESPclient(wifi);
EthernetClient c; 
IPStack ipstack(c);
MQTT::Client<IPStack, Countdown, 50, 1> client = MQTT::Client<IPStack, Countdown, 50, 1>(ipstack);
byte mac[] = { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55 }; 
const char* topic = "server";

void messageArrived(MQTT::MessageData& md)
{
  MQTT::Message &message = md.message;
  
  Serial.print("Message ");
  Serial.print(++arrivedcount);
  Serial.print(" arrived: qos ");
  Serial.print(message.qos);
  Serial.print(", retained ");
  Serial.print(message.retained);
  Serial.print(", dup ");
  Serial.print(message.dup);
  Serial.print(", packetid ");
  Serial.println(message.id);
  Serial.print("Payload ");
  Serial.println((char*)message.payload);
  ESPsendMessage((char*)message.payload,4);
}

void connect()
{
  char hostname[] = "192.168.1.100";
  int port = 1883;

  Serial.print("Connecting to ");
  Serial.print(hostname);
  Serial.print(":");
  Serial.println(port);
 
  int rc = ipstack.connect(hostname, port);
  if (rc != 1)
  {
    Serial.print("rc from TCP connect is ");
    Serial.println(rc);
  }
 
  Serial.println("MQTT connecting");
  MQTTPacket_connectData data = MQTTPacket_connectData_initializer;       
  data.MQTTVersion = 3;
  data.clientID.cstring = (char*)"arduino-sample";
  rc = client.connect(data);
  if (rc != 0)
  {
    Serial.print("rc from MQTT connect is ");
    Serial.println(rc);
  }
  Serial.println("MQTT connected");
 rc = client.subscribe("sensors", MQTT::QOS0, messageArrived);   
  if (rc != 0)
  {
    Serial.print("rc from MQTT subscribe is "); 
    Serial.println(rc);
  }
  Serial.println("MQTT subscribed");
}

void sendMessage(char *msg){
  message.qos = MQTT::QOS0;
  message.retained = false;
  message.dup = false;
  message.payload = (void*)msg;
  message.payloadlen = strlen(msg)+1;
  int rc = client.publish(topic, message);
}


void setup()
{
    Serial.begin(9600);

    // ESP8266
    esp8266Serial.begin(9600);
    wifi.begin();
    wifi.setTimeout(1000);

  
    // setWifiMode
    Serial.print("setWifiMode: ");
    Serial.println(getStatus(wifi.setMode(ESP8266_WIFI_STATION)));

    
    // joinAP
    Serial.print("joinAP: ");
    Serial.println(getStatus(wifi.joinAP("topnet5504", "OMSPWFSD")));

   

    // quitAP
    /*Serial.print("quitAP: ");
    Serial.println(getStatus(wifi.quitAP()));*/

  
    // setMultipleConnections
    Serial.print("setMultipleConnections: ");
    Serial.println(getStatus(wifi.setMultipleConnections(true)));

    Serial.println(getStatus(wifi.deleteServer()));
    // createServer
    Serial.print("createServer: ");
    Serial.println(getStatus(wifi.createServer(4444)));
    Ethernet.begin(mac,ip);
    connect();

}
void loop()
{

    // read data
    unsigned int id;
    int length;
    int totalRead;
    char buffer[11] = {};

      if (!client.isConnected())
    connect();
   else {
        arrivedcount = 0;
        while (arrivedcount == 0)
        {
          
    if ((length = wifi.available()) > 0) {
      id = wifi.getId();
      totalRead = wifi.read(buffer, 10);

      if (length > 0) {
        Serial.print("Received ");
        Serial.print(totalRead);
        Serial.print("/");
        Serial.print(length);
        Serial.print(" bytes from client ");
        Serial.print(id);
        Serial.print(": ");
        Serial.println(buffer);
        sendMessage((char*)buffer);
      //  Serial.println((char*)buffer);
       // ESPsendMessage((char*)buffer,4);
      }
    }
          Serial.println("Waiting for QoS 0 message");
          client.yield(1000);
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

void ESPsendMessage(char *msg,int id){
  char n[2];
  itoa(id,n,10);
  char buffer[20];
  itoa(strlen(msg),buffer,10);
                                               /* set tcp */
  String command="AT+CIPSTART=";
  command+=n;
  command+=",\"TCP\",\"192.168.1.202\",4444\r\n";
  esp8266Serial.write(command.c_str());
 Serial.write(command.c_str());
 Serial.write(msg);
  //  esp8266Serial.write("AT+CIPSTART=0,\"TCP\",\"192.168.1.100\",4444\r\n");
      delay(10);
                                             /* end tcp */
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
                                          /* close tcp */
}
