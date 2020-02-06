package io.pivotal.greenplum.gpss.demo.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages="io.pivotal.greenplum.gpss.demo.*")
@EnableScheduling
public class GPSSDemoApplication { 
	
	//implements CommandLineRunner{
	
//	@Autowired
//	GpssIngestService svc;
//	
//	@Autowired
//	ApplicationProperties props;
//	
//	@Autowired
//	GpssClientMetaRepository gpssClientMetaRepository;
	
	static final Log log = LogFactory.getLog(GPSSDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GPSSDemoApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {		
//		if(props.getRunMode() != null && props.getRunMode().equalsIgnoreCase("verifyenv")) {
//			svc.checkEnv();
//		}else if(props.getRunMode() != null && props.getRunMode().equalsIgnoreCase("ingest")) {
//			while(true) {
//				GpssMeta meta = gpssClientMetaRepository.findByJobName(props.getGptable());
//				if(meta.getJobSignal().equalsIgnoreCase("run")) {
//					svc.ingestBatch();
//				}
//				
//			}
//		} else {
//			log.warn("The run mode specified is not understood. Please specify verifyenv or ingest" );
//			
//		}
//	}

}
