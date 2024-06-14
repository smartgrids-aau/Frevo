package graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

/**
 * Visualizes a structure of the neural network as a graph. 
 * There are a few layouts which allow to look at your NN from different views.  
 * 
 * @author Sergii Zhevzhyk
 *
 */
public class NeuralNetworkVisualisation extends JFrame {
	
	private class Neuron {
		private int id;
		
		private Neuron(int id) {
			this.id = id;			
		}

		public int getId() {
			return id;
		}		
	}
	
	private class Link {
		private Neuron fromNeuron;
		private Neuron toNeuron;
		private double weight;
		
		private Link(Neuron fromNeuron, Neuron toNeuron, double weight) {
			this.fromNeuron = fromNeuron;
			this.toNeuron = toNeuron;
			this.weight = weight;
		}

		public Neuron getFromNeuron() {
			return fromNeuron;
		}

		public Neuron getToNeuron() {
			return toNeuron;
		}

		public double getWeight() {
			return weight;
		}
	}
	
	private static final long serialVersionUID = 8308231231247325111L;
	private static final String CIRCLE_LAYOUT = "Circle layout";
	private static final String NN_LAYOUT = "Standard neural network layout";

	private HashMap<Integer, Neuron> inputLayer = new HashMap<Integer, Neuron>();
	private HashMap<Integer, Neuron> hiddenLayer = new HashMap<Integer, Neuron>();
	private HashMap<Integer, Neuron> outputLayer = new HashMap<Integer, Neuron>();
	private ArrayList<Link> links = new ArrayList<Link>();
	
	private JComboBox<String> typeComboBox;
	private JPanel nnTopologyPanel;
	
