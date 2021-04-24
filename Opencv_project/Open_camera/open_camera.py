'''
Step : 1
Python 3 must be installed
Check if these lib installed or not , if not run
In cmd following command.
a) pip install numpy
b)pip install matplotlib
C) pip install os-win
D) pip install opencv-python
E) pip install mediapipe
F) pip install Pillow
Step: 2
Run/open with python the open_camera.py 
Step:3 choose any of 3 modes i.e 1,2 or 3
We have 3 mode :
1 open camera by pressing 1 initially
2 open file from gallery
3 screen capture and record
Step :4 press any of operations you want of given bellow:
This is a open camera in which we can detect several things as given bellow:
h = hand detection
f = face detection
e= eye detection
m= motion detection
c= object count
v= Video recording
P= photo taking
q = closing and exit
Arrow keys= for previous saved files and nevigation file.
p = in file nevigation for parents folder
Step:5 press q to exist ant any point
'''
import cv2
import time
import numpy as np
import os
path=os.getcwd()
    
def open_img(src):
    print(src)
    img=cv2.imread(src,1)
    return img

def files(k=0,en=0):
    path=os.getcwd()
    myList = os.listdir(path)
    print(myList)
    if(en==0):
        en=(input('enter the no of file to be open p for parent dir. '))
    if(en=='p'):
        os.chdir('..')
        r,cap=files()
        if(r==1 ):
            return 1,cap
        elif(r==-1):
            return -1,cap
    else:
        path=path+'\\'+(myList[int(en)])
    if(path[-1]=='g' or path[-1]=='G'):
        img=open_img(path)
        if(k==1):
            cv2.destroyAllWindows()
            cv2.imshow(path,img)
            c=cv2.waitKey(0)& 0xff
            if(c==0):
                en=int(en)
                en+=1
                files(k=1,en=en)
            if(c==ord('q')):
                cv2.destroyAllWindows()
                return
        return -1,img
    elif(path[-1]=='4' or path[-1]=='i' or path[-1]=='v'):
        cap=cv2.VideoCapture(path)
        if(k==1):
            while(cap.isOpened()):
                ret,frame=cap.read()
                if(ret==True):
                    cv2.imshow('vedio',frame)
                    c=cv2.waitKey(1)& 0xff
                    if(c==0):
                        en=int(en)
                        en+=1
                        files(k=1,en=en)
                    if(c==ord('q')):
                        cap.release()
                        cv2.destroyAllWindows()
                        break
                cap.release()
                cv2.destroyAllWindows()
        return 1,cap
    elif(en!=('p')):
        en=int(en)
        os.chdir(myList[en])
        r,cap=files()
        if(r==1 ):
            return 1,cap
        elif(r==-1):
            return -1,cap

def opencamera(cam):
    cap = cv2.VideoCapture(cam,cv2.CAP_DSHOW)
    cap.set(cv2.CAP_PROP_FPS,30.0)
    cap.set(cv2.CAP_PROP_FOURCC,cv2.VideoWriter_fourcc('m','j','p','g'))
    cap.set(cv2.CAP_PROP_FOURCC,cv2.VideoWriter_fourcc('M','J','P','G'))
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 1080)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1920)
    return cap

# FOR CAPTURING SCREEN RATHER THAN WEBCAM
def captureScreen(bbox=(300,300,690+300,530+300)):
    capScr = np.array(ImageGrab.grab(bbox))
    capScr = cv2.cvtColor(capScr, cv2.COLOR_RGB2BGR)
    return capScr

def face_eye_detection(i,d=0):
    gray = cv2.cvtColor(i, cv2.COLOR_BGR2GRAY)
    faces = f.detectMultiScale(gray, 1.1, 4)
    for (x, y , w ,h) in faces:
        cv2.rectangle(i, (x,y), (x+w, y+h), (255, 0 , 0), 3)
        if d!=0:
            roi_gray = gray[y:y+h, x:x+w]
            roi_color = i[y:y+h, x:x+w]
            eyes = E.detectMultiScale(roi_gray)
            for (ex, ey ,ew, eh) in eyes:
                cv2.rectangle(roi_color, (ex,ey), (ex+ew, ey+eh), (0, 255, 0), 5)

def object_count(im,o=0):
    i=cv2.cvtColor(im,cv2.COLOR_BGR2GRAY)
    i=cv2.medianBlur(i,5)
    if o==0:
        r,th=cv2.threshold(i,128,255,cv2.THRESH_BINARY)  #best
    elif o==1:
        th=cv2.adaptiveThreshold(i,120,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,11,2)
    elif o==2:
        th=cv2.adaptiveThreshold(i,255,cv2.ADAPTIVE_THRESH_MEAN_C,cv2.THRESH_BINARY, 11,2)
    r,markers=cv2.connectedComponents(th)
    #print("obects are=",r-1)
    cv2.putText(im,'object in image are:'+str(r-1),(0,im.shape[0]-10),cv2.FONT_HERSHEY_TRIPLEX,1,(255,255,255),1)
    return r-1

