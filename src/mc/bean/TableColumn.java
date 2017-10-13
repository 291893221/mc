package mc.bean;

public class TableColumn {
	String columnName;
	String dataType;
	int dataLength;
	String comments;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName=columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType=dataType;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength=dataLength;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments=comments;
	}
	
	
}
