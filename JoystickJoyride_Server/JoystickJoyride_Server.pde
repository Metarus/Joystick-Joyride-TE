import processing.net.*;

float timer;

Server s;
Client c;
String[] players = new String[4];
boolean[] playersTrue = new boolean[4];

void setup()
{
  //size(500, 500);
  fullScreen();
  background(255);
  s = new Server(this, 12345);
  fill(0);
  textAlign(CENTER);
  for (int i=0; i<4; i++)
  {
    players[i]="";
  }
}
void draw()
{
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
    //println(input);
    String split0[] = split(input, "||");
    for (int k=0; k<split0.length; k++)
    {
      String split1[] = split(split0[k], '|');
      if (split1[0].equals("DIED"))
      {
        s.write("MSG|"+split1[1]);
      }
      if (split1[0].equals("NEW"))
      {
        for (int i=0; i<4; i++)
        {
          if (playersTrue[i]==false)
          {
            players[i]=split1[1];
            playersTrue[i]=true;
            break;
          }
        }
      }
      for (int p=0; p<4; p++)
      {
        if (split1[0].equals(players[p]))
        {
          fill(255);
          switch (p)
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
          for (int i=1; i<split1.length; i++)
          {
            String split2[] = split(split1[i], ',');
            if (split2.length==3)
            {
              if (split2[0].equals("b"))
              {
                fill(0);
              }
              if (split2[0].equals("c"))
              {
                fill(255, 0, 0);
              }
              if (split2[1]!=""&&split2[2]!=""&&split2.length==3)
              {
                switch (p)
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
    }
  }
}