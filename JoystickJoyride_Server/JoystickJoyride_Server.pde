import processing.net.*;

float timer;

Client c;
Server s, s1, s2, s3, s4;

int cNum, playerCount, playersDead;
String[] players = new String[4];
boolean[] playersTrue = new boolean[4];
boolean gameRunning;

void setup()
{
  fullScreen();
  background(255);
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
void draw()
{
  if (gameRunning==false)
  {
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
    background(255);
    timer=millis()+1000;
    s.write("START|");
    playersDead=0;
    gameRunning=true;
  }
  if (millis()>timer)
  {
    s.write("ATK|"+(int)random(1, 7));
    timer=millis()+5000;
  }
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
          s.write(split1[1]+"|"+i);
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

void display()
{
  if (c!=null)
  {
    String input = c.readString();
    String split0[] = split(input, '|');
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
    for (int i=0; i<split0.length; i++)
    {
      boolean skip=false;
      String test=split0[i];
      String split2[] = split(split0[i], ',');
      for (int a=0; a<test.length(); a++)
      {
        if (test.charAt(a)=='-')
        {
          skip=true;
        }
      }
      if (skip==false)
      {
        if (split2.length==3&&!split2[1].equals("")&&!split2[2].equals(""))
        {              
          if (split2[0].equals("b"))
          {
            fill(0);
          }
          if (split2[0].equals("c"))
          {
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