package mc.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mc.MC;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 数据库操作的一些相关类
 * 
 * @author 陈志光
 * 
 */
@SuppressWarnings({ "unchecked", "unused" })
public class MyDao {

	private static final Log log=LogFactory.getLog(MyDao.class);

	@SuppressWarnings("rawtypes")
	private static Map<Class,String[]> MapClassFields=new HashMap<Class,String[]>();

	/**
	 * @return Connection
	 * @throws Exception
	 */
	public static Connection openConnection(String connUrl,String user,String password) throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Connection conn=DriverManager.getConnection(connUrl,user,password);
		return conn;
	}

	/**
	 * 断开连接
	 * 
	 * @param con
	 */
	public static void closeConncetion(Connection con) {
		if (con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error(e);
				log.error("关闭连接出错");
			}
		}
	}
	/**
	 * 关闭语句
	 * 
	 * @param stmt
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt!=null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 关闭结果集
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 关闭连接，语句，和结果集
	 * 
	 * @param con
	 * @param stmt
	 * @param rs
	 */
	public static void closeSql(Connection con,Statement stmt,ResultSet rs) {
		closeResultSet(rs);
		closeStatement(stmt);
		closeConncetion(con);
	}

	/**
	 * 事务回滚
	 * 
	 * @param con
	 */
	public static void rollBackConnection(Connection con) {
		if (con!=null) {
			try {
				con.rollback();
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				log.error("事务回滚出错");
			}
		}
	}

	/**
	 * 往回多个查询记录中的第一个
	 * 
	 * @param clazz
	 * @param sql
	 * @return
	 */
	public static Object getOneRecordObject(Connection con,Class clazz,String sql) {
		log.info(clazz+";"+sql);
		Statement stmt=null;
		ResultSet rs=null;
		Object object=null;
		try {
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);

			if (rs.next()) {
				object=getObjectFromRS(rs,clazz);
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	/**
	 * 根据Class与sql语句来查询数据库得到一个列表
	 * 
	 * @param clazz
	 * @param sql
	 * @return
	 */
	public static List getListObject(Connection con,Class clazz,String sql) {
		if (clazz==null||sql==null||sql.length()<1) {
			return null;
		}
		Statement stmt=null;
		ResultSet rs=null;
		try {
			List list=new ArrayList();
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			Object object=null;
			while (rs.next()) {
				object=getObjectFromRS(rs,clazz);
				list.add(object);
			}
			return list;
		} catch (Exception e) {
			log.error(e);
			log.error("getListObject使用"+sql+"查询出错");
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	/**
	 * 返回SQL查询结果中的第一行记录
	 * 
	 * @param sql
	 * @return
	 */
	public static Map getOneMap(Connection con,String sql) {
		if (sql==null||sql.length()<1) {
			return null;
		}
		Statement stmt=null;
		ResultSet rs=null;
		try {
			Map map=null;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			if (rs.next()) {
				map=(Map)getObjectFromRS(rs,null);
			}
			return map;
		} catch (Exception e) {
			log.error(e);
			log.error("getListMap使用"+sql+"查询出错");
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	public static List<Map> getList(Connection con,String sql) throws Exception {
		if (sql==null||sql.length()<1) {
			return null;
		}
		List<Map> list=new ArrayList<Map>();
		Map map=null;

		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while (rs.next()) {
				map=(Map)getObjectFromRS(rs,null);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeSql(con,stmt,rs);
		}
		return list;
	}

	/**
	 * Map<String,Object> {List<String> listTitle;List<Map> listData}
	 * 
	 * @param con
	 * @param sql
	 * @return
	 * @throws Exception
	 *             Creator :czg DateTime:2011-2-19 上午05:40:10
	 */
	public static Map<String,Object> getListAndTitle(Connection con,String sql) throws Exception {
		if (sql==null||sql.length()<1) {
			return null;
		}
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Map map=null;

		Statement stmt=null;
		ResultSet rs=null;
		try {
			List<Map> listData=new ArrayList<Map>();
			List<String> listTitle=new ArrayList<String>();
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			ResultSetMetaData rsmd=rs.getMetaData();
			for (int i=0;i<rsmd.getColumnCount();i++) {
				listTitle.add(rsmd.getColumnName(i+1).toUpperCase());
			}

			while (rs.next()) {
				map=new HashMap();
				for (int i=0;i<listTitle.size();i++) {
					map.put(listTitle.get(i),rs.getObject(i+1));
				}
				listData.add(map);
			}

			resultMap.put("listData",listData);
			resultMap.put("listTitle",listTitle);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeSql(con,stmt,rs);
		}
		return resultMap;
	}

	/**
	 * 根据sql获取List<Map>
	 * 
	 * @param sql
	 * @return
	 */
	public static List<Map> getListMap(String sql) {
		if (sql==null||sql.length()<1) {
			return null;
		}
		Statement stmt=null;
		ResultSet rs=null;
		Connection con=null;
		try {
			con=openConnection(MC.connUrl,MC.user,MC.password);
			List<Map> list=new ArrayList<Map>();
			Map map=null;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while (rs.next()) {
				map=(Map)getObjectFromRS(rs,null);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			log.error(e);
			log.error("getListMap使用"+sql+"查询出错");
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	/**
	 * 根据sql获取List<Map>
	 * 
	 * @param sql
	 * @return
	 */
	public static List<Map> getListMap(Connection con,String sql) {
		if (sql==null||sql.length()<1) {
			return null;
		}
		Statement stmt=null;
		ResultSet rs=null;
		try {
			List<Map> list=new ArrayList<Map>();
			Map map=null;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while (rs.next()) {
				map=(Map)getObjectFromRS(rs,null);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			log.error(e);
			log.error("getListMap使用"+sql+"查询出错");
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	/**
	 * 根据sql获取List<Map>
	 * 
	 * @param sql
	 * @return
	 */
	public static List<Map> getListMap(Connection con,String sql,Class clazz) {
		if (sql==null||sql.length()<1) {
			return null;
		}
		Statement stmt=null;
		ResultSet rs=null;
		try {
			List<Map> list=new ArrayList<Map>();
			Map map=null;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while (rs.next()) {
				map=(Map)getObjectFromRS(rs,clazz);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			log.error(e);
			log.error("getListMap使用"+sql+"查询出错");
		} finally {
			closeSql(con,stmt,rs);
		}
		return null;
	}

	/**
	 * 根据sql获取第一个记录的第一个属性列
	 * 
	 * @return
	 */
	public static String getString(String sql) {
		return null;
	}

	public static Object getObjectFromRS(ResultSet rs,Class clazz) {
		if (rs!=null) {
			String strColName="";
			Object Value="";

			try {
				ResultSetMetaData rsmd=rs.getMetaData();
				if (clazz==null) {
					Map map=new HashMap();
					for (int i=0;i<rsmd.getColumnCount();i++) {
						strColName=rsmd.getColumnName(i+1);
						Value=(rs.getObject(strColName));
						map.put(strColName.toUpperCase(),Value);
					}
					return map;
				} else {
					Map map=new HashMap();
					String[] fieldNames=getFiledsFromClass(clazz);
					int[] indexs=getIndexs(fieldNames,rsmd);

					for (int i=0;i<rsmd.getColumnCount();i++) {
						strColName=rsmd.getColumnName(i+1);
						Value=(rs.getObject(strColName));
						if (indexs[i]>=0) {
							map.put(fieldNames[indexs[i]],Value);
						} else {
							map.put(strColName.toUpperCase(),Value);
						}
					}
					return map;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取类里面的属性名列表
	 * 
	 * @author 陈志光
	 * @history Jun 30, 2009 1:02:56 PM
	 * @param clazz
	 * @return String[]
	 */
	private static String[] getFiledsFromClass(Class clazz) {
		String[] al=MapClassFields.get(clazz);
		if (al==null) {
			Field[] fields=clazz.getDeclaredFields();
			al=new String[fields.length];
			for (int i=0;i<fields.length;i++) {
				al[i]=fields[i].getName();
			}
			MapClassFields.put(clazz,al);
		}
		if (al==null) {
			al=new String[0];
		}
		return al;
	}

	private static int[] getIndexs(String[] fieldNames,ResultSetMetaData rsmd) throws Exception {
		int[] indexs=new int[rsmd.getColumnCount()];
		for (int i=0;i<rsmd.getColumnCount();i++) {
			boolean flag=false;
			for (int j=0;j<fieldNames.length;j++) {
				if (Util.isEqualsIgnoreSign(fieldNames[j],rsmd.getColumnName(i+1))) {
					indexs[i]=j;
					flag=true;
					break;
				}
			}
			if (!flag) {
				indexs[i]=-1;
			}
		}
		return indexs;
	}

	/**
	 * 获取一个查询结果集的总数
	 * 
	 * @author 陈志光
	 * @history Jun 30, 2009 2:53:17 PM
	 * @param con
	 * @param sql
	 * @return
	 * @throws Exception
	 *             long
	 */
	private static int getCount(Connection con,String sql) throws Exception {
		if (Util.isStrEmpt(sql)) {
			return 0;
		}
		String strSql=" select count(1) from ("+sql+") TmpTable";
		System.out.println("sql语句："+sql);
		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt=con.createStatement();
			rs=stmt.executeQuery(strSql);
			if (rs.next()) {
				return rs.getBigDecimal(1).intValue();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeSql(null,stmt,rs);
		}
		return 0;
	}

	public static int getInt(Connection con,String sql) throws Exception {
		if (Util.isStrEmpt(sql)) {
			return 0;
		}
		System.out.println("sql语句："+sql);
		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getBigDecimal(1).intValue();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeSql(null,stmt,rs);
		}
		return 0;
	}

	public static void readBlob() throws Exception {
		String version="SCHE_PROCESS-3";
		
		Connection con=openConnection(MC.connUrl,MC.user,MC.password);
		con.setAutoCommit(false);
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery("select BLOB_VALUE_ from RBPM_LOB a where deployment_=(select deployment_ from RBPM_DEPLOYPROP where stringval_='"+version+"')");
		List<Byte> list=new ArrayList<Byte>();
		byte[] b=new byte[102400];
		int len=0;
		if (rs.next()) {
			java.sql.Blob blob=rs.getBlob(1);
			InputStream ins=blob.getBinaryStream();
			// 输出到文件
			File file=new File("c:\\output.txt");
			// 下面将BLOB数据写入文件
			len=ins.read(b);
			System.out.println(len);
			// 依次关闭
			ins.close();
		}
		con.commit();
		con.close();
		
		con=openConnection(MC.connUrl,MC.user,MC.password);
		List<Map> listConfig=getListMap(con,"select node_id,node_name,aqtr_orderby from t_process_config where version='"+version+"' order by aqtr_orderby");
		
		Map<String,Integer> mapConfig=new HashMap<String,Integer>();
		
		if (listConfig!=null) {
			for (Map map:listConfig) {
				String node=map.get("NODE_ID").toString();
				Integer index=Integer.valueOf(map.get("AQTR_ORDERBY").toString());
				mapConfig.put(node,index);
				
				System.out.println(index+"\t"+node+"\t"+"转他人处理"+"\t"+"OTHER_TASK"+"\t"+map.get("NODE_NAME")+"\t"+node+"\t"+node+"\t"+1+"\t"+version);
				
			}
		}
		
		String str=new String(Arrays.copyOf(b,len));
		
		SAXBuilder builder = new SAXBuilder(false);
		try {
			Document document = builder.build(new ByteArrayInputStream(str.getBytes("UTF-8")));
			Element root = document.getRootElement();

			List<Element> listElement = root.getChildren();

			List<BpmNode> listNodes=new ArrayList<BpmNode>();
			Map<String,BpmNode> rootMap=new HashMap<String,BpmNode>();
			for (Element e:listElement) {
				BpmNode node=new BpmNode();
				node.name=e.getAttributeValue("name");
				node.type=e.getName();
				node.alias=e.getAttributeValue("alias");
				
				List<Element> listTran = e.getChildren();
				if (listTran!=null) {
					for (Element et:listTran) {
						if (et.getName().equals("transition")) {
							BpmTran tran=new BpmTran();
							tran.name=et.getAttributeValue("name");
							tran.to=et.getAttributeValue("to");
							node.trans.put(tran.name,tran);
						}
					}
				}
				rootMap.put(node.name,node);
				if (node.type.equals("task")) {
					listNodes.add(node);
				}
			}
			
			for (BpmNode node:listNodes) {
				for (Map.Entry<String,BpmTran> entry:node.trans.entrySet()) {
					BpmNode desideNode=rootMap.get(entry.getValue().to);
					if (desideNode.type=="decision") {
						for (Map.Entry<String,BpmTran> entryDeside:desideNode.trans.entrySet()) {
							BpmNode nextNode=rootMap.get(entryDeside.getValue().to);
							
							int index=2;
							
							String direName=nextNode.alias;
							if (mapConfig.get(node.name)>mapConfig.get(nextNode.name)) {
								direName="回退"+direName;
								index=3;
							}
							
							System.out.println(mapConfig.get(node.name)+"\t"+node.name+"\t"+direName+"\tCOMMIT_TASK"+"\t"+nextNode.alias+"\t"+entryDeside.getValue().to+"\t"+entryDeside.getValue().name+"\t"+index+"\t"+version);
						}
					} else if (desideNode.type=="end") {
						System.out.println(mapConfig.get(node.name)+"\t"+node.name+"\t"+"提交完成"+"\tCOMMIT_TASK"+"\t"+"提交完成"+"\t"+desideNode.name+"\t"+desideNode.name+"\t"+2+"\t"+version);
					} else if (desideNode.type=="task") {
						System.out.println(mapConfig.get(node.name)+"\t"+node.name+"\t"+desideNode.alias+"\tCOMMIT_TASK"+"\t"+desideNode.alias+"\t"+desideNode.name+"\t"+desideNode.name+"\t"+2+"\t"+version);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String args[]) throws Exception {
		readBlob();
	}
	
	public static class BpmNode {
		String name;
		String type;
		String alias;
		Map<String,BpmTran> trans=new HashMap<String,BpmTran>();;
		
		public BpmNode() {
			
		}
	}
	
	public static class BpmTran {
		String name;
		String to;
	}
}


