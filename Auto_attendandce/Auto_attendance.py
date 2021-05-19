import cv2
import numpy as np
import face_recognition
import os
from datetime import datetime
from PIL import ImageGrab
try:
    os.makedirs('Auto_Attendance/Attendee_images',exist_ok=True)
except OSError as error:
    pass
path = 'Auto_Attendance/Attendee_images'
images = []
classNames = []
myList = os.listdir(path)
print(myList)
if(len(myList)==0):
    print('please add images in attendee folder and retry')
    input()
    quit()
for cl in myList:
    curImg = cv2.imread(f'{path}/{cl}')
    images.append(curImg)
    classNames.append(os.path.splitext(cl)[0])
print(classNames)
 
def findEncodings(images):
    encodeList = []
    for img in images:
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = face_recognition.face_encodings(img)[0]
        encodeList.append(encode)
    return encodeList
 
def markAttendance(name):
    try:
        fa=open('Auto_Attendance/Attendance.csv','r+')
    except:
        fa=open('Auto_Attendance/Attendance.csv','w+')
    with fa as f:
        myDataList = f.readlines()
        nameList = []
        for line in myDataList:
            entry = line.split(',')
            nameList.append(entry[0])
        if name not in nameList:
            now = datetime.now()
            dtString = now.strftime('%H:%M:%S')
            f.writelines(f'\n{name},{dtString}')
 
#### FOR CAPTURING SCREEN RATHER THAN WEBCAM
def captureScreen(bbox=(300,300,690+300,530+300)):
    capScr = np.array(ImageGrab.grab(bbox))
    capScr = cv2.cvtColor(capScr, cv2.COLOR_RGB2BGR)
    return capScr
 
encodeListKnown = findEncodings(images)
print('Encoding Complete')
 
def opencamera(cam):
    cap = cv2.VideoCapture(cam,cv2.CAP_DSHOW)
    cap.set(cv2.CAP_PROP_FPS,30.0)
    cap.set(cv2.CAP_PROP_FOURCC,cv2.VideoWriter_fourcc('m','j','p','g'))
    cap.set(cv2.CAP_PROP_FOURCC,cv2.VideoWriter_fourcc('M','J','P','G'))
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1920)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1080)
    return cap
s=1
sf=None
ch=(input('choose 1.webcam 2.screen recorder to turnoff showing window any time press s\n'))
try:
    ch=int(ch)
except:
    quit()
if((ch)==1):
    cap=opencamera(0)
elif((ch)!=2):
    quit()
while True:
    try:
        success, img = cap.read()
    except:
        img = captureScreen()
    imgS = cv2.resize(img,(0,0),None,0.25,0.25)
    imgS = cv2.cvtColor(imgS, cv2.COLOR_BGR2RGB)
    facesCurFrame = face_recognition.face_locations(imgS)
    encodesCurFrame = face_recognition.face_encodings(imgS,facesCurFrame)
    for encodeFace,faceLoc in zip(encodesCurFrame,facesCurFrame):
        matches = face_recognition.compare_faces(encodeListKnown,encodeFace)
        faceDis = face_recognition.face_distance(encodeListKnown,encodeFace)
        #print(faceDis)
        matchIndex = np.argmin(faceDis)
        if matches[matchIndex]:
            name = classNames[matchIndex].upper()
            #print(name)
            y1,x2,y2,x1 = faceLoc
            y1, x2, y2, x1 = y1*4,x2*4,y2*4,x1*4
            cv2.rectangle(img,(x1,y1),(x2,y2),(0,255,0),2)
            cv2.rectangle(img,(x1,y2-35),(x2,y2),(0,255,0),cv2.FILLED)
            cv2.putText(img,name,(x1+6,y2-6),cv2.FONT_HERSHEY_COMPLEX,1,(255,255,255),2)
            markAttendance(name)
    if(s!=0 or sf=='s'):
        cv2.imshow('Webcam',img)
    else:
        sf=input()
    ch=(cv2.waitKey(8) & 0xff)
    if(ch==ord('s')):
        if(s==1):
            cv2.destroyAllWindows()
            s=0
            sf=None
        else:
            s=1
    if(ch==ord('q')or sf=='q'):
        break
try:
    cap.release()
except:
    img.release()
cv2.destroyAllWindows()