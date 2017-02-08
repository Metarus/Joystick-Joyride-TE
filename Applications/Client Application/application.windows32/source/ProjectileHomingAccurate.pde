class ProjectileHomingAccurate extends Projectile
{
  PVector v1;

  ProjectileHomingAccurate(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
    v1=new PVector(0, 0);
  }
  void move()
  {
    //Homing code using vectors (similar to the players movement code)
    v1.x=projX-player.x;
    v1.y=projY-player.y;
    v1.normalize();
    projX-=multiplier*(5*v1.x);
    projY-=multiplier*(5*v1.y);
  }
}