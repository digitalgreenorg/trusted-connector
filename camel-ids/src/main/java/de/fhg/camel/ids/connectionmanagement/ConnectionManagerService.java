package de.fhg.camel.ids.connectionmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.aisec.ids.api.conm.ConnectionManager;
import de.fhg.aisec.ids.api.conm.IDSCPConnection;
import de.fhg.camel.ids.server.WebsocketComponent;



/**
 * Main entry point of the Connection Management Layer.
 *
 * This class is exposed as an OSGi Service and serves to access connection data from the management layer and REST API.
 *
 * @author Gerd Brost(gerd.brost@aisec.fraunhofer.de)
 *
 */
@Component(enabled=true, immediate=true, name="ids-conm")

public class ConnectionManagerService implements ConnectionManager {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerService.class);

	@Activate
	protected void activate() {
		LOG.info("Activating Connection Manager");


	}

	@Deactivate
	protected void deactivate(ComponentContext cContext, Map<String, Object> properties) {

	}

	@Override
	public List<IDSCPConnection> listConnections() {
		//TODO: Replace mock data with the real stuff
		Set<String> keySet = WebsocketComponent.CONNECTORS.keySet();
		List<IDSCPConnection> connections = new ArrayList<IDSCPConnection>();
		for (String key : keySet) {
			connections.add(new IDSCPConnection(key, "someConnection"));
		}
		

		return connections;
	}
	
//	public void setConnectors(HashMap<String, ConnectorRef> connectors) {
//		
//	}

}
