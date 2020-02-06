#!/bin/sh
#please change the gpsshost and gphost related entries in json below to match your environment.
#Please change the below path to match your environment.

GPSS_DEMO_HOME=/Users/sridharpaladugu/DEV/GPSS-Demo/demo

WORK_DIR=$GPSS_DEMO_HOME/run

APP_JAR=$GPSS_DEMO_HOME/target/gpssdemo-0.0.1-SNAPSHOT.jar

LOG_DIR=$WORK_DIR/log

ctime=$(date "+%Y-%m-%d-%H:%M:%S")
logfile=gpssdemo_$ctime.log

java -jar $APP_JAR \
  --spring.application.json='{ "spring.application.name":"GPSSDemo_1", "gpss.gpsshost":"localhost", "gpss.gpssport":5000, "gpss.gphost":"localhost", "gpss.gpport":5432, "gpss.gprole":"gpssuser", "gpss.gppass":"gprocks@2020", "gpss.gpdatabase":"gpdemo", "gpss.gpschema":"gpss_demo", "gpss.gptable":"simpleseries_1", "gpss.runmode":"ingest", "gpss.ingestbatch":1000 }' \
   > $LOG_DIR/$logfile 2>&1 &
