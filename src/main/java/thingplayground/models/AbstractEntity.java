package thingplayground.models;


public class AbstractEntity {
	
	private String type;	
	private String id;
	//private JsonNode jsonNode;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	/*
	public JsonNode getJsonNode() {
		return jsonNode;
	}
	public void setJsonNode(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}
*/
}
