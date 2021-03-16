# -*- coding: utf-8 -*-
import cv2
import scipy
from scipy.spatial import distance as dist
import sklearn
import dlib
import numpy as np
from PIL import Image as im
from imutils import face_utils
from imutils.video import VideoStream, FPS
import imutils
import time
import math

vidcap = VideoStream(src=0).start()
video_capture = cv2.VideoCapture(0)
fps = FPS().start()


#Haar Cascades Classifier
'''
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
eye_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_eye.xml')
'''

'''
POTENTIAL IDEA: COMBINE MOTION DETECTION ALONG WITH CONTOUR SIZE OF EYES IN ORDER
TO MAKE PICS WHICH ARE BOTH FAR AWAY AND CONTAIN A LOT OF MOTION IN THEM
AND MAKE SURE TO SPACE OUT PICS ON INTERVALS
'''


SHOULD_I_TAKE_PICTURE = 17250
FROWN_THRESHOLD = 1.2
EYE_LOWER_THRESHOLD = 15.5
EYE_UPPER_THRESHOLD = 30.6
MOTION_THRESHOLD = 50000
prev_frame = None
COUNTER = 0
MOST_RECENT_PICTURE = None

def dampened_sigmoid(f):
    ret = 1/(1 + math.exp(-0.2*f))
    return ret

def distance_from_eye_contour_function(vl,motion_pic, frame):
    first = (MOTION_THRESHOLD - (MOTION_THRESHOLD * dampened_sigmoid(math.log2(vl)))) + (motion_pic)
    mar = (MOTION_THRESHOLD * dampened_sigmoid(math.log2(vl)))
    return (first, mar)

class MotionModule:
    THRESHOLD = 220
    
    def set_color(self,rgb_small_frame,red,green,blue,row,col):
        rgb_small_frame[row,col,0]=red
        rgb_small_frame[row,col,1]=green
        rgb_small_frame[row,col,2]=blue

    @staticmethod
    def dist(self,r1,g1,b1,r2,g2,b2):
        a1 =r1-r2
        a2 =g1-g2
        a3 =b1-b2
        ret = ( (a1*a1)+(a2*a2)+  (a3*a3) )
        return ret
    
    # Accessing BGR pixel values
    def detect_motion(self,prev_rgb_small_frame,rgb_small_frame):
        perturbed_pixels = 0
        for r in range(rgb_small_frame.shape[0]):
            for c in range(rgb_small_frame.shape[1]):
                t1 = (prev_rgb_small_frame[r,c,0],prev_rgb_small_frame[r,c,1],prev_rgb_small_frame[r,c,2])
                t2 = (rgb_small_frame[r,c,0],rgb_small_frame[r,c,1],rgb_small_frame[r,c,2])
                DET_VAL = MotionModule.dist(self,t1[0],t1[1],t1[2],t2[0],t2[1],t2[2])
                #print("DISTANCE:  {}".format(DET_VAL))
                if(DET_VAL >= MotionModule.THRESHOLD):
                    perturbed_pixels += 1
                    self.set_color(rgb_small_frame,255,255,255,r,c)
                else:
                    self.set_color(rgb_small_frame,0,0,0,r,c)
        print("CHANGED PIXELS: {}".format(perturbed_pixels))
        return perturbed_pixels



FACIAL_LANDMARKS_IDXS = dict([
	("mouth", (48, 68)),
	("right_eyebrow", (17, 22)),
	("left_eyebrow", (22, 27)),
	("right_eye", (36, 42)),
	("left_eye", (42, 48)),
	("nose", (27, 35)),
	("jaw", (0, 17))
])

