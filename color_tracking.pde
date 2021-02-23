import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture c;
OpenCV opencv;
OpenCV opencv2;
PImage drake;
PImage glasses;
PImage first_saved_face;
PImage second_saved_face;

color coltrak = color(255,0,0);

void setup(){
  frameRate(60);
  size(640,480);
  c = new Capture(this,640,480);
  opencv = new OpenCV(this,640,480);
  opencv2 = new OpenCV(this,640,480);
  c.start();
  drake = loadImage("drake.png");
  glasses = loadImage("sun.png");
  //opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
  
}

void put_sunglasses(){
  opencv.loadCascade(OpenCV.CASCADE_NOSE);
  c.loadPixels();
  opencv.loadImage(c);
  image(c,0,0);
  c.updatePixels();
  Rectangle[] f = opencv.detect();
  noFill();
  stroke(0,255,125);
  //puts sunglasses on face
  //for(int i = 0; i < f.length; i++){
    if(f.length > 0){
      rect(f[0].x,f[0].y,f[0].width,f[0].height);
      image(glasses, f[0].x - 75, f[0].y - 50, 4*(f[0].width), (f[0].height) );
    }
  //}
}

void put_face(){
  opencv2.loadCascade(OpenCV.CASCADE_FRONTALFACE);
  c.loadPixels();
  opencv2.loadImage(c);
  image(c,0,0);
  c.updatePixels();
  Rectangle[] f = opencv2.detect();
  noFill();
  stroke(0,255,125);
  //puts sunglasses on face
  for(int i = 0; i < f.length; i++){
    rect(f[i].x,f[i].y,f[i].width,f[i].height);
    image(drake, f[i].x, f[i].y, (f[i].width), (f[i].height) );
  }
  
}

void face_swap(){
    c.loadPixels();
    opencv.loadImage(c);
    image(c,0,0);
    c.updatePixels();
    
    opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
    Rectangle[] r = opencv.detect();
    if(r.length == 2){
        first_saved_face = get(r[0].x,r[0].y,2*r[0].width,2*r[0].height);
        second_saved_face = get(r[1].x,r[1].y, r[1].width,r[1].height);
        set(r[0].x,r[0].y,second_saved_face);
        set(r[1].x,r[1].y,first_saved_face);
    } else {
      println("NUMBER OF FACES: " + r.length);
    }
    c.updatePixels();
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
  //put_face();
  //put_sunglasses();
  face_swap();
  
  /*
  PVector resp = color_tracking(coltrak);
  println("POINT: " + resp.x+ " "+ resp.y);
  noFill();
  ellipse(resp.x,resp.y,10,10);
  */
}
