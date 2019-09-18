#!/bin/sh
echo 1-st list of bo
echo file of distr

HOSTS=$1
FILE=$2
for i in `cat $HOSTS`
    do 
	`pwd`/shareMonitoring.sh $i $FILE
done

