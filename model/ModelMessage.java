package model;

/**
 * A message between the model and the view
 */
public class ModelMessage {
	
	public enum TYPE {
		ERROR
	}
	
	private TYPE type;
	private Object data;
	
	public ModelMessage(TYPE type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public ModelMessage(TYPE type) {
		this(type, null);
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
