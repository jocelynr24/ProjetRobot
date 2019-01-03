#define PORT_SPEED_M1 5 
#define PORT_SPEED_M2 6 
#define PORT_DIRECTION_M1 4 
#define PORT_DIRECTION_M2 7
#define FORWARD 1 
#define BACKWARD 0



// define which serial is used by the bluetooth adapter
#define BLUETOOTH Serial1
#include <string.h>

//initialisation des pins pour l'ultrason 4k
int URPWM = 11; // PWM Output 0－25000US，Every 50US represent 1cm
int URTRIG=10; // PWM trigger pin
long Distance=0;
uint8_t EnPwmCmd[4]={0x44,0x02,0xbb,0x01};    // distance measure command


//initialisation des broches nécéssaires aux néons
const int rouge = 2;
const int vert = 3;
const int bleu = 8;

char c = '\0';

//variables nécéssaires à la copie de la variable cOctet pour les fonctions néons, camera, moteurs et phares
char cAllumer;
char cCamera;
char cPhares;
bool allumer =0;
char cVitesse;
int iVitesse=0;
#define TAILLE_BUF 10


//tableau contenant la distance en cm dans un string
char buf[10];



void setup()

{
//initialisation des sorties de l'arduino
initialisationSorties();

//initialisation du pwm pour le capteur ultrason
PWM_Mode_Setup(); 

//Initialisation of bluetooth adapter
BLUETOOTH.begin(9600); 

//Initialisation of serial for debug
Serial.begin(115200);
}

void loop() {
int iInfraAR, iInfraAG, iInfraAD =0;
char cOctet; //variable qui va recevoir des octets pour la commande du robot

//on recoit des informations des 3 capteurs
iInfraAR = analogRead(3);
iInfraAD = analogRead(2);
iInfraAG = analogRead(1);




//RECEPTION DES OCTETS DE DONNEES PROVENANT D'ANDROID
//Envoi d'une trame en fonction des capteurs infrarouges
  // number of characters received in the frame
  static int iBclReception = 0;
  // counter sent to the phone
  static int iBcl=0;

  static char strTrame[TAILLE_BUF]="_________";
    if(BLUETOOTH.available() > 0)
    {
    strTrame[iBclReception] = BLUETOOTH.read(); //the frame is read character per character (not the complete frame at the same time)
        if(strTrame[iBclReception] == '\0') {  // search end of frame
      // display received message
      Serial.println(strTrame); //debug
      iBclReception=0;
      cOctet = strTrame[0];
        }
    else {
      iBclReception++;
    }
    }


//nécéssaire la mise à jour et pour mémoriser l'état des phares (ON OU OFF)
if((cOctet == 0b00100000) || (cOctet ==0b00100001)){
  cPhares = cOctet;
  }
  
//nécéssaire la mise à jour et pour mémoriser l'état de la camera (ON OU OFF)
if((cOctet == 0b01000000) || (cOctet ==0b01000001)){
  cCamera = cOctet;
  }
 //nécéssaire la mise à jour et pour mémoriser l'état des néons (LEURS COULEURS)
 if ((cOctet ==0b00111100) || (cOctet ==0b00111101) || (cOctet ==0b00111110) || (cOctet ==0b00110110) || (cOctet ==0b00110101) || (cOctet ==0b00110100)){
  cAllumer = cOctet;
  }
  
  //ON OU OFF DES NEONS
if(cOctet == 0b00110000){
  allumer =0;
  }else if(cOctet == 0b00110001){
  allumer =1;
  }


//Necessaire pour mémoriser la vitesse des moteurs
if ((cOctet>=0b01010000) && (cOctet<=0b01011111)){
  cVitesse = cOctet;
  switch(cVitesse){
    case 0b01010000:
    iVitesse=0;
    break;
    case 0b01010001:
    iVitesse=17;
    break;
    case 0b01010010:
    iVitesse=34;
    break;
    case 0b01010011:
    iVitesse=51;
    break;
    case 0b01010100:
    iVitesse=68;
    break;
    case 0b01010101:
    iVitesse=85;
    break;
    case 0b01010110:
    iVitesse=102;
    break;
    case 0b01010111:
    iVitesse=119;
    break;
    case 0b01011000:
    iVitesse=136;
    break;
    case 0b01011001:
    iVitesse=153;
    break;
    case 0b01011010:
    iVitesse=170;
    break;
    case 0b01011011:
    iVitesse=187;
    break;
    case 0b01011100:
    iVitesse=204;
    break;
    case 0b01011101:
    iVitesse=221;
    break;
    case 0b01011110:
    iVitesse=238;
    break;
    case 0b01011111:
    iVitesse=255;
    break;
    }
  }
  

//appels de fonctions
controlesMoteurs(cOctet,iVitesse); // Controles vitesse + sens des moteurs(direction)
neons(cAllumer,allumer); //ON ou OFF des néons + choix des couleurs
PWM_Mode(); //Ultrason
phares(cPhares); //ON ou OFF des phares
CapteurIR( iInfraAR,  iInfraAD,  iInfraAG); //capteurs IR
camera(cCamera); //ON ou OFF de la camera


//conversion de la distance de l'ultrason en un string pour l'envoie au téléphone
sprintf(buf, "%lu", Distance); //conversion d'un long en une chaine de caractères

if(Distance<10){
 iBcl=-1;
    BLUETOOTH.write('0');
    BLUETOOTH.write('0');
    do {
      iBcl++;
            BLUETOOTH.write(buf[iBcl]);
        } while(buf[iBcl]!='\0'); // until last character
  }
 else if(Distance>=10 && Distance<100){
 iBcl=-1;
    BLUETOOTH.write('0');
    do {
      iBcl++;
            BLUETOOTH.write(buf[iBcl]);
        } while(buf[iBcl]!='\0'); // until last character
  }
 else {
 iBcl=-1;
    do {
      iBcl++;
            BLUETOOTH.write(buf[iBcl]);

        } while(buf[iBcl]!='\0'); // until last character
  }


}//fin du loop

                 

