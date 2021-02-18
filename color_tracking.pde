import processing.video.*;
Capture c;

color coltrak = color(255,0,0);

void setup(){
  size(800,800);
  c = new Capture(this,800,800);
  c.start();
}

void captureEvent(Capture c){
  c.read();
}

PVector color_tracking(color target){
  PVector found = new PVector(0,0);
  c.loadPixels();
  float CUR_X = 0;
  float CUR_Y = 0;
  float min_diff= 100000000;
  println("TARGET: " + red(target) + " " + green(target) + " " + blue(target) );
  for(int i = 0; i < c.pixels.length; i++){
      color cl = c.pixels[i];
      //println(red(cl) + " " + green(cl) + " " + blue(cl) );
      min_diff= min(min_diff,dist(red(cl), green(cl), blue(cl), red(target), green(target), blue(target)));
      if(min_diff == dist(red(cl), green(cl), blue(cl), red(target), green(target), blue(target))){
        found = new PVector(CUR_Y,CUR_X);
      }
      CUR_Y++;
      if(CUR_Y % 800 == 0){
        CUR_X++;
        CUR_Y=0;
      }
  }
  println("POSITION: " + found.x + " " + found.y);
  return found;
}



void mousePressed(){
  coltrak = c.pixels[mouseY*800 + mouseX];
}


void draw(){
  c.loadPixels();
  image(c,0,0);
  c.updatePixels();
  PVector resp = color_tracking(coltrak);
  println("POINT: " + resp.x+ " "+ resp.y);
  noFill();
  ellipse(resp.x,resp.y,10,10);
}
