//Firebase libraries
#include <Firebase.h>
#include <FirebaseArduino.h>
#include <FirebaseError.h>      

// needed for firebase
#include <ArduinoJson.h>        

//wifi lib
#include <ESP8266WiFi.h>      

//oled lib
#include <Adafruit_SSD1306.h> 

#define OLED_RESET LED_BUILTIN //4
Adafruit_SSD1306 display(OLED_RESET);


#define FIREBASE_HOST "smartglass2246.firebaseio.com"
#define FIREBASE_AUTH "" //YOUR DATABASE SECRET KEY HERe

const char* ssid="#######";
const char* password = "Zombie123456";
String text;

void setup() {
  
  pinMode(2,OUTPUT);
  digitalWrite(2,HIGH);

  Serial.begin(74880);
  delay(100);
  Serial.println();
  Serial.print("Wifi connecting to ");
  Serial.println( ssid );

  WiFi.begin(ssid,password);

  Serial.println();
  Serial.print("Connecting");

  while( WiFi.status() != WL_CONNECTED ){
      delay(500);
      Serial.print(".");        
  }

  digitalWrite( 2 , LOW);
  Serial.println();

  Serial.println("Wifi Connected Success!");
  Serial.print("NodeMCU IP Address : ");
  Serial.println(WiFi.localIP() );

  Serial.println("Wifi done");

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);//connect to firebase

  // by default, we'll generate the high voltage from the 3.3v line internally! (neat!)
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);  
  // init done
  
  display.display();
  delay(2000);

}

void loop() {
digitalWrite( 2 , LOW);
Serial.println(WiFi.localIP());
checkNotifications();
readSMS();

if (Firebase.failed()){ // Check for errors 
  Serial.print("setting /number failed:");
  Serial.println(Firebase.error());
  return;
}

delay(300);
digitalWrite( 2 , HIGH);
}

void checkNotifications(){
  if(Firebase.getInt("NotifIndicator")) {
    
    text = Firebase.getString("Notification");

    display.clearDisplay();
    display.setTextSize(2);
    display.setTextColor(WHITE);
    display.setCursor(0,0);
    display.println(text);
    display.display();
    Firebase.setInt("NotifIndicator",0);
  }  
}

void readSMS(){
  if(Firebase.getInt("SMIndicator")) {
    
    text = Firebase.getString("StoredMessage");

    display.clearDisplay();
    display.setTextSize(1);
    display.setTextColor(WHITE);
    display.setCursor(0,0);
    display.println(text);
    display.display();
    Firebase.setInt("SMIndicator",0);
  }

}