	public NeuralNetworkVisualisation() {
		super ("Visualizing of the Neural Network");
		
		// settings of the frame
        setSize(500, 500);
        setLocation(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        // **** Create Menu panel
	    JPanel menuPanel = new JPanel();
	    menuPanel.setPreferredSize(new Dimension(300, 50));
	    menuPanel.setMinimumSize(new Dimension(300, 50));
	    menuPanel.setMaximumSize(new Dimension(300, 50));
	    menuPanel.setBorder(new TitledBorder("Control"));
	    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
	    
	    // Different layouts of the graph
	    String[] nnLayouts = { NN_LAYOUT, CIRCLE_LAYOUT };
	    typeComboBox = new JComboBox<String>(nnLayouts);
	    typeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				visualize();	
			}
		});
	    
	    menuPanel.add(typeComboBox);
	    
	    // Add menu panel to the frame
	    getContentPane().add(menuPanel, BorderLayout.NORTH);
	    
	    // **** Create panel for visualization of the NN
	    nnTopologyPanel = new JPanel();

	    nnTopologyPanel.setLayout(new BoxLayout(nnTopologyPanel, BoxLayout.X_AXIS));
	    
	    // Add topology panel to the frame
	    getContentPane().add(nnTopologyPanel, BorderLayout.CENTER);
	}
	
	
	public void addToInputLayer(int id) {
		inputLayer.put(id, new Neuron(id));
	}
	
	public void addToHiddenLayer(int id) {
		hiddenLayer.put(id, new Neuron(id));
	}
	
	public void addToOutputLayer(int id) {
		outputLayer.put(id, new Neuron(id));
	}
	
	public void addLink(int fromId, int toId, double weight) throws Exception {
		Neuron fromNeuron = findNeuron(fromId);
		Neuron toNeuron = findNeuron(toId);
		
		links.add(new Link(fromNeuron, toNeuron, weight));
	}
	
	private Neuron findNeuron(int id) throws Exception {
		if (inputLayer.containsKey(id)) {
			return inputLayer.get(id);
		} else if (hiddenLayer.containsKey(id)) {
			return hiddenLayer.get(id);
		} else if (outputLayer.containsKey(id)) {
			return outputLayer.get(id);
		}
		
		throw new Exception("Neuron with id=" + id + " wasn't found");		
	}
	
	private void fillGraph(mxGraphComponent graphComponent) {
		/* information how to build graphs could be found:
		 * http://jgraph.github.io/mxgraph/docs/manual_javavis.html
		 * information about layouts:
		 * http://jgraph.github.io/mxgraph/docs/js-api/files/layout/mxGraphLayout-js.html
		 * */
		
		// don't allow to disconnect vertices from edges
        graphComponent.getGraph().setDisconnectOnMove(false);
        
        if (typeComboBox.getSelectedItem() == CIRCLE_LAYOUT) {
        	circleLayout(graphComponent);
        } else if (typeComboBox.getSelectedItem() == NN_LAYOUT) {
        	standardNNLayout(graphComponent);
        }
	}
	
	private void standardNNLayout(mxGraphComponent graphComponent) {
		mxGraph graph = graphComponent.getGraph();
		Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        int horizontalDistance = 100;
        int verticalDistance = 100;
        int currentX = horizontalDistance;
        int currentY = verticalDistance;
        try {
        	Hashtable<Integer, Object> vertices = new Hashtable<Integer, Object>();
        	
        	String commonStyle = "fontColor=white;fontSize=18;shape=ellipse;";
        	for (Neuron neuron : inputLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "I" + neuron.getId(), currentX, currentY, 60, 60, commonStyle + "fillColor=green;");
    			vertices.put(neuron.getId(), vertex);
    			currentY += verticalDistance;
    		}
        	currentY = verticalDistance;
        	currentX += horizontalDistance;
        	for (Neuron neuron : hiddenLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "H" + neuron.getId(), currentX, currentY, 60, 60, commonStyle + "fillColor=gray;");
    			vertices.put(neuron.getId(), vertex);
    			currentY += verticalDistance;
        	}
        	currentY = verticalDistance;
        	currentX += horizontalDistance;
        	for (Neuron neuron : outputLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "O" + neuron.getId(), currentX, currentY, 60, 60, commonStyle + "fillColor=blue;");
    			vertices.put(neuron.getId(), vertex);
    			currentY += verticalDistance;
        	}
        	
        	DecimalFormat df = new DecimalFormat("0.000");
        	for (Link link : links) {
        		graph.insertEdge(parent, null, df.format(link.getWeight()), vertices.get(link.getFromNeuron().getId()), vertices.get(link.getToNeuron().getId()));
        	}
        } finally {
            graph.getModel().endUpdate();
        }
	}
	
	private void circleLayout(final mxGraphComponent graphComponent) {
		mxGraph graph = graphComponent.getGraph();
		Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
        	Hashtable<Integer, Object> vertices = new Hashtable<Integer, Object>();
        	
        	String commonStyle = "fontColor=white;fontSize=18;shape=ellipse;";
        	for (Neuron neuron : inputLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "I" + neuron.getId(), 100, 100, 80, 30, commonStyle + "fillColor=green;");
    			vertices.put(neuron.getId(), vertex);
        	}
        	for (Neuron neuron : hiddenLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "H" + neuron.getId(), 100, 100, 80, 30, commonStyle + "fillColor=gray;");
    			vertices.put(neuron.getId(), vertex);
        	}
        	for (Neuron neuron : outputLayer.values()) {
        		Object vertex = graph.insertVertex(parent, null, "O" + neuron.getId(), 100, 100, 80, 30, commonStyle + "fillColor=blue;");
    			vertices.put(neuron.getId(), vertex);
        	}
        	
        	DecimalFormat df = new DecimalFormat("0.000");
        	for (Link link : links) {
        		graph.insertEdge(parent, null, df.format(link.getWeight()), vertices.get(link.getFromNeuron().getId()), vertices.get(link.getToNeuron().getId()));
        	}
        } finally {
            graph.getModel().endUpdate();
        }

        // define layout
        mxIGraphLayout layout =   
        		new mxCircleLayout(graph);
        
        // layout using morphing
        graph.getModel().beginUpdate();
        try {
            layout.execute(graph.getDefaultParent());
        } finally {
            mxMorphing morph = new mxMorphing(graphComponent, 40, 20.2, 40);

            morph.addListener(mxEvent.DONE, new mxIEventListener() {
                @Override
                public void invoke(Object arg0, mxEventObject arg1) {
                	graphComponent.getGraph().getModel().endUpdate();
                }

            });

            morph.startAnimation();
        }
	}

	/**
	 * Shows a frame with visualized neural network topology
	 */
	public void visualize() {
		// remove all components from panel - we don't need all graphs
		this.nnTopologyPanel.removeAll();

		mxGraph graph = new mxGraph();
		// create component for visualizing of the graph
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		// moving of the edges are forbidden
		graphComponent.setConnectable(false);
		// fill graph with neurons and links with specified layout
		fillGraph(graphComponent);
		
		this.nnTopologyPanel.add(BorderLayout.CENTER, graphComponent);
		
		// make frame visible
		this.setVisible(true);
		
		this.repaint();	
	}
	/*
	// for test purposes
	public static void main(String[] args) { 
		NeuralNetworkVisualisation graphVisualizer = new NeuralNetworkVisualisation();
		
		graphVisualizer.addToInputLayer(1);
		graphVisualizer.addToInputLayer(2);
		
		graphVisualizer.addToOutputLayer(10);
		graphVisualizer.addToOutputLayer(11);
		
		try {
			graphVisualizer.addLink(1, 10, 1.11);
			graphVisualizer.addLink(1, 11, 1.11);
			graphVisualizer.addLink(2, 10, 1.11);
			graphVisualizer.addLink(2, 11, 1.11);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphVisualizer.visualize();
	}*/
}
