package mc.util;

public class JavaOracleMap {
	static String [] typesOracle={"NUMBER","CHAR","CLOB","DATE","VARCHAR2","VARCHAR","DOUBLE"};
	static String [] typesOracleJava={"Long","String","String","Date","String","String","Double"};
	static String [] typesOracleJavaSql={"BigDecimal","String","String","Date","String","String","Double"};
	static String [] typesOracleJavaJdbc={"DECIMAL","CHAR","CLOB","TIMESTAMP","VARCHAR","VARCHAR","DOUBLE"};
	
	public static String getJavaType(String dbColumnType){
		for (int i=0;i<typesOracle.length;i++) {
			if (typesOracle[i].intern().equals(dbColumnType)) {
				return typesOracleJava[i];
			}
		}
		return "";
	}
	
	public static String getJavaSqlType(String dbColumnType){
		for (int i=0;i<typesOracle.length;i++) {
			if (typesOracle[i].intern().equals(dbColumnType)) {
				return typesOracleJavaSql[i];
			}
		}
		return "";
	}
	
	public static String getJavaJdbcType(String dbColumnType){
		for (int i=0;i<typesOracle.length;i++) {
			if (typesOracle[i].intern().equals(dbColumnType)) {
				return typesOracleJavaJdbc[i];
			}
		}
		return "";
	}
}
