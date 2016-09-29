class Player
{
  PVector v1; 
  float x=width/2, y=2*(height/3);  
  Player()
  {
    v1=new PVector(0, 0);
  }
  void movementCode()
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
  }
  void tutorial()
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