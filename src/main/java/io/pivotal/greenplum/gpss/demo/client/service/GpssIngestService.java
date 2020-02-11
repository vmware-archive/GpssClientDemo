/**
 * 
 */
package io.pivotal.greenplum.gpss.demo.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Timestamp;

import api.CloseRequest;
import api.ColumnInfo;
import api.ConnectRequest;
import api.DescribeTableRequest;
import api.GpssGrpc;
import api.InsertOption;
import api.ListSchemaRequest;
import api.ListTableRequest;
import api.OpenRequest;
import api.RelationType;
import api.RowData;
import api.Schema;
import api.Session;
import api.TableInfo;
import api.TransferStats;
import api.WriteRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.pivotal.greenplum.gpss.demo.client.config.ApplicationProperties;

/**
 * @author sridharpaladugu
 *
 */
@Component
public class GpssIngestService {

	static final Log log = LogFactory.getLog(GpssIngestService.class);

	ManagedChannel channel = null;
	Session mSession = null;
	GpssGrpc.GpssBlockingStub bStub = null;

	@Autowired
	ApplicationProperties props;
	/**
	 * Verify the basic configuration;
	 * 1. connect to GPSS
	 * 2. Verify GP database connection
	 * 3. Verify we can query the DB metadata to make sure we have access to schema 
	 */
	public void checkEnv() {
		openGpssChannel();
		connectToGP();
		List<String> schemaNameList = listSchemas();
		schemaNameList.forEach(schema -> log.debug("Schema Name ->" + schema));
		List<String> tables = listTables(props.getGpSchema());
		tables.forEach(table -> {
			log.debug("Table Name ->" + table);
			Map<String, String> columnsMap = fetchTableInfo(table);
			columnsMap.forEach( (colname, dbtype) -> {
				log.debug( "column " + colname + " type: " + dbtype );
			});
		});
		
		closeGpssChannel();
	}
	/**
	 * Ingest data to specified database table.
	 */
	public void ingestBatch() {
		log.debug("Opening GPSS channel  .......");
		openGpssChannel();
		log.debug("Opening GP connection  .......");
		connectToGP();
		log.debug("Setting up GP table to write .......");
		openTableForWrite();
		log.debug(" writing events to GP table .......");
		writeToTable();
		log.debug("Closing up GP table after write .......");
		String status = closeTable();
		log.debug( "GP table Write CloseRequest tStats: " + status);
		log.debug("Closing Channel till next iteration .......");
		closeGpssChannel();
	}

	private void openGpssChannel() {
		try {
			channel = ManagedChannelBuilder.forAddress(props.getGpssHost(), props.getGpssPort()).usePlaintext().build();
			bStub = GpssGrpc.newBlockingStub(channel);

		} catch (Exception e) {
			log.debug("Exception while connecting to Stream Server", e);
			throw new RuntimeException("Exception while connecting to Stream Server", e);
		}
	}

	private void closeGpssChannel() {
		try {
			channel.shutdown().awaitTermination(7, TimeUnit.SECONDS);

		} catch (Exception e) {
			log.debug("Exception while closing channel to Stream Server", e);
			throw new RuntimeException("Exception while closing channel to Stream Server", e);
		}
	}

	private void connectToGP() {
		try {
			ConnectRequest connReq = ConnectRequest.newBuilder().setHost(props.getGphost()).setPort(props.getGpport())
					.setUsername(props.getGprole()).setPassword(props.getGppass()).setDB(props.getGpDatabase())
					.setUseSSL(false).build();
			mSession = bStub.connect(connReq);
		} catch (Exception e) {
			log.debug("Exception while connecting to Greenplum Server", e);
			throw new RuntimeException("Exception while connecting to Greenplum Server", e);
		}
	}

	private List<String> listSchemas() {
		try {
			// create a list schema request builder
			ListSchemaRequest lsReq = ListSchemaRequest.newBuilder().setSession(mSession).build();

			// use the blocking stub to call the ListSchema service
			List<Schema> listSchema = bStub.listSchema(lsReq).getSchemasList();

			// extract the name of each schema and save in an array
			List<String> schemaNameList = new ArrayList<String>();
			for (Schema s : listSchema) {
				schemaNameList.add(s.getName());
			}
			return schemaNameList;
		} catch (Exception e) {
			log.debug("Exception while reading schema(s)", e);
			throw new RuntimeException("Exception while reading schema(s)", e);
		}
	}

