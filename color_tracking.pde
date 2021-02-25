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
PImage prev_v_image;
PImage cur_v_image;
int BUFFER_COUNTER = 20;
int CUR_COUNTER = 0;
ArrayList<PVector> drawing = new ArrayList<PVector>();

color coltrak = color(255,255,255);

void setup(){
  frameRate(80);
  size(640,480);
  c = new Capture(this,640,480);
  opencv = new OpenCV(this,640,480);
  opencv2 = new OpenCV(this,640,480);
  prev_v_image = createImage(640,480,RGB);
  cur_v_image = createImage(640,480,RGB);
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
      image(glasses, f[0].x - 75, f[0].y - 50, 4*(f[0].width), 1.5*(f[0].height) );
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
        first_saved_face = get(r[0].x,r[0].y,r[0].width,r[0].height);
        second_saved_face = get(r[1].x,r[1].y, r[1].width, r[1].height);
        first_saved_face.resize(r[1].width + (r[1].width/6), r[1].height + (r[1].height/6) );
        set(r[0].x-20,r[0].y-20,second_saved_face);
        set(r[1].x,r[1].y,first_saved_face);
    } else {
      println("NUMBER OF FACES: " + r.length);
    }
    c.updatePixels();
}


void captureEvent(Capture c){
   prev_v_image.copy(c, 0, 0, c.width, c.height, 0, 0, prev_v_image.width, prev_v_image.height);
   prev_v_image.updatePixels();
   CUR_COUNTER = ( (CUR_COUNTER + 1) % (BUFFER_COUNTER) );
   c.read();
}

boolean detectMotion(){
  c.loadPixels();
  loadPixels();
  //prev_v_image.loadPixels();
  image(c,0,0);
  boolean hasMove = false;
  for(int i = 0; i < c.pixels.length; i++){
     /*
      println("IMAGE: " + red(prev_v_image.pixels[i]) + " " + green(prev_v_image.pixels[i]) + " " + blue(prev_v_image.pixels[i]) );
      println("CAPTURE: " + red(c.pixels[i]) + " " + green(c.pixels[i]) + " " + blue(c.pixels[i]) );
      println("DISTANCE: " + dist( red(prev_v_image.pixels[i]), green(prev_v_image.pixels[i]), blue(prev_v_image.pixels[i]), red(c.pixels[i]), green(c.pixels[i]), blue(c.pixels[i]) ) );
      */
      if(dist( red(prev_v_image.pixels[i]), green(prev_v_image.pixels[i]), blue(prev_v_image.pixels[i]), red(c.pixels[i]), green(c.pixels[i]), blue(c.pixels[i]) ) <= 100 ){
        pixels[i] = color(0,0,0);
      } else{
        hasMove = true;
        pixels[i] = color(255,255,0);
      }
   }
   c.updatePixels();
   updatePixels();
   return hasMove;
}

void air_draw(){
  c.loadPixels();
  if(red(coltrak)==255 && green(coltrak)==255 && blue(coltrak)== 255){
    return;
  }
  PVector p = color_tracking(coltrak);
  fill(255,0,0);
  println("POSITION: " + p.x + " " + p.y);
  drawing.add(p);
  c.updatePixels();
}

void keyPressed(){
  if(key==BACKSPACE){
    drawing.clear();
  }
}



PVector color_tracking(color target){
  if(red(target) == 255 && green(target)==255 & blue(target)==255){
    return new PVector(-1,-1);
  }
  PVector found = new PVector(0,0);
  c.loadPixels();
  float CUR_X = 0;
  float CUR_Y = 0;
  println("TARGET: " + red(target) + " " + green(target) + " " + blue(target) );
  float min_diff= 100000000;
  int idx = 0;
  for(int i = 0; i < c.pixels.length; i++){
      color cl = c.pixels[i];
      //println(red(cl) + " " + green(cl) + " " + blue(cl) );
      min_diff= min(min_diff,dist(red(cl), green(cl), blue(cl), red(target), green(target), blue(target)));
      if(min_diff == dist(red(cl), green(cl), blue(cl), red(target), green(target), blue(target))){
        found = new PVector(CUR_Y,CUR_X);
        idx = i;
      }
      CUR_Y++;
      if(CUR_Y % 640 == 0){
        CUR_X++;
        CUR_Y=0;
      }
  }
  if(dist(red(c.pixels[idx]),green(c.pixels[idx]),blue(c.pixels[idx]),red(c.pixels[idx]),green(c.pixels[idx]),blue(c.pixels[idx])) >= 80){
    return new PVector(-1,-1);
  }
  
  return found;
}



void mousePressed(){
  coltrak = c.pixels[mouseY*640 + mouseX];
}


void draw(){
  if(detectMotion()){
    noFill();
    stroke(255,0,0);
    rect(0,0,640,480);
    stroke(255,255,0);
    rect(1,1,638,478);
    stroke(0,255,255);
    rect(2,2,637,477);
  }
  /*
  air_draw();
  image(c,0,0);
  for(PVector p : drawing){
    if(p.x==-1 || p.y==-1){
      continue;
    }
    ellipse(p.x,p.y,10,10);
  }
  */
  
  
  /*
  PVector resp = color_tracking(coltrak);
  image(c,0,0);
  if(resp.x != -1 && resp.y != -1){
    println("POINT: " + resp.x+ " "+ resp.y);
    fill(255,0,0);
    ellipse(resp.x,resp.y,10,10);
  }
  */

  
  
}
