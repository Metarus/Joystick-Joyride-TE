class ProjectileNormal extends Projectile
{
  ProjectileNormal(float projX, float projY, float velX, float velY)
  {
    super(projX, projY, velX, velY);
  }
  void move()
  {
    //Most basic projectile, just adds the velocity
    projX+=multiplier*velX;
    projY+=multiplier*velY;
  }
}