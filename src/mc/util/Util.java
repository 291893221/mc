package mc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mc.bean.TableColumn;

public class Util {
	
	public static List<TableColumn> getListTableColumn(String tableName) {
		StringBuffer sb=new StringBuffer();
		sb.append("select a.table_name,a.column_name,case when a.data_scale>0 then 'DOUBLE' else a.data_type end data_type,a.data_length,replace(replace(replace(replace(replace(replace(replace(b.comments,'，',','),'：',','),':',','),' ',','),'(',','),'（',','),'.',',') comments ");
		sb.append("from user_tab_cols a ");
		sb.append("	left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name ");
		sb.append("where a.table_name=upper('"+tableName+"') ");
		sb.append("order by a.column_id");
		
		List<Map> list=MyDao.getListMap(sb.toString());
		if (list!=null&&list.size()>0) {
			ArrayList<TableColumn> listOut=new ArrayList<TableColumn>(list.size());
			for (Map map:list) {
				TableColumn tc=new TableColumn();
				tc.setColumnName(map.get("COLUMN_NAME")+"");
				tc.setComments(map.get("COMMENTS")+"");
				tc.setDataLength(new Integer(map.get("DATA_LENGTH")+""));
				tc.setDataType(map.get("DATA_TYPE")+"");
				listOut.add(tc);
			}
			return listOut;
		}
		return null;
	}
	
	public static List<TableColumn> getListTablePrimaryKeyColumn(String tableName) {
		StringBuffer sb=new StringBuffer();
		sb.append("select a.table_name,a.column_name,a.data_type,a.data_length,replace(replace(replace(replace(replace(replace(replace(b.comments,'，',','),'：',','),':',','),' ',','),'(',','),'（',','),'.',',') comments ");
		sb.append("from user_tab_cols a ");
		sb.append("	left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name ");
		sb.append("where a.table_name=upper('"+tableName+"') ");
		sb.append("	and a.column_name in ( ");
		sb.append("            select d.column_name ");
		sb.append("            from user_constraints c,user_cons_columns d ");
		sb.append("            where c.owner=d.owner  ");
		sb.append("                and c.table_name=d.table_name  ");
		sb.append("                and c.constraint_name=d.constraint_name ");
		sb.append("                and c.constraint_type='P' ");
		sb.append("                and c.table_name=upper('"+tableName+"') ");
		sb.append("        ) ");
		sb.append("order by a.column_id");
		
		List<Map> list=MyDao.getListMap(sb.toString());
		if (list!=null&&list.size()>0) {
			ArrayList<TableColumn> listOut=new ArrayList<TableColumn>(list.size());
			for (Map map:list) {
				TableColumn tc=new TableColumn();
				tc.setColumnName(map.get("COLUMN_NAME")+"");
				tc.setComments(map.get("COMMENTS")+"");
				tc.setDataLength(new Integer(map.get("DATA_LENGTH")+""));
				tc.setDataType(map.get("DATA_TYPE")+"");
				listOut.add(tc);
			}
			return listOut;
		}
		return null;
	}
	
	public static List<TableColumn> getListTableNoPrimaryKeyColumn(String tableName) {
		StringBuffer sb=new StringBuffer();
		sb.append("select a.table_name,a.column_name,a.data_type,a.data_length,replace(replace(replace(replace(replace(replace(replace(b.comments,'，',','),'：',','),':',','),' ',','),'(',','),'（',','),'.',',') comments ");
		sb.append("from user_tab_cols a ");
		sb.append("	left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name ");
		sb.append("where a.table_name=upper('"+tableName+"') ");
		sb.append("	and a.column_name not in ( ");
		sb.append("            select d.column_name ");
		sb.append("            from user_constraints c,user_cons_columns d ");
		sb.append("            where c.owner=d.owner  ");
		sb.append("                and c.table_name=d.table_name  ");
		sb.append("                and c.constraint_name=d.constraint_name ");
		sb.append("                and c.constraint_type='P' ");
		sb.append("                and c.table_name=upper('"+tableName+"') ");
		sb.append("        ) ");
		sb.append("order by a.column_id");
		
		List<Map> list=MyDao.getListMap(sb.toString());
		if (list!=null&&list.size()>0) {
			ArrayList<TableColumn> listOut=new ArrayList<TableColumn>(list.size());
			for (Map map:list) {
				TableColumn tc=new TableColumn();
				tc.setColumnName(map.get("COLUMN_NAME")+"");
				tc.setComments(map.get("COMMENTS")+"");
				tc.setDataLength(new Integer(map.get("DATA_LENGTH")+""));
				tc.setDataType(map.get("DATA_TYPE")+"");
				listOut.add(tc);
			}
			return listOut;
		}
		return null;
	}
	
