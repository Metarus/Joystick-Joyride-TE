class ProjectileHoming extends Projectile
{
  ProjectileHoming(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
  }
  void move()
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