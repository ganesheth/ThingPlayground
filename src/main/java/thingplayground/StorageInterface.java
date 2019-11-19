package thingplayground;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thingplayground.models.AbstractEntity;
import thingplayground.models.ApplicationPDU;



public class StorageInterface extends AbstractComponent {
	

	private Map<String, AbstractEntity> mockStorage = new HashMap<>();
	private static Logger logger =     LoggerFactory.getLogger(StorageInterface.class);
	//private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");
	private GraphDBInterface graphDb = new GraphDBInterface();

	@Override
	public void start(OrchestratorCallback callback) {
		logger.info("Storage interface is ready");
	}
	
	@Override
	public void stop() {
		graphDb.close();
	}
	
	public void handleData(AbstractComponent sender, ApplicationPDU data) {
		mockStorage.put(data.getEntity().getId(), data.getEntity());
		if(data != null && data.getIntent().compareTo(ApplicationPDU.INTENT_CREATE) == 0 ) {
			graphDb.create(data.getEntity());
			return;
		}
		
		if(data != null && data.getIntent().compareTo(ApplicationPDU.INTENT_UPDATE) == 0 ) {
			graphDb.update(data.getEntity());
			return;
		}
		
		if(data != null && data.getIntent().compareTo(ApplicationPDU.INTENT_DELETE) == 0 ) {
			graphDb.delete(data.getEntity().getId());
			return;
		}
	}
	
	public Map<String, AbstractEntity> getDataStore(){
		return mockStorage;
	}
	
	
}
