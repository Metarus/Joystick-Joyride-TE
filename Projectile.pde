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
  void collision()
  {
    //Testing the distance to see if there is collision
    fill(255,0,0);
    distance=dist(projX, projY, player.x, player.y);
    if (distance<=5+size/2)
    {
      collide=true;
    }
    fill(0);
    ellipse(projX, projY, size, size);
  }
}