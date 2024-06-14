package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ARGoSNetworkInterface {
	NetworkInterface[] controllerInterfaces;
	NetworkInterface loopFunctionInterface;

	public ARGoSNetworkInterface(int numController, String address,
			int portNrLoopFunction) throws UnknownHostException, IOException {

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		controllerInterfaces = new NetworkInterface[numController];
		for (int i = 0; i < numController; i++) {
			controllerInterfaces[i] = new NetworkInterface(address,
					portNrLoopFunction + i + 1);
			controllerInterfaces[i].readLine();
		}
		loopFunctionInterface = new NetworkInterface(address,
				portNrLoopFunction);
		loopFunctionInterface.readLine();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeActuatorData(int numController, String ActuatorData)
			throws IOException {
		controllerInterfaces[numController].writeLine(ActuatorData);
	}

	public String readSensorData(int numController) throws IOException {
		return controllerInterfaces[numController].readLine();
	}

	public String handleLoopFunction() throws IOException {
		String r = loopFunctionInterface.readLine();
		loopFunctionInterface.writeLine(" ");
		return r;
	}
	
	public void close() throws IOException{
		loopFunctionInterface.clearBuffer();
		loopFunctionInterface.close();
		for(int i = 0; i < controllerInterfaces.length; i++){
			controllerInterfaces[i].clearBuffer();
			controllerInterfaces[i].close();
		}
	}

	public double[] getFitness() throws IOException {
		loopFunctionInterface.readLine();
		loopFunctionInterface.writeLine("Fitness");
		String fitIn = loopFunctionInterface.readLine();
		String[] fitarr = fitIn.split(";");
		double[] fitness = new double[fitarr.length];
		for (int i = 0; i < fitness.length; i++) {
			fitness[i] = Double.parseDouble(fitarr[i]);
		}
		return fitness;
	}

	public void reset() throws Exception {
		loopFunctionInterface.readLine();
		loopFunctionInterface.writeLine("Reset");
		String input = loopFunctionInterface.readLine();
		if (!input.equals("Resetting")) {
			throw new Exception("ARGoS does not reset");
		}
		close();
	}

	class NetworkInterface {
		private Socket s;
		private BufferedReader r;
		private BufferedWriter w;

		public NetworkInterface(String address, int portNr)
				throws UnknownHostException, IOException {
			s = new Socket(InetAddress.getByName(address), portNr);
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		}

		public String readLine() throws IOException {
			return r.readLine();
		}

		public void writeLine(String message) throws IOException {
			if (!message.endsWith("\n")) {
				message += "\n";
			}
			w.write(message);
			w.flush();

		}
		
		public void clearBuffer() throws IOException{
			while(r.ready()){
				r.read();
			}
		}
		
		public void close() throws IOException{
			r.close();
			w.close();
			s.close();
		}
	}
}
