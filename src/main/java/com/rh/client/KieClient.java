package com.rh.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstanceCustomVarsList;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieClient {

	final static Logger log = LoggerFactory.getLogger(KieClient.class);

	// private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
	private static final String URL = "http://localhost:8090/rest/server";
	private static final String user = System.getProperty("username", "user");
	private static final String password = System.getProperty("password", "user");

	// CONSTANTS
	private static final String CONTAINER = "work-flow";
	private static final String PROCESS_ID = "work-flow.work-assignment";
	private static final String QUERY_PROCESS_INSTANCES = "jbpmProcessInstances";
	private static final String QUERY_PROCESS_INSTANCES_WITH_VAR = "jbpmProcessInstancesWithVariables";

	private KieServicesClient client;

	public static void main(String[] args) {
		KieClient clientApp = new KieClient();

		System.setProperty("org.drools.server.filter.classes", "true");

		log.info("begin");

		Long piid = clientApp.launchProcess();
		// clientApp.findProcessesByIds();

		// log.info("piid {}", piid);

		log.info("end");
	}

	public KieClient() {
		client = getClient();
	}

	public Long launchProcess() {
		try {
			ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);
			Map<String, Object> inputData = new HashMap<>();

			setInputData(inputData);
			// ---------------------------
			return processClient.startProcess(CONTAINER, PROCESS_ID, inputData);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void findProcessesByIds() {
		try {
			QueryServicesClient queryClient = client.getServicesClient(QueryServicesClient.class);

			QueryFilterSpec spec = new QueryFilterSpecBuilder().between("processInstanceId", 0, 10)
															   .in("variableId", Arrays.asList("dossierId", "status"))
			                                                   .get();

			queryClient.query(QUERY_PROCESS_INSTANCES_WITH_VAR, QueryServicesClient.QUERY_MAP_PI_WITH_VARS, spec, 0, 10,
			        ProcessInstanceCustomVarsList.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setInputData(Map<String, Object> inputData) {
		
	}

	private void sendSignal(String signalName, Object signalPayload) {
		ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);
		processClient.signal(CONTAINER, signalName, signalPayload);
	}

	private KieServicesClient getClient() {
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, user, password);

		// Marshalling
		config.setMarshallingFormat(MarshallingFormat.JSON);
		KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

		return client;
	}

}
