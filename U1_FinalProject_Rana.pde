import ddf.minim.*;
import processing.net.*;
/*
Rana Lulla's Project
 I don't really have a formal name as of yet, but it's basically a joystick
 controlled game where you dodge bullets and waves of projectiles.
 */
float multiplier=0.75;
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

void setup()
{
  //Setting up the minim
  minim = new Minim(this);

  music = minim.loadFile("ASGORE.mp3");
  imageMode(CENTER);
  fullScreen(P2D);
  textAlign(CENTER);
  noCursor();
}
void draw()
{
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
void startScreen()
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
void gameCode()
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
    String[] end = split(input, '|');
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
  //Updating everything else
  player.movementCode();
  attackUpdate();
  //Updates the score and multiplier
  score++;
  multiplier+=0.00025; //Rate of 0.25 per 1000 points, just a smaller scale for more accurate updating
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
void attackUpdate()
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
void killAll()
{
  //Clear all projectiles before every wave
  normalProjectiles.clear();
  homingProjectiles.clear();
  accurateHomingProjectiles.clear();
}
void died()
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
void reset()
{
  //Sets values back to normal
  multiplier=0.75;
  player.x=width/2;
  player.y=2*(height/3);
  score=0;
  died=false;
  timer=0;
  justDied=false;
  scores.clear();
}
void receive()
{
  if (c.available()!=0)
  {
    String input = c.readString();
    String[] end = split(input, '|');
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
void texter()
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
void keyPressed()
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