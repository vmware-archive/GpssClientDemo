/**
 * 
 */
package io.pivotal.greenplum.gpss.demo.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @author sridharpaladugu
 *
 */
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.pivotal.greenplum.gpss.demo.client.config.ApplicationProperties;
import io.pivotal.greenplum.gpss.demo.client.model.GpssMeta;
import io.pivotal.greenplum.gpss.demo.client.repository.GpssClientMetaRepository;
import io.pivotal.greenplum.gpss.demo.client.service.GpssIngestService;

@Component
public class GpssCron {
	private static final Log log = LogFactory.getLog(GpssCron.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	@Autowired
	ApplicationProperties props;

	@Autowired
	GpssIngestService svc;

	@Autowired
	GpssClientMetaRepository gpssClientMetaRepository;

	@Scheduled(fixedRate = 15000)
	public void runIngest() {
		StringBuffer msg = new StringBuffer();
		msg.append("<<<<<<<<<<<<<<<<<<<<<<<<<<< ")
		.append("Job Iteration Starting at ")
		.append(dateFormat.format(new Date()))
		.append(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(msg.toString());
		if (props.getRunMode() != null && props.getRunMode().equalsIgnoreCase("verifyenv")) {
			svc.checkEnv();
		} else if (props.getRunMode() != null && props.getRunMode().equalsIgnoreCase("ingest")) {
			GpssMeta meta = gpssClientMetaRepository.findByJobName(props.getGptable());
			if (meta.getJobSignal().equalsIgnoreCase("run")) {
				svc.ingestBatch();
			}

		} else {
			log.warn("The run mode specified is not understood. Please specify verifyenv or ingest");

		}
		msg=null; msg= new StringBuffer();
		msg.append("<<<<<<<<<<<<<<<<<<<<<<<<<<< ")
		.append("Job Iteration Ending at ")
		.append(dateFormat.format(new Date()))
		.append(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(msg.toString());
	}
}
