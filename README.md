# GpssClientDemo

A client Application demonstrating Greenplum Stream Server for Data ingestion.

The example application  demonstrates the data ingestion using micro batching with Greenplum Streaming server. The Greenplum Stream Server (GPSS) is an ETL  tool. The Greenplum Stream Server is a gRPC server. The GPSS gRPC service definition includes the operations and messages necessary to connect to Greenplum Database and examine Greenplum metadata. The service definition also includes the operations and messages necessary to write data from a client into a Greenplum Database table. For more information about gRPC, refer to the gRPC documentation. For more information on Greenplum Streaming Server please refer to GPSS Introduction.

The use case demonstrates the small batches of ingestion w/o kafka. For instnace you might have an existing ETL process based on Spring Batch or Nifi or Spark Streaming and you want to add Greenplum ETL Sink you could easily build that capability using GPSS API. The main advantage of this is you can customize the ETL and have full control of how to tranform the stream when you the out of box GPKafka or similar technologies fall short. However in this case we need to take care of the edge cases like dead letter queue for failed chunks etc.Let us explore how we can use GPSS in a small isolated usecase without the heavy workflow.

### Requirements:
1. Greenplum Database. 

2. Install GPSS package in Greenplum as per the instructions on https://gpdb.docs.pivotal.io/streaming-server/1-3-2/instcfgmgt.html

3. SSH into Greenplum Master or Standby Master with gpadmin user. In the gpadmin home create a file gpsscfg1.json file with below content. Please chance your hostname. You could choose to run GPSS on MDW and gpfdist on SMDW.
```
{
        "ListenAddress": {
                "Host": "coruscant.databank.com",
                "Port": 5000,
                "Certificate": {
                        "CertFile": "",
                        "KeyFile": "",
                        "CAFile": ""
                }
        },
        "Gpfdist": {
                "Host": "bespin.databank.com",
                "Port": 8080,
                "ReuseTables": true,
                "Certificate": {
                        "CertFile": "",
                        "KeyFile": "",
                        "CAFile": ""
                },
                "BindAddress": "0.0.0.0"
        }
}
```
4. Run the gpss process in the node where you created above file with below command
 ```  gpss gpsscfg1.json --log-dir /home/gpadmin/gpss-log & ```

5. This launches the gpss process and waiting for incoming requests.

6. Run the SQL ```GpssClientDemo/SQL/GreenplumServerside.sql``` script to create the necessary Greenplum artifacts.

7. The client Application need a persistent store(Not a GPSS requirement, i have chose to use for convinence). This sample application uses MySQL that is available on my local work station where i run the client. Please run the script ``` GpssClientHome/SQL/ClientApplication.sql``` on your data base. 

Please review and modify any user names and passwords as per your preference.

### Instructions to setup the client:
1. clone the repo
2. Run mvn clean install package.

After the code is built your should see GpssClientDemo/target/GpssClientDemo-1.0.0-SNAPSHOT.jar
Also the gpss.proto file is used to build the automated code generation for gRpc client needed to communicated with GPSS. The jar file is 40MB in size.

### Running the client
I packaged a shell script to run the spring boot application. Please navigate to the folder ``` GpssClientDemo/RUN ```.
Please edit the rundemo.sh file. This file looks as below;
```
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
  --spring.application.json='{ "spring.application.name":"GPSSDemo_1", "gpss.gpsshost":"coruscant.databank.com", "gpss.gpssport":5000, "gpss.gphost":"coruscant.databank.com", "gpss.gpport":5432, "gpss.gprole":"gpssuser", "gpss.gppass":"gprocks@2020", "gpss.gpdatabase":"gpdemo", "gpss.gpschema":"gpss_demo", "gpss.gptable":"simpleseries_1", "gpss.runmode":"ingest", "gpss.ingestbatch":1000 }' \
   > $LOG_DIR/$logfile 2>&1 &

```
As you see in the above the Springboot application is exposed some parameters. In future i will implement this more generic in the sense you could pass columns and transformations for the cloumns that need to be sinked to Greenplum.
Please make changes to hostnames, ports, user, passwords as per your configuration.

After you review and make sure the data is right save and close. Run the script in commandline.

When it runs the code ingest 1000 records per 15 seconds.There is no reason for 15 seconds just a random value used. I used Spring Schedular that wakes for every 15 seconds and fabricate some data and write as single batch to GPSS. This keep running till you kill it. Also you can update th local mysql data base to pause the ingestion.

``` update gpss_meta.hour_glass set job_signal='pause' where job_name='simpleseries_1' ```
