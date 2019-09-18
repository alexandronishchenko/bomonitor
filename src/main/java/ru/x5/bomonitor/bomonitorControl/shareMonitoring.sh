#!/bin/sh
HOST=$1
FILE=$2

#start from root!!!

#make directory structure, access. And download.
scp $FILE root@bo-$HOST:/home/mgmgkappl/bomonitor.zip
ssh root@bo-$HOST 'mkdir -p /etc/zabbix/bomonitor;
chmod -R 777 /etc/zabbix/bomonitor;
sed -i 's/StartAgents=7/StartAgents=8/' /etc/zabbix/zabbix_agentd.conf;
sed -i 's/'`grep ^Server= /etc/zabbix/zabbix_agentd.conf`'/'`grep "^Server=" /etc/zabbix/zabbix_agentd.conf`',127.0.0.1/' /etc/zabbix/zabbix_agentd.conf;
sed -i 's/'`grep ^ServerActive= /etc/zabbix/zabbix_agentd.conf`'/'`grep ^ServerActive= /etc/zabbix/zabbix_agentd.conf`',127.0.0.1,msk-dpro-app351.x5.ru/' /etc/zabbix/zabbix_agentd.conf;
sed -i 's/\# Hostname=/Hostname='`hostname`'/' /etc/zabbix/zabbix_agentd.conf;
service zabbix-agent restart'

#copying files to correct directory and starting. Add crone rule to restart if process was killed
ssh mgmgkappl@bo-$HOST 'unzip -d /etc/zabbix/bomonitor/ /etc/zabbix/bomonitor/bomonitor.zip;
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.jar /etc/zabbix/bomonitor/bomonitor.jar;
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.properties /etc/zabbix/bomonitor/bomonitor.properties;
mv /etc/zabbix/bomonitor/bomonitor/zabbixjar.sh /etc/zabbix/bomonitor/zabbixjar.sh;
mv /etc/zabbix/bomonitor/bomonitor/bomonitor.version /etc/zabbix/bomonitor/bomonitor.version;
mv /etc/zabbix/bomonitor/bomonitor/errorList /etc/zabbix/bomonitor/errorList;
rm -r /etc/zabbix/bomonitor/bomonitor/;
rm /etc/zabbix/bomonitor/bomonitor.zip;
/etc/zabbix/bomonitor/zabbixjar.sh start;
crontab -l > bomonitor;
echo "5 * * * * nohup /etc/zabbix/bomonitor/zabbixjar.sh start > /dev/null" >> bomonitor;
crontab bomonitor;
rm bomonitor;'
