package thingplayground.models;

public class ApplicationPDU {

	private String intent;
	private AbstractEntity entity;
	
	
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	
	public AbstractEntity getEntity() {
		return entity;
	}
	public void setEntity(AbstractEntity entity) {
		this.entity = entity;
	}

	public static final String INTENT_CREATE = "create";
	public static final String INTENT_UPDATE = "update";
	public static final String INTENT_DELETE = "delete";
	public static final String INTENT_READ = "read";
}
