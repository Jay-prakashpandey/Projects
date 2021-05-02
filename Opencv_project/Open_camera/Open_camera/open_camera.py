import cv2
import time
import numpy as np
import os
import mediapipe as mp
path=os.getcwd()

epf=0
evf=0
ecf=0
eef=0
emf=0
ehf=0
eff=0
eqf=0
egf=0
h=None
ov=0
k=0
def encode(x,y):
    global epf,ecf,eef,emf,ehf,eff,eqf,egf,h,ov
    tem=check_icon(x,y,h)
    if(tem==1):
        if(ecf==0):
            ecf=1
        else:
            ecf=0
    if(tem==2):
        if(eef==0):
            eef=1
        else:
            eef=0
    if(tem==3):
        if(emf==0):
            emf=1
        else:
            emf=0
    if(tem==4):
        if(ehf==0):
            ehf=1
        else:
            ehf=0
    if(tem==5):
        if(eff==0):
            eff=1
        else:
            eff=0
    if(tem==6):
        if(eqf==0):
            eqf=1
        else:
            eqf=0
    if(tem==7):
        if(egf==0):
            egf=1
        else:
            egf=0
    if(tem==8 and ov!=-1):
        global vf
        vf=1
    if(tem==9):
        if(epf==0):
            epf=1
        else:
            epf=0

def check_icon(x,y,h):
    if(y<60):
        if(x>450 and x<498):
            return 1
        if(x>560 and x<611):
            return 2
        if(x>150 and x<201):
            return 3
        if(x>10 and x<58):
            return 4
        if(x>300 and x<348):
            return 5
        if(x>h-49 and x<h-1):
            return 6
    if(y>300):
        if(x>h-42 and x<h-1):
            return 7       
        if(x>300 and x<348):
            return 8
        if(x>h-450 and x<h-407):
            return 9
  
def click_event(event,x,y,flags,param):
    if event == cv2.EVENT_LBUTTONDOWN:
        encode(x,y)
        #print(x,', ' ,y)
        font = cv2.FONT_HERSHEY_SIMPLEX
        strXY = str(x) + ', '+ str(y)
        cv2.putText(img, strXY, (x, y), font, .5, (255, 255, 0), 2)
        cv2.imshow('Image', img)
    if event == cv2.EVENT_RBUTTONDOWN:
        global t,tim1
        tim1=time.time()
        if(t!=7):
            t=7
        else:
            t=0
        
def open_img(src):
    print(src)
    img=cv2.imread(src,1)
    return img

def files(en=0):
    global k
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
        if(int(en)>=len(myList)or len(myList)==0):
            print("There is no file please input in range of 0-",len(myList))
            return -2,None
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
                    files(en=en)
                if(c==ord('q')):
                    cv2.destroyAllWindows()
                    return -2,None
            return -1,img
        elif(path[-1]=='4' or path[-1]=='i' or path[-1]=='v'):
            cap=cv2.VideoCapture(path)
            if(k==1):
                cv2.destroyAllWindows()
                while(cap.isOpened()):
                    ret,frame=cap.read()
                    if(ret==True and cap.isOpened()):
                        cv2.imshow('vedio',frame)
                        c=(cv2.waitKey(25)& 0xff)
                        if(c==0):
                            en=int(en)
                            en+=1
                            files(en=en)
                        if(c==ord('q')):
                            cap.release()
                            cv2.destroyAllWindows()
                            break
                cap.release()
                cv2.destroyAllWindows()
            return 1,cap
        elif(en!=('p')):
            en=int(en)
            if(en>=len(myList)):
                print("There is no file please input in range of 0-",len(myList))
                return -2,None
            else:
                os.chdir(myList[en])
                r,cap=files()
                if(r==1 ):
                    return 1,cap
                elif(r==-1):
                    return -1,cap

def opencamera(cam):
    if(cam==1):
        cap=cv2.VideoCapture(cam)
        return cap
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
    global f
    gray = cv2.cvtColor(i, cv2.COLOR_BGR2GRAY)
    if(f==None):
        f= cv2.CascadeClassifier('C:\\Users\\Prakash-pandey\\OneDrive\\Desktop\\courses\\Projects\\Open_camera\\resourses\\haarcascade_frontalface_default.xml')
    faces = f.detectMultiScale(gray, 1.1, 4)
    for (x, y , w ,h) in faces:
        if(d==0):
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
def takepic(img):
        tim=time.localtime()
        times='images\\'+time.strftime("%H_%M_%S",tim)
        cv2.imwrite((times+".PNG"),img)

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
                h, w, c = img.shape
                cx, cy = int(lm.x * w), int(lm.y * h)
                if(id==12):
                    if(cy<57 or cy>300):
                        encode(cx,cy)
                    cv2.circle(img, (cx, cy), 15, (255, 0, 255), cv2.FILLED)
            mpDraw.draw_landmarks(img, handLms, mpHands.HAND_CONNECTIONS)
                
def icon(img):
    img[0:48,450:498]=img1
    img[0:47,560:611]=img2
    img[0:51,150:201]=img3
    img[0:49,10:58]=img4
    img[0:48,300:348]=img5  
    img[0:48,-49:-1]=img6
    img[-46:-1,-42:-1]=img7
    img[-49:-1,300:348]=img8
    img[-46:-1,-450:-407]=img9

