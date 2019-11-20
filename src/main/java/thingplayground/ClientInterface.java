package thingplayground;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientInterface extends AbstractComponent{
	
	ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void start(OrchestratorCallback callback) {
		port(Configuration.HTTP_PORT);
		get("/things", (req, res) -> objectMapper.writeValueAsString(callback.handleRequest(this, req)));
		System.out.println("Client interface is listening");
	}
}