	private List<String> listTables(String schemaName) {
		try {
			// create a list table request builder
			ListTableRequest ltReq = ListTableRequest.newBuilder().setSession(mSession).setSchema(schemaName).build();

			// use the blocking stub to call the ListTable service
			List<TableInfo> tblList = bStub.listTable(ltReq).getTablesList();

			// extract the name of each table only and save in an array
			List<String> tblNameList = new ArrayList<String>();
			for (TableInfo ti : tblList) {
				if (ti.getTypeValue() == RelationType.Table_VALUE) {
					tblNameList.add(ti.getName());
				}
			}
			return tblNameList;
		} catch (Exception e) {
			log.debug("Exception while reading table(s)", e);
			throw new RuntimeException("Exception while reading table(s)", e);
		}
	}

	
	private Map<String, String> fetchTableInfo(String tableName) {
				// create a describe table request builder
		DescribeTableRequest dtReq = DescribeTableRequest.newBuilder()
		    .setSession(mSession)
		    .setSchemaName(props.getGpSchema())
		    .setTableName(tableName)
		  .build();

		// use the blocking stub to call the DescribeTable service
		List<ColumnInfo> columnList = bStub.describeTable(dtReq).getColumnsList();
		Map<String, String> columnsMap = new HashMap<String, String>();
		// print the name and type of each column
		for(ColumnInfo ci : columnList) {
		  String colname = ci.getName();
		  String dbtype = ci.getDatabaseType();
		  columnsMap.put(colname, dbtype);
		}
		return columnsMap;
	}
	
	private void openTableForWrite() {
		Integer errLimit = props.getErrorLimit();
		Integer errPct = props.getErrorPercentage();
		// create an insert option builder
		InsertOption iOpt = InsertOption.newBuilder()
		    .setErrorLimitCount(errLimit)
		    .setErrorLimitPercentage(errPct)
		    .setTruncateTable(false)
		    .addInsertColumns("comments")
		    .addInsertColumns("atime")
		  .build();

		// create an open request builder
		OpenRequest oReq = OpenRequest.newBuilder()
		    .setSession(mSession)
		    .setSchemaName(props.getGpSchema())
		    .setTableName(props.getGptable())
		    .setTimeout(15)
		    .setInsertOption(iOpt)
		  .build();

		// use the blocking stub to call the Open service; it returns nothing
		bStub.open(oReq);
	}
	
	private void writeToTable() {
		try {
			List<RowData> rows = new ArrayList<RowData>();
			long millis = System.currentTimeMillis();
			Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
					.setNanos((int) ((millis % 1000) * 1000000)).build();

			for (int row = 0; row < props.getIngestBatch(); row++) {
				// create a row builder
				api.Row.Builder builder = api.Row.newBuilder();

				// create builder for each column, in order, and set values - text, timestamp
				api.DBValue.Builder colbuilder1 = api.DBValue.newBuilder();
				colbuilder1.setStringValue(UUID.randomUUID().toString());
				builder.addColumns(colbuilder1.build());
				
				api.DBValue.Builder colbuilder2 = api.DBValue.newBuilder();
				colbuilder2.setTimeStampValue(timestamp);
				builder.addColumns(colbuilder2.build());
				
				// build the row
				RowData.Builder rowbuilder = RowData.newBuilder().setData(builder.build().toByteString());
				// add the row
				rows.add(rowbuilder.build());
			}

			// create a write request builder
			WriteRequest wReq = WriteRequest.newBuilder().setSession(mSession).addAllRows(rows).build();

			// use the blocking stub to call the Write service; it returns nothing
			bStub.write(wReq);
		} catch (Exception e) {
			log.debug("Exception while writing to table", e);
			throw new RuntimeException("Exception while writing to table", e);
		}
	}
	
	private String closeTable() {
		// create a close request builder
		TransferStats tStats = null;
		CloseRequest cReq = CloseRequest.newBuilder()
		    .setSession(mSession)
		  .build();

		// use the blocking stub to call the Close service
		tStats = bStub.close(cReq);
		return tStats.toString();
	}
}
