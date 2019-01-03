void PWM_Mode_Setup()
{ 
  pinMode(URTRIG,OUTPUT);                     // A low pull on pin COMP/TRIG
  digitalWrite(URTRIG,HIGH);                  // Set to HIGH
  
  pinMode(URPWM, INPUT);                      // Sending Enable PWM mode command
  
  for(int i=0;i<4;i++)
  {
      Serial.write(EnPwmCmd[i]);
  } 
}


 
void PWM_Mode()
{                              // a low pull on pin COMP/TRIG  triggering a sensor reading
    digitalWrite(URTRIG, LOW);
    digitalWrite(URTRIG, HIGH);               // reading Pin PWM will output pulses
     
    unsigned long DistanceMeasured=pulseIn(URPWM,LOW,20000); //au dela de 10ms (timeout puleIn renvoit 0)
     
    if(DistanceMeasured>=10200)
    {              // the reading is invalid.
      Serial.println("Invalid");    
    }
    else
    {
      Distance=DistanceMeasured/50;           // every 50us low level stands for 1cm
    }

}

void initialisationSorties(void){
    pinMode(PORT_SPEED_M1, OUTPUT); 
    pinMode(PORT_SPEED_M2, OUTPUT); 
    pinMode(PORT_DIRECTION_M1, OUTPUT); 
    pinMode(PORT_DIRECTION_M2, OUTPUT);  
    pinMode(rouge, OUTPUT); 
    pinMode(bleu, OUTPUT); 
    pinMode(14, OUTPUT); 
    pinMode(12, OUTPUT); 
  }

void controlesMoteurs(char cBluetooth, int iVitesse){
    if(cBluetooth ==0b00010011 || cBluetooth=='f'){ //avance
      digitalWrite(PORT_DIRECTION_M1, LOW);
      digitalWrite(PORT_DIRECTION_M2, LOW);
      analogWrite(PORT_SPEED_M1, iVitesse);
      analogWrite(PORT_SPEED_M2, iVitesse);
     
    }
    else if(cBluetooth ==0b00010000 || cBluetooth=='s'){ //arrêt
      digitalWrite(PORT_DIRECTION_M1, HIGH);
      digitalWrite(PORT_DIRECTION_M2, HIGH);
      analogWrite(PORT_SPEED_M1, 0);
      analogWrite(PORT_SPEED_M2, 0);
    }
    else if(cBluetooth ==0b00010100 || cBluetooth=='l'){ //gauche
      digitalWrite(PORT_DIRECTION_M1, LOW);
      digitalWrite(PORT_DIRECTION_M2, HIGH);
      analogWrite(PORT_SPEED_M1, iVitesse);
      analogWrite(PORT_SPEED_M2, iVitesse);
    }
    else if(cBluetooth ==0b00010001 || cBluetooth=='r'){ //droite
      digitalWrite(PORT_DIRECTION_M1, HIGH);
      digitalWrite(PORT_DIRECTION_M2, LOW);
      analogWrite(PORT_SPEED_M1, iVitesse);
      analogWrite(PORT_SPEED_M2, iVitesse);
    }
    else if(cBluetooth ==0b00010010 || cBluetooth=='b'){ //reculer
      digitalWrite(PORT_DIRECTION_M1, HIGH);
      digitalWrite(PORT_DIRECTION_M2, HIGH);
      analogWrite(PORT_SPEED_M1, iVitesse);
      analogWrite(PORT_SPEED_M2, iVitesse);
    }
  
  }

void CapteurIR(int iInfraAR, int iInfraAD, int iInfraAG){
char cAR, cAD, cAG;
char cCapteur[4];
int iBcl=0;

  if(iInfraAR>300){
  cAR = '0';
  
  }else{
   cAR = '1';

    }

 if(iInfraAD>300){
   cAD = '0';
  
  }else{
   cAD = '1';
 
    }
    
 if(iInfraAG>300){
  cAG = '0';

  }else{
   cAG = '1';
  
    }
cCapteur[0]=cAG;
cCapteur[1]=cAD;
cCapteur[2]=cAR;
cCapteur[3]=c;
BLUETOOTH.write(cCapteur);     
}


void neons(char cBluetooth, bool allumer){
  if(cBluetooth ==0b00111100 && allumer==1 ){                          //on red
      digitalWrite(rouge, HIGH);
  }
  else if(cBluetooth ==0b00111101 && allumer==1){                     //on green
     digitalWrite(vert, HIGH);
  }
  else if(cBluetooth ==0b00111110 && allumer==1){                 //on blue
      digitalWrite(bleu, HIGH);
  }
  else if(cBluetooth ==0b00110100 && allumer==1){                 
      digitalWrite(rouge, LOW);
  }
  else if(cBluetooth ==0b00110101 && allumer==1){                
      digitalWrite(vert, LOW);
  }
  else if(cBluetooth ==0b00110110 && allumer==1){              
      digitalWrite(bleu, LOW);
  }
  else{
     digitalWrite(rouge, LOW);
      digitalWrite(vert, LOW);
      digitalWrite(bleu, LOW);
    }
        
}

void phares(char cBluetooth){
if(cBluetooth ==0b00100001){  
  digitalWrite(12, HIGH);
}
else if (cBluetooth ==0b00100000)
digitalWrite(12, LOW);
}
void camera(char cBluetooth){
if(cBluetooth ==0b01000000){  
  digitalWrite(14, LOW);
}
else if (cBluetooth ==0b01000001)
digitalWrite(14, HIGH);
}

