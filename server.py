import paho.mqtt.client as mqtt
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import threading
import subprocess
from subprocess import call
import pickle
import socket

def get_ip_address():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    return s.getsockname()[0]

def on_connect(client, userData, rc):
    print("Connected with result code "+str(rc))
    client.subscribe([("android",0),("server",0)])

def on_message(client, userData, msg):
    print(msg.topic + " " + str(msg.payload))
    global sender
    global password
    global event
    message = msg.payload[:-3].decode()
    if message[:4] == 'mail':
        temp = message[5:].split()
        if len(temp) == 2 :
            sender = temp[0]
            password = temp[1]
            print("sender : " + sender +"\npassword : " + password)
            print(sender+" "+password)
            event.set()
            dic = {'mail':sender, 'passwd':password}
            F = open('homeData.pkl','wb')
            pickle.dump(dic, F)
            F.close()
    if  message == "kgt" :
        sendMail('Home Emergency','Gas leak detected in the kitchen!Hurry Now!')
        call(["mpg123", 'gas_leak.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    elif message == "kwt":
        sendMail('Home Emergency','Water leak detected!Hurry!'
                )
        call(["mpg123", 'water_leak.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    elif message == "kft":
        sendMail( 'Home Emergency','Fire detected!\nHurry'
                )
        call(["mpg123", 'Fire_detected.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    elif message.startswith("bt"):
        temperature = int(message[3:])
        if temperature < 10:
            sendMail( 'Home Emergency','Temperature level is down to '+str(temperature)+'\nHurry')
            call(["mpg123", 'temperature_not_normal.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    elif message.startswith("bdo"):
        call(["mpg123", 'baby_door_open.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    elif message.startswith("bdc"):
        call(["mpg123", 'baby_room_closed.mp3'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


def sendMail(subject, messaje):
    print("Sending an email...")
    global sender
    global password
    global event
    def doIt():
        if  len(sender) == 0 :
            print("gonna wait until you give the password")
            client.publish("android","mail");
            event.wait()
            print("something happened i must wake up")
        try:
            msg = MIMEMultipart()
            msg['From'] = sender
            msg['To'] = sender
            msg['Subject'] = subject
            message = messaje
            msg.attach(MIMEText(message))
            mailserver = smtplib.SMTP('smtp.gmail.com', 587)
            mailserver.ehlo()
            mailserver.starttls()
            mailserver.ehlo()
            mailserver.login(sender, password)
            mailserver.sendmail(sender, sender, msg.as_string())
            mailserver.quit()
            print("mail sent successfully to "+sender+".")
        except:
            print("something went wrong and i couldn't send the mail!")
        event.clear()
    thread = threading.Thread()
    thread.run = doIt
    thread.start()

#ip = input("Enter the serve's ip : ")
ip = get_ip_address()
#port = int(input("Enter the serve's port : "))
port = 1883
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(ip, port, 60)

sender=""
password=""
retCode = call(["ls","homeData.pkl"],stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

if retCode == 0:
    f= open('homeData.pkl','rb')
    dic = pickle.load(f)
    sender=dic['mail']
    password=dic['passwd']
else:
    client.publish("android","mail");

event = threading.Event()
client.loop_forever()