shape_predictor= "shape_predictor_68_face_landmarks.dat" 
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(shape_predictor)
(mStart, mEnd) = face_utils.FACIAL_LANDMARKS_IDXS["mouth"]
(mStart2, mEnd2) = face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
(mStart3, mEnd3) = face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]
(mStart4,mEnd4)= face_utils.FACIAL_LANDMARKS_IDXS["left_eyebrow"]
(mStart5,mEnd5)= face_utils.FACIAL_LANDMARKS_IDXS["right_eyebrow"]
(mStart6,mEnd6) = face_utils.FACIAL_LANDMARKS_IDXS["nose"]


INC_COUNTER = 0

def take_picture(img):
    global INC_COUNTER
    s = "PICTURE_" + str(INC_COUNTER) + ".jpg"
    cv2.imwrite(s, img)
    INC_COUNTER += 1

def inflate_right_eye(img):
    right_eye = (215,105)
    radius = 30
    power = 1.6 # >1.0 for expansion, <1.0 for shrinkage
    
    height, width, _ = img.shape
    map_y = np.zeros((height,width),dtype=np.float32)
    map_x = np.zeros((height,width),dtype=np.float32)
    
    # create index map
    for i in range(height):
        for j in range(width):
            map_y[i][j]=i
            map_x[i][j]=j
    
    # deform around the right eye
    for i in range (-radius, radius):
        for j in range(-radius, radius):
            if (i**2 + j**2 > radius ** 2):
                continue
    
            if i > 0:
                map_y[right_eye[1] + i][right_eye[0] + j] = right_eye[1] + (i/radius)**power * radius
            if i < 0:
                map_y[right_eye[1] + i][right_eye[0] + j] = right_eye[1] - (-i/radius)**power * radius
            if j > 0:
                map_x[right_eye[1] + i][right_eye[0] + j] = right_eye[0] + (j/radius)**power * radius
            if j < 0:
                map_x[right_eye[1] + i][right_eye[0] + j] = right_eye[0] - (-j/radius)**power * radius
    
    warped=cv2.remap(img,map_x,map_y,cv2.INTER_LINEAR)
    cv2.imshow('warp.jpg', warped)



class Capture():
    def __init__(self):
        pass


def get_contours(contours):
    pass        
def deform_mouth(mouth_param):
    pass
def deform_eyes(eye_param):
    pass
def add_caption(message):
    pass
def smile(mouth):
   f =  dist.euclidean(mouth[3], mouth[11])+dist.euclidean(mouth[4],mouth[10])+dist.euclidean(mouth[5],mouth[9])
   g = dist.euclidean(mouth[0],mouth[7])
   return (f/(3*g))

def eye_landmarks(eyes):
    res = 0
    for idx in range(len(eyes)):
        #print("EYE {}, VALUE {}".format(idx,eyes[idx]))
        res += dist.euclidean(eyes[idx],eyes[0])
    res /= len(eyes)
    return res

