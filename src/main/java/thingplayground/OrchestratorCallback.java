package thingplayground;

import thingplayground.models.ApplicationPDU;

public interface OrchestratorCallback {
	void handleData(AbstractComponent sender, ApplicationPDU data);
	Object handleRequest(AbstractComponent sender, Object request);
}
