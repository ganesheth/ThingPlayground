package thingplayground;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import thingplayground.models.AbstractEntity;
import thingplayground.models.ApplicationPDU;


public class DeviceInterface extends AbstractComponent implements MqttCallback {
	
	private IMqttClient client;
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	OrchestratorCallback callback;
	
	@Override
	public void start(OrchestratorCallback callback) throws MqttException {
		this.callback = callback;
		String publisherId = UUID.randomUUID().toString();
		client = new MqttClient("tcp://127.0.0.1:5566",publisherId);
		client.connect();
		client.setCallback(this);
		client.subscribe("device/+" );
		System.out.println("Device interface is listening");
	}
	
	@Override
	public void stop() {
		try {
			if(client.isConnected())
				client.disconnect();
			client.close();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void connectionLost(Throwable cause) {
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			ApplicationPDU pdu = objectMapper.readValue(message.getPayload(), ApplicationPDU.class);		
			if(pdu.getEntity() != null && pdu.getIntent() != null) {
				//pdu.getEntity().setJsonNode(objectMapper.readTree(message.getPayload()).findValue("entity"));
				callback.handleData(this, pdu);
			}
		}catch(Exception ex) {
			System.out.println("Exception in processing message: " + ex.getMessage());
		}
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	

}
