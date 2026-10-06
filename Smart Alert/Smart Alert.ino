#include "ThingSpeak.h"
#include "WiFiEsp.h"
#include "secrets.h"
#include "ultra.h"
char ssid[] = SECRET_SSID;   // your network SSID (name) 
char pass[] = SECRET_PASS;   // your network password
int keyIndex = 0;            // your network key Index number (needed only for WEP)
WiFiEspClient  client;
// Emulate Serial1 on pins 6/7 if not present
#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(6, 7); // RX, TX
#define ESP_BAUDRATE  19200
#else 
#define ESP_BAUDRATE  115200
#endif
unsigned long myChannelNumber = SECRET_CH_ID;
const char * myWriteAPIKey = SECRET_WRITE_APIKEY;
String myStatus = "new entry";
int sensorToGroundCm = 0;
bool isCalibrated = false;
const int alarmLevelCm = 30;
const unsigned long uploadIntervalMs = 20000UL;
const int calibrationCountdownSeconds = 5;

void setup() {
  isCalibrated = false;
  pinMode(8, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(2, INPUT);
  pinMode(13, OUTPUT);
  pinMode(9, OUTPUT); // Sets the trigPin as an Output
  pinMode(10, INPUT); // Sets the echoPin as an Input
  digitalWrite(11,0);
  digitalWrite(8,1);
  //Initialize serial and wait for port to open
  Serial.begin(115200);  // Initialize serial
  
  // initialize serial for ESP module  
  Serial.print("Setting ESP8266 baudrate to ");
  Serial.print(ESP_BAUDRATE);
  Serial.println("...");
  Serial1.begin(ESP_BAUDRATE);
  //pinMode(btnpin , INPUT_PULLUP);
  //attachInterrupt(btnpin,pin_cali,HIGH);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo native USB port only
  }

  Serial.print("Searching for ESP8266..."); 
  // initialize ESP module
  WiFi.init(&Serial1);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }
  Serial.println("found it!");
  Serial.println("Waiting For Calibration");
  for (int i = 0; i < calibrationCountdownSeconds; i++) {
  delay(1000);
  Serial.print("Calibration in ");
  Serial.println(calibrationCountdownSeconds - i);
  }
  digitalWrite(13,1);
  delay(800);
  pin_cali();
  delay(500);
  digitalWrite(13,LOW); 
  ThingSpeak.begin(client);  // Initialize ThingSpeak
}

void loop() {
  if (isCalibrated) {
  // Connect or reconnect to WiFi
  if(WiFi.status() != WL_CONNECTED){
    Serial.println("Attempting to connect to WiFi...");
    while (WiFi.status() != WL_CONNECTED) {
      WiFi.begin(ssid, pass);  // Connect to WPA/WPA2 network. Change this line if using open or WEP network
      Serial.print(".");
      delay(2000);     
    } 
    Serial.println("\nConnected.");
  }
  //calling reader function to check current water level
  const int sensorToWaterCm = reader();
  if (sensorToWaterCm < 0) {
    Serial.println("No ultrasonic echo received; skipping upload.");
    delay(uploadIntervalMs);
    return;
  }

  // The sensor measures clearance; subtract it from the calibrated ground distance.
  const int waterLevelCm = max(0, sensorToGroundCm - sensorToWaterCm);
  ThingSpeak.setField(1, waterLevelCm);
  
  // figure out the status message
  // set the status
  myStatus = String(waterLevelCm);
  Serial.print(waterLevelCm);
  delay(1000);
  if (waterLevelCm >= alarmLevelCm)
    alarm();
  ThingSpeak.setStatus(myStatus);
  // write to the ThingSpeak channel
  int x = ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);
  if(x == 200){
    Serial.println("Channel update successful.");
  }
  else{
    Serial.println("Problem updating channel. HTTP error code " + String(x));
  }
  
  delay(uploadIntervalMs); // ThingSpeak public channels accept updates no faster than every 15 seconds.
}
}
void pin_cali(){
    const int measuredDistance = reader();
    if (measuredDistance < 0) {
      Serial.println("Calibration failed: no ultrasonic echo received.");
      return;
    }
    sensorToGroundCm = measuredDistance;
    isCalibrated = true;
    Serial.print("Calibrated sensor-to-ground distance: ");
    Serial.print(sensorToGroundCm);
    Serial.println(" cm");
}
void alarm(){
  for(int i=0;i<20;i++){
      digitalWrite(13,HIGH);
      delay(200);
      digitalWrite(13,LOW);
      delay(200);
  }
}
// This function attempts to set the ESP8266 baudrate. Boards with additional hardware serial ports
// can use 115200, otherwise software serial is limited to 19200.
/*void setEspBaudRate(unsigned long baudrate){
  long rates[6] = {115200,74880,57600,38400,19200,9600};
*/
/*
  for(int i = 0; i < 6; i++){
    Serial1.begin(rates[i]);
    delay(100);
    Serial1.print("AT+UART_DEF=");
    Serial1.print(baudrate);
    Serial1.print(",8,1,0,0\r\n");
    delay(100);  
  }
    
}*/
