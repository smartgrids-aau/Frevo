package graphics;

import java.util.Hashtable;

import core.XMLFieldEntry;

public class BatchQueueElement {
	private String propertyKey;
	//private XMLFieldEntry propertyEntry;
	private String startValue;
	private String endValue;
	private String stepValue;

	private Hashtable<String, XMLFieldEntry> properties;

	public BatchQueueElement(String key, XMLFieldEntry stepProperty,
			Hashtable<String, XMLFieldEntry> properties) {
		//this.propertyEntry = stepProperty;
		this.properties = properties;
		this.propertyKey = key;

		this.startValue = stepProperty.getValue();
		this.endValue = stepProperty.getValue();
		this.stepValue = Integer.toString(1);
	}

	public String getPropertyKey() {
		return propertyKey;
	}
	
	public String getStartValue() {
		return startValue;
	}
	
	public String getEndValue() {
		return endValue;
	}
	
	public String getStepValue() {
		return stepValue;
	}
	
	public void setStepValue(String stepValue) {
		this.stepValue = stepValue;
	}

	public void setFromValue(String startValue) {
		this.startValue = startValue;
	}

	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}

	public Hashtable<String, XMLFieldEntry> getProperties() {
		return properties;
	}
}
