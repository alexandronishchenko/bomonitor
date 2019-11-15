#!/bin/sh

version(){
  cur_ver=`cat /etc/zabbix/bomonitor/bomonitor.version`
  echo "curent version $cur_ver"
}

getpid(){
if [ -f /tmp/bomonitor.pid ]
then
echo `cat /tmp/bomonitor.pid`
else
echo '9999999'
fi
}
start(){

echo 'starting bomonitor agent'
#isrun="`checkInstance`"
if [ -f /tmp/bomonitor.pid ]
then
pid=`cat /tmp/bomonitor.pid`
else
pid='9999999'
fi



proc=`ps aux | grep "$pid" | grep '/zabbix/bomonitor/bomonitor.jar'`
if [ -n "$proc" ]
then
isrun="run"
else
isrun="no"
fi

if [ $isrun = "no" ];
then
#Проверка на - запущен ли скрипт запуска
CUR_PID=`date +"%H:%M"`
zabbixjar_ps=`ps aux | grep zabbixjar | grep zabbixjar.sh | grep -v color=auto | grep -v gedit| grep -v $$| grep -v grep|grep -v $CUR_PID`
sleep 2
if [ -n "$zabbixjar_ps" ];
then
echo "already running zabbixjar.sh"
exit 0
fi
#Проверка на занятость порта. Проверять, пока не освободиться.
PORT_IS_BUSY=`netstat -ap | grep 10055`
#echo $PORT_IS_BUSY
while [ -n "$PORT_IS_BUSY" ]
do
#echo "busy"
PORT_IS_BUSY=`netstat -ap | grep 10055`
sleep 1
done
#echo "free"
nohup /usr/local/gkretail/jdk/oracle/1.8.0_201/jre/bin/java -jar -Xms16m -Xmx100m /etc/zabbix/bomonitor/bomonitor.jar zabbix > /dev/null & echo $! > /tmp/bomonitor.pid &
#fi
else
echo "already running"
fi


}

stop(){
kill -TERM `getpid`
echo "stopped"
}

restart(){
stop
sleep 5
start
}
update(){
#check curent version running
cur_ver=`cat /etc/zabbix/bomonitor/bomonitor.version`
echo "curent version $cur_ver"
#check remote last version
UPDATE_HOST='192.168.153.25'
#scp mgmgkappl@${UPDATE_HOST}:/home/mgmgkappl/bomonitor/currentVersion /etc/zabbix/bomonitor/currentVersion
wget -O /etc/zabbix/bomonitor/currentVersion http://${UPDATE_HOST}/bomonitor/archive/currentVersion
REMOTE_VER=`cat /etc/zabbix/bomonitor/currentVersion`
if [ "$cur_ver" \< "$REMOTE_VER" ];
then
echo "needs to update"
stop
wget -O /etc/zabbix/bomonitor/bomonitor.zip http://${UPDATE_HOST}/bomonitor/archive/bomonitor.zip
unzip -d /etc/zabbix/bomonitor/ /etc/zabbix/bomonitor/bomonitor.zip
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.jar /etc/zabbix/bomonitor/bomonitor.jar
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.properties /etc/zabbix/bomonitor/bomonitor.properties
mv /etc/zabbix/bomonitor/bomonitor/zabbixjar.sh /etc/zabbix/bomonitor/zabbixjar.sh
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.version /etc/zabbix/bomonitor/bomonitor.version
mv /etc/zabbix/bomonitor/bomonitor/errorList /etc/zabbix/bomonitor/errorList
rm -r /etc/zabbix/bomonitor/bomonitor/
rm /etc/zabbix/bomonitor/bomonitor.zip
echo "updated"
start
else
echo "no updates"
fi
#smbclient -U client //${UPDATE_HOST}/bomonitor
#cat
#REMOTE_VER=`echo smb://${UPDATE_HOST}/bomonitor/archive/currentVersion`
echo "remote version: $REMOTE_VER , local version was: $cur_ver"
#stop,download and start
#}


#checkInstance(){
#
}




#mvn clean compile assembly:single
#getpid
if [ "$1" = "start" ];
then
echo "starting"
start
elif [ "$1" = "stop" ];
then
echo stoping
stop
elif [ "$1" = "update" ];
then
update
elif [ "$1" = "restart" ];
then
echo restarting
restart
elif [ "$1" = "version" ];
then
version
else
  echo "wrong parameter. It should be: start,stop,restart,update,version"
fi
