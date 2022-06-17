# Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:
-> Install App on Device(android)
-> Select your local Address
-> Enter Ip Addresss to get local live data

![image](https://user-images.githubusercontent.com/81908427/174269727-dae3ad41-cdf0-4f23-9993-fb689e80c29e.png)

![image](https://user-images.githubusercontent.com/81908427/174269799-2c597440-1d90-4bf0-b7e9-6b3f061722c4.png)

![image](https://user-images.githubusercontent.com/81908427/174269854-a1d66e95-c098-49cf-90f5-56129edd3eb2.png)

A new Flutter project.

# For IOT
Things you need 
- ESP8266, DHT sensor, Bread Board, Jumper wire, A Arduino ide installed device
## Esp8266
![image](https://user-images.githubusercontent.com/81908427/174275756-c97f8e08-2801-4c04-97bf-128ec969fd23.png)
## Bread-board
![image](https://user-images.githubusercontent.com/81908427/174275851-d4e474ba-82ea-4763-ab27-4ef50b84a1c6.png)
## DHT
![image](https://user-images.githubusercontent.com/81908427/174276255-3aea5194-34bf-40fe-92e3-33df5e49a373.png)


## Steps
- Connect DHT sensor G-ground , Vcc- + power(0.3 v), Data to Pin 5(D1)
- Installing the DHT Library for ESP8266
To read from the DHT sensor, we’ll use the DHT library from Adafruit. To use this library you also need to install the Adafruit Unified Sensor library. Follow the next steps to install those libraries.

- 1. Open your Arduino IDE and go to Sketch > Include Library > Manage Libraries. The Library Manager should open 
- 2. Search for “DHT” on the Search box and install the DHT library from Adafruit.
![image](https://user-images.githubusercontent.com/81908427/174274062-3111a2f0-932f-4945-967a-9983b56e7c62.png)
- 3. After installing the DHT library from Adafruit, type “Adafruit Unified Sensor” in the search box. Scroll all the way down to find the library and install it
![image](https://user-images.githubusercontent.com/81908427/174274242-ced5b6f1-5174-480f-ba73-c51756d92717.png)
- 4. After installing the libraries, restart your Arduino IDE.
- 5. Now ESP8266 Asynchronous Web Server
 To build the web server we’ll use the ESPAsyncWebServer library that provides an easy way to build an asynchronous web server. Building an asynchronous web server has   several advantages. We recommend taking a quick look at the library documentation on its GitHub page.

Installing the ESPAsyncWebServer library
The ESPAsyncWebServer library is not available to install in the Arduino IDE Library Manager. So, you need to install it manually.

-> Follow the next steps to install the ESPAsyncWebServer library:

1) Click here to download the ESPAsyncWebServer library. You should have a .zip folder in your Downloads folder
2) Unzip the .zip folder and you should get ESPAsyncWebServer-master folder
3) Rename your folder from ESPAsyncWebServer-master to ESPAsyncWebServer
4) Move the ESPAsyncWebServer folder to your Arduino IDE installation libraries folder
5) Installing the ESPAsync TCP Library

-> The ESPAsyncWebServer library requires the ESPAsyncTCP library to work. Follow the next steps to install that library:

1) Click here to download the ESPAsyncTCP library. You should have a .zip folder in your Downloads folder
2) Unzip the .zip folder and you should get ESPAsyncTCP-master folder
3) Rename your folder from ESPAsyncTCP-master to ESPAsyncTCP
4) Move the ESPAsyncTCP folder to your Arduino IDE installation libraries folder
5) Finally, re-open your Arduino IDE

We’ll program the ESP8266 using Arduino IDE, so you must have the ESP8266 add-on installed in your Arduino IDE. If you haven’t, follow the next tutorial first:

