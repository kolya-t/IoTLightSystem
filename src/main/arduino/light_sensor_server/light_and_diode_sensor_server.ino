#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char* ssid = "Redmi";
const char* password = "qwerty123";

ESP8266WebServer server(80);

#define LED D3

void setup() {
  pinMode(LED, OUTPUT);
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  if (MDNS.begin("esp8266")) {
    Serial.println("MDNS responder started");
  }

  server.on("/", handler);
  server.on("/on", handleOn);
  server.on("/off", handleOff);

  server.begin();
  Serial.println("HTTP server started");
}

void loop(void){
  server.handleClient();
}

void handler() {
  int value = analogRead(A0);
  server.send(200, "text/plain", String(value));
  Serial.println(value);
}

void handleOn() {
  if (server.method() != HTTP_POST) {
    return;
  }
  
  digitalWrite(LED, HIGH);
  server.send(200, "text/plain", "turned on!");
  Serial.println("ON");
}

void handleOff() {
  if (server.method() != HTTP_POST) {
    return;
  }
  
  digitalWrite(LED, LOW);
  server.send(200, "text/plain", "turned off!");
  Serial.println("OFF");
}