def main_menu(img):
    cv2.putText(img,"WELCOME TO JAY CAMERA",(200,100),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX,1,(0,0,255),2,cv2.LINE_AA)
    cv2.putText(img,"h=HAND f=Face v=record\stop  p=photo e=Eye m=Motion c=count q=EXIT",(0,img.shape[0]-70),cv2.FONT_HERSHEY_TRIPLEX,0.8,(0,255,0),1)
cam=0
cf=0
img1=cv2.imread('resourses\\c.PNG',1)
img2=cv2.imread("resourses\\eyes.JFIF",1)
img3=cv2.imread("resourses\\m.PNG",1)
img4=cv2.imread("resourses\\hand.JFIF",1)
img5=cv2.imread("resourses\\fc.PNG",1)  
img6=cv2.imread("resourses\\quit.png",1)
img7=cv2.imread("resourses\\G.JFIF",1)
img8=cv2.imread("resourses\\video.JPG",1)
img9=cv2.imread("resourses\\sutter.PNG",1)
print("Choose option \n1. for camera \n2. open file\n3.for screen record\n")
t=int(input())
if(t==1):
    cap=opencamera(cam)
elif(t==2):
    ov,cap=files()
    if(ov==-2):
        print('retry')
        quit()
elif(t==3):
    from PIL import ImageGrab
    cf=1
vf=0
E=None
flag=0
f=None
t=7
ch=None
tim1=time.time()

while(True):
    if cf==1:
        img=captureScreen()
    else:
        try:
            success, img = cap.read(-1)
        except:
            img=cap.copy()
    if(ehf==1):
        hand_detection(img)
    if(eff==1):
        face_eye_detection(img)
    if(eef==1):
        face_eye_detection(img,d=1)
    if(epf==1):
        takepic(img)
        epf=0
    if(emf==1):
        motion_detector(f1,img)
        if(cf==0):
            try:
                _,f1=cap.read()
            except:
                f1=cap.copy()
        else:
            f1=img
    if(ecf==1):
        object_count(img)
    if(evf==1):
        if(flag==1):
            save.write(img)
            tim2=time.time()-tim1
            st=str(round(tim2,2))
            cv2.putText(img, st, (10, 70), cv2.FONT_HERSHEY_PLAIN, 3,(255, 0, 255), 3)
        elif(flag==2):
            flag=0
            evf=0
            save.release()
            cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080)
            cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920)
    if(t==7):
        if(time.time()-tim1<=8):
            main_menu(img)
    icon(img)
    h=img.shape[1]
    cv2.imshow("Image", img)
    cv2.setMouseCallback('Image',click_event)
    if(ov==1):
        ch=(cv2.waitKey(30) & 0xff)
    else:
        ch=(cv2.waitKey(15) & 0xff)
### Now to call different operations on img by pressing key
    if(ch==ord('q') or eqf==1):
        break
    if(ch==ord('h')):
        if(ehf!=1):
            ehf=1
        else:
            ehf=0
    if(ch==ord('f')or eff==1):
        if(f==None):
            f= cv2.CascadeClassifier('C:\\Users\\Prakash-pandey\\OneDrive\\Desktop\\courses\\Projects\\Open_camera\\resourses\\haarcascade_frontalface_default.xml')
            eff=1
        elif(eff==1 and ch==ord('f')):
            eff=0
    if(ch==ord('e')or eef==1):
        if(f==None):
            f= cv2.CascadeClassifier('C:\\Users\\Prakash-pandey\\OneDrive\\Desktop\\courses\\Projects\\Open_camera\\resourses\\haarcascade_frontalface_default.xml')
        if(E==None):
            E = cv2.CascadeClassifier('C:\\Users\\Prakash-pandey\\OneDrive\\Desktop\\courses\\Projects\\Open_camera\\resourses\\haarcascade_eye_tree_eyeglasses.xml')
            eef=1
        elif(eef==1 and ch==ord('e')):
            eef=0
    if(ch==ord('m')or emf==1):
        if(emf==1 and ch==ord('m')):
            emf=0
        else:
            if(cf==0):
                try:
                    _,f1=cap.read()
                except:
                    f1=cap.copy() 
            else:
                f1=img
            emf=1
    if(ch==ord('v')or vf==1):
        if(evf==0):
            cap.set(cv2.CAP_PROP_FRAME_HEIGHT,640)
            cap.set(cv2.CAP_PROP_FRAME_WIDTH,480)
            tim=time.localtime()
            tim1=time.time()
            times='videos\\'+time.strftime("%H_%M_%S",tim)
            fourcc=cv2.VideoWriter_fourcc(*'XVID')
            save=cv2.VideoWriter((times + ".avi"),fourcc,30.0,(640,480))
            if(vf==1):
                vf=0
            evf=1
        if(evf==1):
            flag+=1
            vf=0
    if(ch==ord('p')or ch==ord(' ')):
        epf=1
    if(ch==ord('c')):
        if(ecf!=1):
            ecf=1
        else:
            ecf=0
    if(ch==ord('s')):
        tim1=time.time()
        t=7 
    if(ch==0 or egf==1):
        if(cf==0):
            try:
                cap.release()
            except:
                pass
        cv2.destroyAllWindows()
        k=1
        files()
        k=0
        try:
            cap.release()
        except:
            pass
        cv2.destroyAllWindows()
        break
try:
    cap.release()
except:
    pass
cv2.destroyAllWindows()
