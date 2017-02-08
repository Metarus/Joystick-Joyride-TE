import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class U1_FinalProject_Rana extends PApplet {



/*
Rana Lulla's Project
 I don't really have a formal name as of yet, but it's basically a joystick
 controlled game where you dodge bullets and waves of projectiles.
 */
float multiplier=0.75f;
int screenState=5, timer, attackPattern, score, connector;
boolean died, nightMode, justDied, connected, sendAll;
String name="", Msg="";
//String[] scores = new String[2];
ArrayList<String> scores = new ArrayList<String>();
Minim minim;
AudioPlayer music;

//Declaring the player
Player player = new Player();
//Declaring all of the arraylists for all of the projectiles
ArrayList<ProjectileNormal> normalProjectiles = new ArrayList<ProjectileNormal>();
ArrayList<ProjectileHoming> homingProjectiles = new ArrayList<ProjectileHoming>();
ArrayList<ProjectileHomingAccurate> accurateHomingProjectiles = new ArrayList<ProjectileHomingAccurate>();

Client c, c1, c2, c3, c4;
Server s;

public void setup()
{
  //Setting up the minim
  minim = new Minim(this);

  music = minim.loadFile("ASGORE.mp3");
  imageMode(CENTER);
  
  //size(1280, 800, P2D);
  textAlign(CENTER);
  noCursor();
}
public void draw()
{
  println(connector);
  //Basic screenState switch
  switch(screenState)
  {
  case 0:
    startScreen();
    break;
  case 1:
    music.play();
    gameCode();
    if (died)
    {
      //Stopping the music if you die
      music.close();
      screenState=2;
      //Re opening it but not playing 
      music = minim.loadFile("ASGORE.mp3");
    }
    break;
  case 2:
    died();
    break;
  case 3:
    player.tutorial();
    break;
  case 4:
    background(0);
    text("You win!", width/2, height/2);
    receive();
    break;
  case 5:
    texter();
    break;
  }
  fill(0, 0, 255);
  ellipse(mouseX, mouseY, 10, 10);
  fill(255);
}
public void startScreen()
{
  receive();

  //Initial Screen
  background(0);
  textSize(75);
  //Night Mode button
  text("Night Mode", width/2, 200);
  if (mousePressed&&mouseX>500&&mouseX<780&&mouseY>125&&mouseY<225)
  {
    nightMode=true;
    screenState=1;
  }
  textSize(200);
  //Normal mode button
  text("Start", width/2, height/2);
  if (mousePressed&&mouseX>400&&mouseX<880&&mouseY>300&&mouseY<450)
  {
    nightMode=false;
    screenState=1;
  }
  textSize(75);
  //Tutorial button
  text("Tutorial", width/2, (height/2)+100);
  if (mousePressed&&mouseX>500&&mouseX<780&&mouseY>450&&mouseY<600)
  {
    screenState=3;
  }
}
public void gameCode()
{
  //Different backgrounds based on nightMode being true/false
  if (nightMode==false)
  {
    background(255);
  }
  if (nightMode==true)
  {
    background(0);
    ellipse(player.x, player.y, 600, 600);
  }
  //Runs randomiser every 5 seconds

  if (c.available()!=0)
  {
    String input = c.readString();
    String[] split1 = split(input, "||");
    for (int i=0; i<split1.length; i++)
    {
      String[] end = split(split1[i], '|');
      if (end[0].equals("START"))
      {
        reset();
      }
      if (end[0].equals("WIN"))
      {
        print(end[1]);
      }
      if (end[0].equals("MSG"))
      {      
        scores.add(end[1]);
      }
      if (end[0].equals("ATK"))
      {
        attackPattern=Integer.parseInt(end[1]);
        player.x=width/2;
        player.y=3*(height/4);
        switch(attackPattern)
        {
        case 1: 
          Attack1();
          break;
        case 2:
          Attack2();
          break;
        case 3:
          Attack3();
          break;
        case 4:
          Attack4();
          break;
        case 5:
          Attack5();
          break;
        case 6:
          Attack6();
          break;
        }
      }
    }
  }
  //Updating everything else
  player.movementCode();
  attackUpdate();
  //Updates the score and multiplier
  score++;
  multiplier+=0.00025f; //Rate of 0.25 per 1000 points, just a smaller scale for more accurate updating
  textSize(10);
  //Different colored text (white for night mode and black for normal mode) for the score
  if (nightMode)
  {
    fill(255);
    text(score, 10, 10);
    //text(millis()-timer, width-20, 20);
  } else
  {
    fill(0);
    //text(timer-millis(), width-20, 10);
    text(score, 10, 10);
  }
}
public void attackUpdate()
{
  //Runs through all of the code for updating the projectiles; collision, movement, and if it collides kill the player
  for (int i=0; i<normalProjectiles.size(); i++)
  {
    ProjectileNormal part = normalProjectiles.get(i);
    part.move();
    part.collision();
    if (part.collide)
    {
      died=true;
    }
  }
  for (int i=0; i<accurateHomingProjectiles.size(); i++)
  {
    ProjectileHomingAccurate part = accurateHomingProjectiles.get(i);
    part.move();
    part.collision();
    if (part.collide)
    {
      died=true;
    }
  }
  for (int i=0; i<homingProjectiles.size(); i++)
  {
    ProjectileHoming part = homingProjectiles.get(i);
    part.move();
    part.collision();
    if (part.collide)
    {
      died=true;
    }
  }
}
public void killAll()
{
  //Clear all projectiles before every wave
  normalProjectiles.clear();
  homingProjectiles.clear();
  accurateHomingProjectiles.clear();
}
public void died()
{
  if (c.available()!=0)
  {
    String input = c.readString();
    String[] end = split(input, '|');
    if (end[0].equals("START"))
    {
      screenState=1;
      reset();
      nightMode=false;
    }
    if (end[0].equals("MSG"))
    {
      scores.add(end[1]);
    }
  }  
  //The respawn screenState
  if (justDied==false)
  {
    justDied=true;
    c.write("DIED|"+name+": "+score);
  }
  background(0);
  killAll();
  textSize(50);
  text("You died!", width/2, height/3);
  textSize(30);
  text("Click to play again!", width/2, 2*(height/3));
  //text(scores[0], width/2, height/2);
  //text(scores[1], width/2, (height/2)+50);
  for (int i=0; i<scores.size(); i++)
  {
    text(scores.get(i), width/2, (height/2)+(i*50)-50);
  }
  text("Back", 35, 30);
  if (mousePressed&&mouseX<90&&mouseY<40)
  {
    screenState=0;
    reset();
  } else if (mousePressed)
  {
    reset();
    screenState=1;
  }
}
public void reset()
{
  //Sets values back to normal
  multiplier=0.75f;
  player.x=width/2;
  player.y=2*(height/3);
  score=0;
  died=false;
  timer=0;
  justDied=false;
  scores.clear();
}
public void receive()
{
  if (c.available()!=0)
  {
    String input = c.readString();
    String[] split1 = split(input, "||");
    for (int i=0; i<split1.length; i++)
    {
      String[] end = split(split1[i], '|');
      println(input);
      if (end[0].equals("START"))
      {
        screenState=1;
        reset();
        nightMode=false;
      }
      if (end[0].equals("MSG"))
      {
        scores.add(end[1]);
      }
      if (end[0].equals(name))
      {
        connector=Integer.valueOf(end[1]);
        println(connector);
      }
    }
  }
}
public void texter()
{
  background(0);
  textSize(50);
  fill(255);
  text(Msg, width/2, height/2);
  if (connected==false)
  {
    text("Please enter an IP", width/2, height/2-100);
  } else
  {
    text("Please enter a name", width/2, height/2-100);
  }
}
public void keyPressed()
{
  if (screenState == 5)
  {
    if (key==ENTER)
    {
      if (connected==false)
      {
        connected=true;
        c = new Client(this, Msg, 12345);
        c1 = new Client(this, Msg, 12341);
        c2 = new Client(this, Msg, 12342);
        c3 = new Client(this, Msg, 12343);
        c4 = new Client(this, Msg, 12344);
      } else
      {
        name=Msg;
        screenState=0;
        c.write("NEW|"+name);
      }
      Msg="";
    }
    if (key==BACKSPACE)
    {
      if (0<Msg.length())
      {
        Msg = Msg.substring(0, Msg.length()-1);
      }
    } else if (key!=CODED&&key!=ENTER)
    {
      Msg=(Msg+key);
    }
  }
}
//Different attack patterns

public void Attack1()
{
  killAll();

  for (int i=0; i<100; i++)
  {
    normalProjectiles.add(new ProjectileNormal(random(width), 0, 0, 3));
  }
}
public void Attack2()
{
  killAll();

  for (int i=0; i<5; i++)
  {
    homingProjectiles.add(new ProjectileHoming(random(width), 0, 0, 0));
    homingProjectiles.add(new ProjectileHoming(0, random(height), 0, 0));
    homingProjectiles.add(new ProjectileHoming(random(width), height, 0, 0));
    homingProjectiles.add(new ProjectileHoming(width, random(height), 0, 0));
  }
}
public void Attack3()
{
  killAll();

  for (int i=0; i<5; i++)
  {
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(random(width), 0, 0, 0));
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(0, random(height), 0, 0));
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(random(width), height, 0, 0));
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(width, random(height), 0, 0));
  }
}
public void Attack4()
{
  killAll();

  for (int i=0; i<50; i++)
  {
    normalProjectiles.add(new ProjectileNormal(random(width), 0, 0, 3));
    normalProjectiles.add(new ProjectileNormal(random(width), -500, 0, 3));
  }
  for (int i=0; i<5; i++)
  {
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(0, random(height), 0, 0));
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(width, random(height), 0, 0));
  }
}
public void Attack5()
{
  killAll();

  for (int i=0; i<40; i++)
  {
    normalProjectiles.add(new ProjectileNormal(random(width), 0, 0, 4));
    normalProjectiles.add(new ProjectileNormal(random(width), -300, 0, 4));
    normalProjectiles.add(new ProjectileNormal(random(width), -600, 0, 4));
  }
}
public void Attack6()
{
  killAll();
  
  for (int i=0; i<5; i++)
  {
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(0, random(height), 0, 0));
    accurateHomingProjectiles.add(new ProjectileHomingAccurate(width, random(height), 0, 0));
    homingProjectiles.add(new ProjectileHoming(random(width), height, 0, 0));
    homingProjectiles.add(new ProjectileHoming(random(width), 0, 0, 0));
  }
}
class Player
{
  PVector v1; 
  float x=width/2, y=2*(height/3);  
  Player()
  {
    v1=new PVector(0, 0);
  }
  public void movementCode()
  {
    fill(0);
    //Setting values of vectors to track the cursor relative to the center point
    v1.x=mouseX-(width/2);
    v1.y=mouseY-(3*(height/4));
    //Normalising it so it's in terms of values of 1
    v1.normalize();
    //Drawing the line so it has a nice joystick effect
    line(width/2, 3*(height/4), mouseX, mouseY);
    ellipse(width/2, 3*(height/4), 20, 20);
    //Adding the velocity to the positions themselves
    x+=multiplier*6*v1.x;
    y+=multiplier*6*v1.y;
    //Drawing the character itself
    fill(255, 0, 0);
    ellipse(x, y, 10, 10);
    fill(0);
    //Setting character to dead if it's out of the boundaries
    if (x<0||y<0||x>width||y>height)
    {
      died=true;
    }
    int x1=(int)x;
    int y1=(int)y;
    switch(connector)
    {
    case 0:
      c1.write("c,"+x1+","+y1+"|");
      break;
    case 1:
      c2.write("c,"+x1+","+y1+"|");
      break;
    case 2:
      c3.write("c,"+x1+","+y1+"|");
      break;
    case 3:
      c4.write("c,"+x1+","+y1+"|");
      break;
    }
  }
  public void tutorial()
  {
    //Tutorial which runs through the player
    background(255);
    player.movementCode();
    textSize(30);
    text("You are the little red ball", width/2, 50);
    text("Move around by moving your cursor around the black ball", width/2, 100);
    text("In the game, you can't touch the walls so be careful!", width/2, 150);
    text("You can't touch the black projectiles either! Some move differently too", width/2, 200);
    text("Click the back button when you feel confident!", width/2, 250);
    text("Back", 35, 30);
    //Setting boundaries instead of killing the character for an easier tutorial
    if (mousePressed&&mouseX<90&&mouseY<40)
    {
      screenState=0;
    }
    if (player.x<=0)
    {
      player.x=0;
    }
    if (player.x>=width)
    {
      player.x=width;
    }
    if (player.y<=0)
    {
      player.y=0;
    }
    if (player.y>=height)
    {
      player.y=height;
    }
    fill(255, 0, 0);
    ellipse(player.x, player.y, 10, 10);
  }
}
class Projectile
{
  float velX, velY, projX, projY, distance, size=10;
  boolean collide, kill;
  Projectile(float _x, float _y, float _velX, float _velY)
  {
    projX=_x;
    projY=_y;
    velX=_velX;
    velY=_velY;
  }
  public void collision()
  {
    //Testing the distance to see if there is collision
    fill(255, 0, 0);
    distance=dist(projX, projY, player.x, player.y);
    if (distance<=5+size/2)
    {
      collide=true;
    }
    fill(0);
    ellipse(projX, projY, size, size);
    int x, y;
    x=(int)projX;
    y=(int)projY;
    if (projX>0&&projX<width&&projY>0&&projY<height)
    {
      switch(connector)
      {
      case 0:
        c1.write("b,"+x+","+y+"|");
        break;
      case 1:
        c2.write("b,"+x+","+y+"|");
        break;
      case 2:
        c3.write("b,"+x+","+y+"|");
        break;
      case 3:
        c4.write("b,"+x+","+y+"|");
        break;
      }
    }
  }
}
class ProjectileHoming extends Projectile
{
  ProjectileHoming(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
  }
  public void move()
  {
    //Basic homing code, simply goes up if it's below character, down if it's above, etc. 
    if(projX<player.x)
    {
      projX+=multiplier*3;
    }
    if(projX>player.x)
    {
      projX-=multiplier*3;
    }
    if(projY<player.y)
    {
      projY+=multiplier*3;
    }
    if(projY>player.y)
    {
      projY-=multiplier*3;
    }
  }
}
class ProjectileHomingAccurate extends Projectile
{
  PVector v1;

  ProjectileHomingAccurate(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
    v1=new PVector(0, 0);
  }
  public void move()
  {
    //Homing code using vectors (similar to the players movement code)
    v1.x=projX-player.x;
    v1.y=projY-player.y;
    v1.normalize();
    projX-=multiplier*(5*v1.x);
    projY-=multiplier*(5*v1.y);
  }
}
class ProjectileNormal extends Projectile
{
  ProjectileNormal(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
  }
  public void move()
  {
    //Most basic projectile, just adds the velocity
    projX+=multiplier*velX;
    projY+=multiplier*velY;
  }
}
  public void settings() {  fullScreen(P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "U1_FinalProject_Rana" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
