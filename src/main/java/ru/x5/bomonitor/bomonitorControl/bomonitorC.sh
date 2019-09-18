#!/bin/sh

HOSTS=$2

update(){
  for i in `cat $HOSTS`
      do
        echo BO-$i
      ssh mgmgkappl@bo-$i /etc/zabbix/bomonitor/zabbixjar.sh update
  done
}
status(){
  for i in `cat $HOSTS`
      do
        echo BO-$i
      ssh mgmgkappl@bo-$i '/etc/zabbix/bomonitor/zabbixjar.sh version;  ps aux | grep bomon'
  done
}
version(){
  for i in `cat $HOSTS`
      do
        echo BO-$i
      ssh mgmgkappl@bo-$i /etc/zabbix/bomonitor/zabbixjar.sh version
  done
}
share(){
  echo 'run other script: shareStarter.sh'
}
test(){
  for i in `cat $HOSTS`
  do
    echo BO-$i
  echo $i
  done
}
uploadProperties(){
 for i in `cat $HOSTS`
 do
  echo BO-$i
  scp /etc/zabbix/bomonitor/bomonitor.properties mgmgkappl@bo-$i:/etc/zabbix/bomonitor/bomonitor.properties
  #ssh mgmgkappl@bo-$i '/etc/zabbix/bomonitor/zabbixjar.sh restart'
done
}
uploadJAR(){
 for i in `cat $HOSTS`
 do
  echo BO-$i
  scp /etc/zabbix/bomonitor/bomonitor.jar mgmgkappl@bo-$i:/etc/zabbix/bomonitor/bomonitor.jar
  ssh mgmgkappl@bo-$i '/etc/zabbix/bomonitor/zabbixjar.sh restart'
done
}
uploadsh(){
 for i in `cat $HOSTS`
 do
  echo BO-$i
  scp /etc/zabbix/bomonitor/zabbixjar.sh mgmgkappl@bo-$i:/etc/zabbix/bomonitor/zabbixjar.sh
  #ssh mgmgkappl@bo-$i '/etc/zabbix/bomonitor/zabbixjar.sh restart'
done
}
if [ "$1" = "status" ];
then
status
elif [ "$1" = "update" ];
then
update
elif [ "$1" = "share" ];
then
share
elif [ "$1" = "version" ];
then
version
elif [ "$1" = "test" ];
then
test
elif [ "$1" = "uploadprop" ];
then
uploadProperties
elif [ "$1" = "uploadjar" ];
then
uploadsh
elif [ "$1" = "uploadsh" ];
then
uploadsh
else
  echo "wrong parameter. It should be: status,update,share,version,uploadprop list_file ,uploadjar list_file, uploadsh list "
fi
