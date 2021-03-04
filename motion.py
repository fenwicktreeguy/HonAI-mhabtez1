
import cv2


video_capture = cv2.VideoCapture(0)
THRESHOLD = 220


def set_color(red,green,blue,row,col):
    rgb_small_frame[row,col,0]=red
    rgb_small_frame[row,col,1]=green
    rgb_small_frame[row,col,2]=blue

def dist(r1,g1,b1,r2,g2,b2):
    a1 =r1-r2
    a2 =g1-g2
    a3 =b1-b2
    ret = ( (a1*a1)+(a2*a2)+(a3*a3) )
    return ret
    
# Accessing BGR pixel values
def detect_motion(prev_rgb_small_frame, rgb_small_frame):
    for r in range(rgb_small_frame.shape[0]):
        for c in range(rgb_small_frame.shape[1]):
            t1 = (prev_rgb_small_frame[r,c,0],prev_rgb_small_frame[r,c,1],prev_rgb_small_frame[r,c,2])
            t2 = (rgb_small_frame[r,c,0],rgb_small_frame[r,c,1],rgb_small_frame[r,c,2])
            DET_VAL = dist(t1[0],t1[1],t1[2],t2[0],t2[1],t2[2])
            #print("DISTANCE:  {}".format(DET_VAL))
            if(DET_VAL >= THRESHOLD):
                set_color(255,255,255,r,c)
            else:
                set_color(0,0,0,r,c)


while True:
    # Grab a single frame of video
    ret, frame = video_capture.read()
    prev_frame = frame[:]
    ret, frame = video_capture.read()

    # Resize frame of video to 1/4 size for faster processing
    prev_small_frame = cv2.resize(prev_frame, (0, 0), fx=0.25, fy=0.25)
    small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)

    # Convert the image from BGR color (which OpenCV uses) to RGB color 
    prev_rgb_small_frame = prev_small_frame[:, :, ::-1]
    rgb_small_frame = small_frame[:, :, ::-1]

    height, width, channels = rgb_small_frame.shape
    detect_motion(prev_rgb_small_frame,rgb_small_frame)
    '''
    for r in range(0, height):
        for c in range(0, width):
                rgb_small_frame[r,c,0] = 255
                rgb_small_frame[r,c,1] = 255
                rgb_small_frame[r,c,2] =255
    '''
               

    # Display the resulting image
    cv2.imshow('Video', rgb_small_frame)

    # Hit 'q' on the keyboard to quit!
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release handle to the webcam
video_capture.release()
cv2.destroyAllWindows()# -*- coding: utf-8 -*-