	public static List<TableColumn> listTableCharDateColumn(String tableName) {
		StringBuffer sb=new StringBuffer();
		sb.append("select a.table_name,a.column_name,a.data_type,a.data_length,replace(replace(replace(replace(replace(replace(replace(b.comments,'，',','),'：',','),':',','),' ',','),'(',','),'（',','),'.',',') comments ");
		sb.append("from user_tab_cols a ");
		sb.append("	left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name ");
		sb.append("where a.table_name=upper('"+tableName+"') ");
		sb.append("	and a.data_type in ('CHAR','VARCHAR2','DATE','TIMESTAMP(6)')");
		sb.append("	and a.column_name not in ( ");
		sb.append("            select d.column_name ");
		sb.append("            from user_constraints c,user_cons_columns d ");
		sb.append("            where c.owner=d.owner  ");
		sb.append("                and c.table_name=d.table_name  ");
		sb.append("                and c.constraint_name=d.constraint_name ");
		sb.append("                and c.constraint_type='P' ");
		sb.append("                and c.table_name=upper('"+tableName+"') ");
		sb.append("        ) ");
		sb.append("order by a.column_id");
		
		List<Map> list=MyDao.getListMap(sb.toString());
		if (list!=null&&list.size()>0) {
			ArrayList<TableColumn> listOut=new ArrayList<TableColumn>(list.size());
			for (Map map:list) {
				TableColumn tc=new TableColumn();
				tc.setColumnName(map.get("COLUMN_NAME")+"");
				tc.setComments(map.get("COMMENTS")+"");
				tc.setDataLength(new Integer(map.get("DATA_LENGTH")+""));
				tc.setDataType(map.get("DATA_TYPE")+"");
				listOut.add(tc);
			}
			return listOut;
		}
		return null;
	}
	
	/**
	 * 如：ORG_ID 转换成： orgId
	 * _ORG_ID_ 转换成： orgId
	 */
	public static String toJavaFirstLower(String str){
		if (str==null||str.length()<1) {
			return "";
		}
		StringBuffer sb=new StringBuffer();
		boolean flag=false;		// true 当前字符为符号
		boolean first=false;	// false 第一个字母未出现
		for (int i=0;i<str.length();i++) {
			char c=str.charAt(i);
			if (isCharNum(c)) {
				if (flag&&first) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				};
				flag=false;
				if (!first) {
					first=true;
				}
			} else {
				flag=true;
			}
		}
		return sb.toString();
	}
	
	/**
	 * 如：T_SYM_ORG 转换成： TSymOrg
	 * _T_SYM_ORG_ 转换成： TSymOrg
	 */
	public static String toJavaFirstUpper(String str){
		if (str==null||str.length()<1) {
			return "";
		}
		StringBuffer sb=new StringBuffer();
		boolean flag=true;
		for (int i=0;i<str.length();i++) {
			char c=str.charAt(i);
			if (isCharNum(c)) {
				if (flag) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				};
				flag=false;
			} else {
				flag=true;
			}
		}
		return sb.toString();
	}
	
	/**
	 * get方法
	 */
	public static String toGetMethod(String str){
		if (str==null||str.length()<1) {
			return "";
		}
		return "get"+toJavaFirstUpper(str);
	}
	
	/**
	 * set方法
	 */
	public static String toSetMethod(String str){
		if (str==null||str.length()<1) {
			return "";
		}
		return "set"+toJavaFirstUpper(str);
	}
	
	public static String getTableComment(String tableName) {
		String sql="select * from user_tab_comments a where a.table_name='"+tableName+"' and a.table_type='TABLE'";
		List<Map> list=MyDao.getListMap(sql);
		String str=null;
		if (list!=null&&list.size()==1) {
			Map map=list.get(0);
			str=(String)map.get("COMMENTS");
		}
		if (str==null) {
			str=tableName;
		}
		return str;
	}
	
	/**
	 * 判断字符串是否为空
	 */
	public static boolean isStrEmpt(String str){
		if (str==null||str.trim().length()<1){
			return true;
		}
		return false;
	}
	
	/**
	 * 忽略符号与大小写的比较，即只对英文字母和数字进行比较
	 */
	public static boolean isEqualsIgnoreSign(String str1,String str2){
		if (str1==null||str2==null){
			return false;
		}
		char c1;
		char c2;
		int i=0;
		int j=0;
		int res=0;
		while(i<str1.length()&&j<str2.length()){
			c1=str1.charAt(i);
			if (!isCharNum(c1)){
				i++;
				continue;
			}
			c2=str2.charAt(j);
			if (!isCharNum(c2)){
				j++;
				continue;
			}
			res=c1-c2;
			if (!(res==0||res==32||res==-32)){
				return false;
			}
			i++;
			j++;
		}
		if (i<str1.length()){
			while(i<str1.length()){
				if (isCharNum(str1.charAt(i))){
					return false;
				}
				i++;
			}
		}
		if (j<str2.length()){
			while(j<str2.length()){
				if (isCharNum(str2.charAt(j))){
					return false;
				}
				j++;
			}
		}
		return true;
	}
	
	/**
	 * 是否字母或者数字 
	 */
	public static boolean isCharNum(char c){
		if ((c>='A'&&c<='Z')||(c>='a'&&c<='z')||(c>='0'&&c<='9')){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否字母 
	 */
	public static boolean isChar(char c) {
		return ((c>='a'&&c<='z')||(c>='A'&&c<='Z'));
	}
	
	public static void main(String args[]) {
		System.out.println(getListTableColumn("t_sym_org").size());
	}

	public static String getClassPath(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