mot = MotionModule()
        
    
while(True):
    
    ret, frame1 = video_capture.read()
    #frame1 = vidcap.read()
    prev_frame = frame1[:]
    #frame1= vidcap.read()
    ret, frame1 = video_capture.read()

    # Resize frame of video to 1/4 size for faster processing
    prev_small_frame = cv2.resize(prev_frame, (0, 0), fx=0.25, fy=0.25)
    small_frame = cv2.resize(frame1, (0, 0), fx=0.25, fy=0.25)

    # Convert the image from BGR color (which OpenCV uses) to RGB color 
    prev_rgb_small_frame = prev_small_frame[:, :, ::-1]
    rgb_small_frame = small_frame[:, :, ::-1]

    grayscale = cv2.cvtColor(rgb_small_frame, cv2.COLOR_BGR2GRAY)
    LAB = cv2.cvtColor(rgb_small_frame,cv2.COLOR_BGR2LAB)
    
    #cv2.imshow("prev",prev_rgb_small_frame)
    #cv2.imshow("cur",rgb_small_frame)
    #print(type(rgb_small_frame))
    #mot.exchange_frames(rgb_small_frame)
    DETPIC = mot.detect_motion(prev_rgb_small_frame,rgb_small_frame)
    #inflate_right_eye(grayscale)
    
    
        
    '''
    try:
        faces = face_cascade.detectMultiScale(grayscale, 1.3, 5)
        for (x,y,w,h) in faces:
            img = cv2.rectangle(grayscale,(x,y),(x+w,y+h),(255,0,0),2)
            roi_gray = grayscale[y:y+h, x:x+w]
            roi_color =prev_rgb_small_frame[y:y+h, x:x+w]
            eyes = eye_cascade.detectMultiScale(roi_gray)
            for (ex,ey,ew,eh) in eyes:
                cv2.rectangle(roi_color,(ex,ey),(ex+ew,ey+eh),(0,255,0),2)
    except Exception:
        raise
    '''
    
    
    rects = detector(frame1,0)
    for rect in rects:
       shape = predictor(grayscale, rect)
       shape = face_utils.shape_to_np(shape)
       mouth= shape[mStart:mEnd]
       left_eye = shape[mStart2:mEnd2]
       right_eye = shape[mStart3:mEnd3]
       left_eyebrow = shape[mStart4:mEnd4]
       right_eyebrow = shape[mStart5:mEnd5]
       nose = shape[mStart6:mEnd6]
       mouthHull = cv2.convexHull(mouth)
       one_hull = cv2.convexHull(left_eye)
       two_hull = cv2.convexHull(right_eye)
       three_hull = cv2.convexHull(left_eyebrow)
       four_hull = cv2.convexHull(right_eyebrow)
       five_hull = cv2.convexHull(nose)
       #print("{},{}".format(mStart,mEnd))
       cv2.drawContours(frame1, [mouthHull], -1, (255,255,0), 1)
       cv2.drawContours(frame1, [one_hull], -1, (0,255,255), 1)
       cv2.drawContours(frame1, [two_hull], -1, (255,255,0), 1)
       cv2.drawContours(frame1, [three_hull], -1, (128,0,128), 1)
       cv2.drawContours(frame1, [four_hull], -1, (0,128,128), 1)
       cv2.drawContours(frame1, [five_hull], -1, (255,0,255), 1)
       mar= smile(mouth)
       eye_val = eye_landmarks(one_hull)
       eye_val2 = eye_landmarks(two_hull)
       cv2.putText(frame1, "Smile Detect {}".format(mar), (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0), 2)
       cv2.putText(frame1, "Closed Left Eye Detect: {}".format(eye_val), (10,50), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0), 2)
       cv2.putText(frame1, "Closed Right Eye Detect: {}".format(eye_val2), (10,70),cv2.FONT_HERSHEY_SIMPLEX, 0.5,(255,0,0),2)
    #inflate_right_eye(frame1)
       (heuristic_val, debug_val) = distance_from_eye_contour_function( ((eye_val + eye_val2)/2), DETPIC, frame1)
       cv2.putText(frame1, "MOTION HEURISTIC: {}".format(DETPIC), (10,140),cv2.FONT_HERSHEY_SIMPLEX, 0.5,(255,0,0),2)
       cv2.putText(frame1,"HEURISTIC VALUE: {}".format(heuristic_val), (10,120), cv2.FONT_HERSHEY_SIMPLEX,0.5,(255,0,0),2)
       print("HEURISTIC VALUE: {}".format(heuristic_val))
       if(heuristic_val >= SHOULD_I_TAKE_PICTURE):
         take_picture(frame1)
         MOST_RECENT_PIC = "PICTURE_" + str(INC_COUNTER) + ".jpg"
    
    cv2.imshow('vid', frame1)
    b = MOST_RECENT_PICTURE is None
    if(not b):
        cv2.imshow("Recent pic",MOST_RECENT_PIC)
    #cv2.imshow('vid2', mot.rgb_small_frame)
    fps.update()
    #time.sleep(.3)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break


fps.stop()
cv2.destroyAllWindows()
vidcap.stop()
    
    
    
    
    


