package thingplayground;

import org.eclipse.paho.client.mqttv3.MqttException;

import thingplayground.models.ApplicationPDU;

public class Orchestrator extends AbstractComponent implements OrchestratorCallback {

	private ClientInterface clientInterface;
	private DeviceInterface deviceInterface;
	private StorageInterface storageInterface;
	
	@Override
	public void start() {
		clientInterface = new ClientInterface();
		deviceInterface = new DeviceInterface();
		storageInterface = new StorageInterface();
		storageInterface.start(this);
		clientInterface.start(this);
		try {
			deviceInterface.start(this);
		} catch (MqttException e) {
			e.printStackTrace();
		}
		System.out.println("Orchestrator has started");
	}
	
	@Override
	public void stop() {
		clientInterface.stop();
		deviceInterface.stop();
		storageInterface.stop();
	}


	@Override
	public void handleData(AbstractComponent sender, ApplicationPDU data) {
		storageInterface.handleData(sender, data);
		
	}


	@Override
	public Object handleRequest(AbstractComponent sender, Object request) {
		return storageInterface.getDataStore();
	}
}
