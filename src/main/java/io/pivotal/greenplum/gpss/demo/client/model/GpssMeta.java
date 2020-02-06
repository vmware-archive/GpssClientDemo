/**
 * 
 */
package io.pivotal.greenplum.gpss.demo.client.model;

import javax.persistence.Column;
/**
 * @author sridharpaladugu
 *
 */
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hour_glass", schema="gpss_meta")
public class GpssMeta {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="job_name", nullable=false)
	private String jobName;
	@Column(name="job_signal", nullable=false)
	private String jobSignal;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobSignal() {
		return jobSignal;
	}
	public void setJobSignal(String jobSignal) {
		this.jobSignal = jobSignal;
	}

}
