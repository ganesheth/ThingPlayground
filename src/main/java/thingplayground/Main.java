package thingplayground;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		System.out.println("Welcome to Events Playground");		
		
		Orchestrator orchestrator = new Orchestrator();
		
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("STOPPED !!!");
                orchestrator.stop();
            }


        });
		orchestrator.start();
		
		try {
			int code = System.in.read();
			Runtime.getRuntime().exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