def motion_detector(i,j,o=0):
        diff=cv2.absdiff(i,j)
        kernel=np.ones((5,5),np.uint8)
        blur=cv2.GaussianBlur(cv2.cvtColor(diff,cv2.COLOR_BGR2GRAY),(5,5),0)
        r,th=cv2.threshold(blur,20,255,cv2.THRESH_BINARY)
        dilated=cv2.dilate(th,kernel,iterations=4)
        contours,th=cv2.findContours(dilated,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        for cnt in contours:
            (x,y,w,h)=cv2.boundingRect(cnt)
            if cv2.contourArea(cnt)<1000:
                continue
            cv2.rectangle(j,(x,y),(x+w,y+h),(0,255,255),2)
            cv2.putText(j,'status: {}'.format('movement'),(10,20),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX,1,(0,0,255),2)
        if o==1:
            cv2.drawContours(j,contours,-1,(255,255,20),2)

def hand_detection(img):
    mpHands = mp.solutions.hands
    hands = mpHands.Hands()
    mpDraw = mp.solutions.drawing_utils
    imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    results = hands.process(imgRGB)
    #print(results.multi_hand_landmarks)
    if results.multi_hand_landmarks:
        for handLms in results.multi_hand_landmarks:
            for id, lm in enumerate(handLms.landmark):
                #print(id, lm)
                h, w, c = img.shape
                cx, cy = int(lm.x * w), int(lm.y * h)
                #print(id, cx, cy)
                if id == 4:
                    cv2.circle(img, (cx, cy), 15, (255, 0, 255), cv2.FILLED)
            mpDraw.draw_landmarks(img, handLms, mpHands.HAND_CONNECTIONS)

def main_menu(img):
    cv2.putText(img,"WELCOME TO JAY CAMERA",(100,50),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX,1,(0,0,255),2,cv2.LINE_AA)
    cv2.putText(img,"h=HAND f=Face v=record\stop  p=photo e=Eye m=Motion c=count q=EXIT",(0,img.shape[0]-10),cv2.FONT_HERSHEY_TRIPLEX,0.8,(0,255,0),1)

cam=0
cf=0

print("Choose option \n1. for camera \n2. open file\n3.for screen record\n")
t=int(input())
if(t==1):
    cap=opencamera(cam)
elif(t==2):
    ov,cap=files()
elif(t==3):
    from PIL import ImageGrab
    cf=1
vf=0
flag=0
f=None
t=7
ch=None
tim1=time.time()
#Author @Jay prakash pandey
while(True):
    if cf==1:
        img=captureScreen()
    else:
        try:
            success, img = cap.read(-1)
        except:
            img=cap
    if(t==1):
        hand_detection(img)
    if(t==2):
        face_eye_detection(img)
    if(t==3):
        face_eye_detection(img,d=1)
    if(t==4):
        motion_detector(f1,img)
        if(cf==0):
            _,f1=cap.read()
        else:
            f1=img
    if(t==5):
        object_count(img)
    if(vf==1):
        if(flag==1):
            save.write(img)
            tim2=time.time()-tim1
            st=str(round(tim2,2))
            cv2.putText(img, st, (10, 70), cv2.FONT_HERSHEY_PLAIN, 3,(255, 0, 255), 3)
        elif(flag==2):
            flag=0
            save.release()
    if(t==7):
        if(time.time()-tim1<=8):
            main_menu(img)
    cv2.imshow("Image", img)
    ch=(cv2.waitKey(1) & 0xff)
### Now to call different operations on img by pressing key
    if(ch==ord('q')):
        break
    if(ch==ord('h')):
        import mediapipe as mp
        t=1
    if(ch==ord('f')):
        f= cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        t=2
    if(ch==ord('e')):
        if(f==None):
            f= cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        E = cv2.CascadeClassifier('haarcascade_eye_tree_eyeglasses.xml')
        t=3
    if(ch==ord('m')):
        if(cf==0):
            _,f1=cap.read() 
        else:
            f1=img
        t=4
    if(ch==ord('v')):
        cap.set(cv2.CAP_PROP_FRAME_HEIGHT,640)
        cap.set(cv2.CAP_PROP_FRAME_WIDTH,480)
        tim=time.localtime()
        tim1=time.time()
        times=time.strftime("%H_%M_%S",tim)
        fourcc=cv2.VideoWriter_fourcc(*'XVID')
        save=cv2.VideoWriter((times + ".avi"),fourcc,30.0,(640,480))
        flag+=1
        vf=1
    if(ch==ord('p')or ch==ord(' ')):
        tim=time.localtime()
        times=time.strftime("%H_%M_%S",tim)
        cv2.imwrite((times+".PNG"),img)
    if(ch==ord('c')):
        t=5
    if(ch==ord('s')):
        tim1=time.time()
        t=7 
    if(ch==0):
        if(cf==0):
            cap.release()
        cv2.destroyAllWindows()
        files(1)
        cap.release()
        cv2.destroyAllWindows()
        break
cap.release()
cv2.destroyAllWindows()
