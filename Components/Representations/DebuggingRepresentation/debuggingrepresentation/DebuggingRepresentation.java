package debuggingrepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

/**
 * 
 * @author Sergii Zhevzhyk
 * 
 */
public class DebuggingRepresentation extends AbstractRepresentation {

	//private static Logger logger = Logger.getLogger("debuggingrepresentation.DebuggingRepresentation");
	
	private int seed = 12345;
	private int update_interval = 10;
	private boolean protocol_io = false;
	private String protocol_log = "debugging_representation.log";
	private int saveInterval = 10000;
	protected NESRandom generator;
	private int input_nodes;
	private int output_nodes;
	public static String newline = System.getProperty("line.separator");

	private int currentSaveInterval = 0;
	private int currentIteration = 0;
	private ArrayList<Float> currentOutput = new ArrayList<Float>();
	//private boolean loaded = false;
	

	public DebuggingRepresentation(int numberOfInputs, int numberOfOutputs,
			NESRandom random, Hashtable<String, XMLFieldEntry> properties) {
		super(numberOfInputs, numberOfOutputs, random, properties);
		this.setProperties(properties);
		
		loadProperties();
		this.generator = new NESRandom(seed);
		this.input_nodes = numberOfInputs;
		this.output_nodes = numberOfOutputs;
		
		if (protocol_io) {
			try {
				FileWriter log = new FileWriter(protocol_log, false);
				for (int i = 1; i <= input_nodes; i++) {
					log.write("I" + i + " ");
				}
				for (int i = 1; i <= output_nodes; i++) {
					log.write("O" + i + " ");
				}
				log.write(newline);
				log.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		if (currentIteration % update_interval == 0  /* && !loaded*/) {
			generateNewOutput();
		}

		// write log into file
		if (protocol_io && currentSaveInterval % saveInterval == 0 ) {
			StringBuilder builder = new StringBuilder();
			for (float in : input) {
				builder.append(Float.toString(in));
				builder.append(" ");
			}

			for (float out : currentOutput) {
				builder.append(Float.toString(out));
				builder.append(" ");
			}
			
			builder.append(newline);
			log(builder.toString());			
		}

		setEvaluated(true);
		currentIteration++;
		saveInterval++;
		return (ArrayList<Float>) currentOutput.clone();
	}

	private void generateNewOutput() {
		//currentOutput.clear();
		ArrayList<Float> newOutput = new ArrayList<Float>();
		for (int i = 0; i < output_nodes; i++) {
			newOutput.add(generator.nextFloat());
		}
		currentOutput = newOutput;
	}

	@Override
	public void exportToFile(File saveFile) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void mutationFunction(float severity, float probability,
			int method) {
	}

	@Override
	protected void recombinationFunction(AbstractRepresentation other,
			int method) {
	}

	@Override
	public int getNumberofMutationFunctions() {
		return 0;
	}

	@Override
	public int getNumberOfRecombinationFunctions() {
		return 0;
	}

	@Override
	public void reset() {
	}

	@Override
	public double diffTo(AbstractRepresentation representation) {
		return 0;
	}

	@Override
	protected AbstractRepresentation cloneFunction() {
		return this;
	}

	@Override
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("DebuggingRepresentation");
		nn.addAttribute("input_nodes", String.valueOf(this.input_nodes));
		nn.addAttribute("output_nodes", String.valueOf(this.output_nodes));
		if (this.isEvaluated()) {
			nn.addAttribute("fitness", String.valueOf(this.getFitness()));
		}

		Element dnodes = nn.addElement("nodes");

		for (int i = 0; i < this.output_nodes; i++) {
			Element node = dnodes.addElement("node");
			node.addAttribute("nr", String.valueOf(i));
			Element outputNode = node.addElement("output");
			outputNode.addText(String.valueOf(currentOutput.get(i)));
		}
	}

	@Override
	public AbstractRepresentation loadFromXML(Node node) {
		try {
			// Add properties
			loadProperties();
			
			String fitnessString = node.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}

			this.input_nodes = Integer.parseInt(node.valueOf("./@input_nodes"));
			this.output_nodes = Integer.parseInt(node.valueOf("./@output_nodes"));
			
			this.currentOutput = new ArrayList<Float>();
			//this.loaded  = true;
			
			Node dnodes = node.selectSingleNode("./nodes");
			for (int nr = 0; nr < this.output_nodes; nr++) {
				Node curnode = dnodes.selectSingleNode("./node[@nr='" + nr
						+ "']");
				if (curnode == null)
					throw new IllegalArgumentException(
							"CompleteNetwork: node tags inconsistent!"
									+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
			
				currentOutput.add(Float.valueOf(curnode.valueOf("./output")));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"CompleteNetwork: NumberFormatException! Check XML File");
		}
		return this;
	}

	@Override
	public Hashtable<String, String> getDetails() {
		Hashtable<String, String> result = new Hashtable<String, String>(output_nodes);
		int i =1;
		for (float output : currentOutput) {
			result.put("output " + Integer.toString(i), Float.toString(output));
			i++;
		}
		return result;
	}

	@Override
	public String getHash() {
		return "haha";
	}

	private void log(String logString) {
		try {
			FileWriter log = new FileWriter(protocol_log, true);
			log.write(logString);
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadProperties() {
		XMLFieldEntry seedEntry = getProperties().get("seed");
		this.seed = Integer.parseInt(seedEntry.getValue());

		XMLFieldEntry intervalEntry = getProperties().get("update_interval");
		this.update_interval = Integer.parseInt(intervalEntry.getValue());

		XMLFieldEntry saveLogEntry = getProperties().get("protocol_io");
		this.protocol_io = Boolean.parseBoolean(saveLogEntry.getValue());

		XMLFieldEntry protocolLogEntry = getProperties().get("protocol_log");
		protocol_log = protocolLogEntry.getValue();
	}

	@Override
	public String getC() throws FileNotFoundException {
			System.err.println ("DebuggingRepresentation not implemented!");
		return null;
	}

}
