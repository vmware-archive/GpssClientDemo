/**
 * 
 */
package io.pivotal.greenplum.gpss.demo.client.repository;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.greenplum.gpss.demo.client.model.GpssMeta;

/**
 * @author sridharpaladugu
 *
 */
public interface GpssClientMetaRepository extends CrudRepository<GpssMeta, Integer> {
	public GpssMeta findByJobName(String jobName);

}
