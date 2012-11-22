package com.evolveum.midpoint.provisioning.test.impl;

import static com.evolveum.midpoint.test.IntegrationTestTools.display;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.AssertJUnit;
import org.testng.annotations.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.opends.server.core.AddOperation;
import org.opends.server.types.Entry;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.types.ResultCode;
import org.opends.server.util.LDIFReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.evolveum.midpoint.provisioning.api.ProvisioningService;
import com.evolveum.midpoint.provisioning.api.ResourceObjectChangeListener;
import com.evolveum.midpoint.provisioning.test.mock.SynchornizationServiceMock;

import com.evolveum.midpoint.provisioning.ucf.api.ConnectorFactory;
import com.evolveum.midpoint.repo.api.RepositoryService;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.task.api.TaskManager;
import com.evolveum.midpoint.test.AbstractIntegrationTest;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ConnectorType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectFactory;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.TaskType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.UserType;

@ContextConfiguration(locations = { "classpath:application-context-provisioning.xml",
		"classpath:application-context-provisioning-test.xml",
		"classpath:application-context-task.xml",
        "classpath:application-context-audit.xml",
		"classpath:application-context-repository.xml",
		"classpath:application-context-repo-cache.xml",
		"classpath:application-context-configuration-test.xml" })
@DirtiesContext
public class TestSynchronization extends AbstractIntegrationTest {

	private static final String FILENAME_RESOURCE_OPENDJ = "src/test/resources/object/resource-opendj.xml";
	private static final String FILENAME_LDAP_CONNECTOR = "src/test/resources/ucf/connector-ldap.xml";
	private static final String SYNC_TASK_OID = "91919191-76e0-59e2-86d6-3d4f02d3ffff";
	private static final String FILENAME_SYNC_TASK = "src/test/resources/impl/sync-task-example.xml";
	private static final String LDIF_WILL_FILENAME = "src/test/resources/ucf/will.ldif";
	private static final String FILENAME_USER_ADMIN = "src/test/resources/impl/admin.xml";

	private ResourceType resource;
	
	@Autowired
	private ConnectorFactory manager;
	
	@Autowired
	private ProvisioningService provisioningService;

	@Autowired
	private ResourceObjectChangeListener syncServiceMock;

	@BeforeClass
	public static void startLdap() throws Exception {
		openDJController.startCleanServer();
	}

	@AfterClass
	public static void stopLdap() throws Exception {
		openDJController.stop();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.evolveum.midpoint.test.AbstractIntegrationTest#initSystem()
	 */
	@Override
	public void initSystem(Task initTask, OperationResult initResult) throws Exception {
		assertNotNull(manager);
		resource = (ResourceType) addObjectFromFile(FILENAME_RESOURCE_OPENDJ, ResourceType.class, initResult).asObjectable();
		//it is needed to declare the task owner, so we add the user admin to the reposiotry
		addObjectFromFile(FILENAME_USER_ADMIN, UserType.class, initResult);
		assertNotNull(provisioningService);
	}

	// TODO: MID-1031
	@Test(enabled=false)
	public void testSynchronization() throws Exception {

		final OperationResult result = new OperationResult(TestSynchronization.class.getName()
				+ ".synchronizationTest");

		try {

			addObjectFromFile(FILENAME_SYNC_TASK, TaskType.class, result);
			addObjectFromFile(FILENAME_LDAP_CONNECTOR, ConnectorType.class, result);

			// create add change in embeded LDAP
			LDIFImportConfig importConfig = new LDIFImportConfig(LDIF_WILL_FILENAME);
			LDIFReader ldifReader = new LDIFReader(importConfig);
			Entry entry = ldifReader.readEntry();
			display("Entry from LDIF", entry);

			final Task syncCycle = taskManager.getTask(SYNC_TASK_OID, result);
			AssertJUnit.assertNotNull(syncCycle);

			AddOperation addOperation = openDJController.getInternalConnection().processAdd(entry);

			AssertJUnit.assertEquals("LDAP add operation failed", ResultCode.SUCCESS,
					addOperation.getResultCode());

			// WHEN
			provisioningService.synchronize(resource.getOid(), syncCycle, result);
			
			// THEN
			SynchornizationServiceMock mock = (SynchornizationServiceMock) syncServiceMock;
			
			assertEquals("Synchronization service was not called.", true, mock.wasCalled());

		} finally {
			repositoryService.deleteObject(TaskType.class, SYNC_TASK_OID, result);
		}
	}


}