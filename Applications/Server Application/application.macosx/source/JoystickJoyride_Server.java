import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class JoystickJoyride_Server extends PApplet {



float timer;
//Setting up all 5 servers; one for each
Client c;
Server s, s1, s2, s3, s4;

int cNum, playerCount, playersDead;
String[] players = new String[4];
boolean[] playersTrue = new boolean[4];
boolean gameRunning;

public void setup()
{
  
  background(255);
  //Creating all the servers
  s = new Server(this, 12345);
  s1 = new Server(this, 12341);
  s2 = new Server(this, 12342);
  s3 = new Server(this, 12343);
  s4 = new Server(this, 12344);
  fill(0);
  textAlign(CENTER);
  for (int i=0; i<4; i++)
  {
    players[i]="None";
  }
}
public void draw()
{
  if (gameRunning==false)
  {
    //Start screen
    fill(0);
    background(0);
    fill(255);
    textSize(30);
    text(players[0], width/4, height/4);
    text(players[1], width/4+width/2, height/4);
    text(players[2], width/4, height/4+height/2);
    text(players[3], width/4+width/2, height/4+height/2);
    textSize(100);
    text("Joystick Joyride", width/2, height/2);
    textSize(50);
    text("Tournament Edition", width/2, height/2+80);
  }
  if (mousePressed)
  {
    //Starting the code
    background(255);
    timer=millis()+1000;
    //Sending all of the players connections in case it doesn't get through the first time
    s.write("START||"+players[0]+"|"+0+"||"+players[1]+"|"+1+"||"+players[2]+"|"+2+"||"+players[3]+"|"+3+"||");
    playersDead=0;
    gameRunning=true;
  }
  if (millis()>timer)
  {
    //Sending out the next attack pattern
    s.write("ATK|"+(int)random(1, 7));
    timer=millis()+5000;
  }
  //Doing the general detection
  c = s.available();
  if (c!=null)
  {
    String input = c.readString();
    String split1[] = split(input, '|');

    if (split1[0].equals("DIED"))
    {
      s.write("MSG|"+split1[1]);
      playersDead++;
    }
    if (split1[0].equals("NEW"))
    {
      for (int i=0; i<4; i++)
      {
        if (playersTrue[i]==false)
        {
          players[i]=split1[1];
          playersTrue[i]=true;
          playerCount++;
          break;
        }
      }
    }
  }
  if (playersDead==playerCount)
  {
    gameRunning=false;
  }
  //Receiving all of the players enemy movements
  for (cNum=0; cNum<4; cNum++)
  {
    switch(cNum)
    {
    case 0:
      c = s1.available();
      break;
    case 1:
      c = s2.available();
      break;
    case 2:
      c = s3.available();
      break;
    case 3:
      c = s4.available();
      break;
    }
    display();
  }
}

public void display()
{
  if (c!=null)
  {
    //Doing the general splitting into all of the sections within each channel
    String input = c.readString();
    String split0[] = split(input, '|');
    for (int i=0; i<split0.length; i++)
    {
      boolean skip=false;
      String test=split0[i];
      String split2[] = split(split0[i], ',');
      //Splitting it into each seperate variable
      for (int a=0; a<test.length(); a++)
      {
        if (test.charAt(a)=='-')
        {
          skip=true;
        }
      }
      if (skip==false)
      {
        //Doing final checks before proceeding
        if (split2.length==3&&!split2[1].equals("")&&!split2[2].equals(""))
        {              
          if (split2[0].equals("b"))
          {
            fill(0);
          }
          if (split2[0].equals("c"))
          {
            //Characters are always at the start, so if there are multiple characters then only the last one
            fill(255);
            switch (cNum)
            {
            case 0:
              rect(0, 0, width/2, height/2);
              break;
            case 1:
              rect(width/2, 0, width, height/2);
              break;
            case 2:
              rect(0, height/2, width/2, height);
              break;
            case 3:
              rect(width/2, height/2, width, height);
              break;
            }
            fill(255, 0, 0);
          }
          switch (cNum)
          {
          case 0:
            ellipse(Integer.parseInt(split2[1])/2, Integer.parseInt(split2[2])/2, 5, 5);
            break;
          case 1:
            ellipse(Integer.parseInt(split2[1])/2+width/2, Integer.parseInt(split2[2])/2, 5, 5);
            break;
          case 2:
            ellipse(Integer.parseInt(split2[1])/2, Integer.parseInt(split2[2])/2+height/2, 5, 5);
            break;
          case 3:
            ellipse(Integer.parseInt(split2[1])/2+width/2, Integer.parseInt(split2[2])/2+height/2, 5, 5);
            break;
          }
        }
      }
    }
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "JoystickJoyride_Server" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
