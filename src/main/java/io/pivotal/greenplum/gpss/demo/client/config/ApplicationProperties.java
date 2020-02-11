package io.pivotal.greenplum.gpss.demo.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gpss")
public class ApplicationProperties {
	String gpssHost;
	int gpssPort;
	String gpSchema;
	String gpDatabase;
	String gptable;
	String gphost;
	int gpport;
	String gprole;
	String gppass;
	String runMode;
	int ingestBatch;
	int  errorPercentage;
	int errorLimit;
	
	public String getGpssHost() {
		return gpssHost;
	}
	public void setGpssHost(String gpssHost) {
		this.gpssHost = gpssHost;
	}
	public int getGpssPort() {
		return gpssPort;
	}
	public void setGpssPort(int gpssPort) {
		this.gpssPort = gpssPort;
	}
	public String getGpSchema() {
		return gpSchema;
	}
	public void setGpSchema(String gpSchema) {
		this.gpSchema = gpSchema;
	}
	public String getGpDatabase() {
		return gpDatabase;
	}
	public void setGpDatabase(String gpDatabase) {
		this.gpDatabase = gpDatabase;
	}
	public String getGptable() {
		return gptable;
	}
	public void setGptable(String gptable) {
		this.gptable = gptable;
	}
	public String getGphost() {
		return gphost;
	}
	public void setGphost(String gphost) {
		this.gphost = gphost;
	}
	public int getGpport() {
		return gpport;
	}
	public void setGpport(int gpport) {
		this.gpport = gpport;
	}
	public String getGprole() {
		return gprole;
	}
	public void setGprole(String gprole) {
		this.gprole = gprole;
	}
	public String getGppass() {
		return gppass;
	}
	public void setGppass(String gppass) {
		this.gppass = gppass;
	}
	public String getRunMode() {
		return runMode;
	}
	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}
	public int getIngestBatch() {
		return ingestBatch;
	}
	public void setIngestBatch(int ingestBatch) {
		this.ingestBatch = ingestBatch;
	}
	public int getErrorPercentage() {
		return errorPercentage;
	}
	public void setErrorPercentage(int errorPercentage) {
		this.errorPercentage = errorPercentage;
	}
	public int getErrorLimit() {
		return errorLimit;
	}
	public void setErrorLimit(int errorLimit) {
		this.errorLimit = errorLimit;
	}
	
}