-> Install the ESP8266 Board in Arduino IDE
1) Open ArduinoIDE, you need to set up the preferences by going to File and then click Preferences and then 
![image](https://user-images.githubusercontent.com/81908427/174276495-8d59f2ed-5ac7-4fdb-8f28-3b2594db5dcf.png)
2) paste "http://arduino.esp8266.com/versions/2.3.0/package_esp8266com_index.json" in the Additional Boards Manager URLs option, click ok.
![image](https://user-images.githubusercontent.com/81908427/174276528-82a4f691-d223-4e59-9bcd-3d5a6a479a03.png)

-> Open your Arduino IDE and copy the following code.
# code

  #include <DHT_U.h>

  #include <Arduino.h>

  #include <ESP8266WiFi.h>

  #include <Hash.h>

  #include <ESPAsyncTCP.h>

  #include <ESPAsyncWebServer.h>

  #include <Adafruit_Sensor.h>

  #include <DHT.h>

  const char* ssid =  "REPLACE_WITH_YOUR_SSID"; //"Wifi to you connect " "realme 3 Pro" , "OM PG_2GHZ", "Redmi Note 7 Pro";

  const char* password = "REPLACE_WITH_YOUR_PASSWORD"; //"wifi password", "12345678"

  #define DHTPIN 5

  #define DHTTYPE    DHT11

  DHT dht(DHTPIN, DHTTYPE);

  float t = 0.0;

  float h = 0.0;

  AsyncWebServer server(80);

  unsigned long previousMillis = 0;

  const long interval = 10000;  

  const char index_html[] PROGMEM = R"rawliteral(

  <!DOCTYPE HTML><html>
  
  <head>
  
    <meta name="viewport" content="width=device-width, initial-scale=1">
  
      <link rel="stylesheet" 
        
        href="https://use.fontawesome.com/releases/v5.7.2/css/all.css" 
      
        integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" 
        
        crossorigin="anonymous">
  
    <style>
    
      html {
    
      font-family: Arial;
    
      display: inline-block;
    
      margin: 0px auto;
    
      text-align: center;
    
      }
    
      h2 { font-size: 3.0rem; }
    
      p { font-size: 3.0rem; }
    
      .units { font-size: 1.2rem; }
    
      .dht-labels{
    
        font-size: 1.5rem;
    
        vertical-align:middle;
    
        padding-bottom: 15px;
    
      }
    
    </style>
  
  </head>
  
  <body>
  
    <h2>ESP8266 DHT Server</h2>
  
    <p>
    
      <i class="fas fa-thermometer-half" style="color:#059e8a;"></i> 
    
      <span class="dht-labels">Temperature</span> 
    
      <span id="temperature">%TEMPERATURE%</span>
    
      <sup class="units">&deg;C</sup>
    
    </p>
  
    <p>
    
      <i class="fas fa-tint" style="color:#00add6;"></i> 
    
      <span class="dht-labels">Humidity</span>
    
      <span id="humidity">%HUMIDITY%</span>
    
      <sup class="units">%</sup>
    
    </p>
  
  </body>
  
  <script>
  
  setInterval(function ( ) {
  
    var xhttp = new XMLHttpRequest();
  
    xhttp.onreadystatechange = function() {
  
      if (this.readyState == 4 && this.status == 200) {
  
        document.getElementById("temperature").innerHTML = this.responseText;
  
      }
  
    };
  
    xhttp.open("GET", "/temperature", true);
  
    xhttp.send();
  
  }, 10000 ) ;
  

  setInterval(function ( ) {
  
    var xhttp = new XMLHttpRequest();
  
    xhttp.onreadystatechange = function() {
  
      if (this.readyState == 4 && this.status == 200) {
  
        document.getElementById("humidity").innerHTML = this.responseText;
  
      }
  
    };
  
    xhttp.open("GET", "/humidity", true);
  
    xhttp.send();
  
  }, 10000 ) ;
  
  </script>
  
  </html>)rawliteral";


  // Replaces placeholder with DHT values

  String processor(const String& var){

    Serial.println(var);
  
    if(var == "TEMPERATURE"){
  
      return String(t);
    
    }
    else if(var == "HUMIDITY"){
  
      return String(h);
    
    }
  
    return String();
  
  }

  void setup(){

    Serial.begin(115200);
  
    dht.begin();

    WiFi.begin(ssid, password);
  
    Serial.println("Connecting to WiFi");
  
    while (WiFi.status() != WL_CONNECTED) {
  
      delay(1000);
    
      Serial.println(".");
    
    }
  
    Serial.println(WiFi.localIP());
  
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
  
      request->send_P(200, "text/html", index_html, processor);
    
    });
  
    server.on("/temperature", HTTP_GET, [](AsyncWebServerRequest *request){
  
      request->send_P(200, "text/plain", String(t).c_str());
    
    });
  
    server.on("/humidity", HTTP_GET, [](AsyncWebServerRequest *request){
  
      request->send_P(200, "text/plain", String(h).c_str());
    
    });
  
    server.begin();
  
  }
 
  void loop(){  

    unsigned long currentMillis = millis();
  
    if (currentMillis - previousMillis >= interval) {
  
      previousMillis = currentMillis;
    
      float newT = dht.readTemperature();
    
      if (isnan(newT)) {
    
        Serial.println("Failed to read from DHT sensor!");
      
      }
    
      else {
    
        t = newT;
      
        Serial.println(t);
      
      }
    
      float newH = dht.readHumidity(); 
    
      if (isnan(newH)) {
    
        Serial.println("Failed to read from DHT sensor!");
      
      }
    
      else {
    
        h = newH;
      
        Serial.println(h);
      
      }
    
    }
  
  }

-> Uploading the code
After modifying the sketch with the necessary changes, if needed, upload the code to your ESP8266 (if you can’t upload code to your ESP8266, read this troubleshooting guide).

Make sure you have the right board and COM port select. Go to Tools> Board and select the ESP8266 model you’re using. In our case, we’re using the ESP8266 12-E NodeMCU Kit.

![image](https://user-images.githubusercontent.com/81908427/174277219-4b8aa7b6-ff1d-4cfb-bcde-43611d17e99b.png)

Go to Tools > Port and select the COM port the ESP8266 is connected to.
![image](https://user-images.githubusercontent.com/81908427/174278058-c699dc36-4be2-4b99-bc2d-27ccbb3c2f54.png)

-> ESP8266 IP Address
After uploading the code, open the Serial Monitor at a baud rate of 115200. Press the ESP8266 reset button. The ESP8266 IP address will be printed in the serial monitor as shown in the following figure.
![image](https://user-images.githubusercontent.com/81908427/174278309-4954ed49-6f7f-42e5-8fec-3e3c8decb5c5.png)



