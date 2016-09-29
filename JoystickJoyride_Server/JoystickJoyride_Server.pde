import processing.net.*;

float timer;

Server s;
Client c;

void setup()
{
  size(500, 500);
  s = new Server(this, 12345);
  fill(0);
  textAlign(CENTER);
}
void draw()
{
  background(255);
  if (mousePressed)
  {
    timer=millis()+1000;
    s.write("START|");
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
    String split[] = split(input, '|');
    if (split[0].equals("DIED"))
    {
      s.write("MSG|"+split[1]);
      println(split[1]);
    }
  }
  text("Click here to start a server", width/2, height/2);
}