package mc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import mc.bean.TableColumn;
import mc.util.JavaOracleMap;
import mc.util.Util;


/**
 * 代码生成工具
 * @author czg
 * 2011-7-26
 * 目前只支持Oracle
 */
public class MC {
/***********************配置字段****************************/
	
	/*******************下面4项必须人工填*********************
	 *	原则上
	 *	service,dao的Spring访问beanName的来源，取表名的除了T开头以外的字符
	 *	controller,service,dao文件名的来源，取表名的除了(T_模块)以外的字符，但有时像T_PLAN_INFO这种表，controller文件名来源必须定义为PLAN_INFO
	 * 	classPack的定义，就是放controller，service的上级目录
	 ***************************************************/
	
	// 表名，日前只支持单表
	public static String tableName="t_sys_dict";
	// service,dao的Spring访问beanName来源 
	// 例如@Component(value="resSuppUserService")
	public static String modelName="FILE_TABLE";
	// controller,service,dao文件名来源
	public static String subModelName="FILE_TABLE";
	// 包路径 
	public static String classPack="cn.file";
	
	/****************************************上面4项必须人工填***************************************************/
	
	public static String sequenceName = "SEQ_"+modelName;
	
	// 输出文件位置
	public static String outFilePath="d:/tmladf/";
	// 模板位置
	public static String tmplFilePath=System.getProperty("user.dir")+"\\src\\mc\\tmpl";
	
	// 数据库连接串，用户名，密码
	public static String connUrl="jdbc:oracle:thin:@172.16.24.91:1521:tisson";
	public static String user="GPDI_DCP";
	public static String password="123qwe";
	
	public static String tableComment=Util.getTableComment(tableName);
	
	/********************************************************/
	
	public static String [] tmplFileNames={"J_VO","J_CONTROLLER","J_FACADE","J_FACADEIMPL","J_SERVICE","J_SERVICEIMPL","J_DAO","J_DAOIMPL","X_SqlMap","P_LIST"};
	public static String [] outFileNames={"${TABLE_NAME_JAVA}VO.java","${SUBMODEL_NAME_JAVA_FU}Controller.java","${SUBMODEL_NAME_JAVA_FU}Facade.java","${SUBMODEL_NAME_JAVA_FU}FacadeImpl.java","${SUBMODEL_NAME_JAVA_FU}Service.java","${SUBMODEL_NAME_JAVA_FU}ServiceImpl.java","${SUBMODEL_NAME_JAVA_FU}DAO.java","${SUBMODEL_NAME_JAVA_FU}DAOImpl.java","${TABLE_NAME_JAVA_FL}_SqlMap.xml","${SUBMODEL_NAME_JAVA_FL}Manager.jsp"};
	public static String [] packages={"entity","control","facade","facade.impl","service","service.impl","dao","dao.impl","ibatis",""};
	
	public static List<TableColumn> listTableColumn=Util.getListTableColumn(tableName);
	public static List<TableColumn> listTablePrimaryKeyColumn=Util.getListTablePrimaryKeyColumn(tableName);
	public static List<TableColumn> listTableNoPKColumn=Util.getListTableNoPrimaryKeyColumn(tableName);
	public static List<TableColumn> listTableCharDateColumn=Util.listTableCharDateColumn(tableName);
	
	private static String LOOP_LABEL_START="<MCLOOP";
	private static String LOOP_LABEL_BRACE=">";
	private static String LOOP_LABEL_END="</MCLOOP>";
	private static String LINE_SEPARATOR=System.getProperty("line.separator");
	
	public static void writeFile() {
		for (int i=0;i<tmplFileNames.length;i++) {
			String outFullPath=createPath(outFilePath, packages[i].replace("${JSP_PATH}",modelName.toLowerCase().replace("_",".")));
			try {   
	            File file = new File(outFullPath+"/"+outFileNames[i].replace("${TABLE_NAME_JAVA}", Util.toJavaFirstUpper(tableName)).replace("${TABLE_NAME_JAVA_FL}", Util.toJavaFirstLower(tableName)).replace("${TABLE_NAME}", tableName.toUpperCase()).replace("${MODEL_NAME_JAVA_FU}", Util.toJavaFirstUpper(modelName)).replace("${MODEL_NAME_JAVA_FL}", Util.toJavaFirstLower(modelName)).replace("${SUBMODEL_NAME_JAVA_FU}",Util.toJavaFirstUpper(subModelName)).replace("${SUBMODEL_NAME_JAVA_FL}",Util.toJavaFirstLower(subModelName)));   
	            OutputStreamWriter osw = new OutputStreamWriter(new  FileOutputStream(file), "utf-8");
	            List<String> contentList=readFile(tmplFilePath+"/"+tmplFileNames[i]+".txt");
	            if (contentList==null||contentList.size()<1) {
	    			return ;
	    		}
	            osw.write(genernateCode(contentList));   
	            osw.close();  
	        } catch (IOException e) {
	            e.printStackTrace();
	        }   
		}
	}
	
	public static String genernateCode(List<String> contentList) {
		StringBuffer sb=new StringBuffer();
		
		int startIndex=-1;
		int braceIndex=-1;
		int endIndex=-1;
		for (int i=0;i<contentList.size();i++) {
			String line=contentList.get(i);
			startIndex=line.indexOf(LOOP_LABEL_START);
			if (startIndex==-1) {
				sb.append(line+LINE_SEPARATOR);
			} 
			// 进入循环
			else {
				sb.append(line.substring(0,startIndex));
				braceIndex=line.indexOf(LOOP_LABEL_BRACE, startIndex);
				if (braceIndex<=startIndex) {
					return "";
				}
				String startLabel=line.substring(startIndex,braceIndex+1);
				StringBuffer loopContent=new StringBuffer();
				endIndex=line.indexOf(LOOP_LABEL_END,braceIndex+1);
				// 当前行没有结束符
				if (endIndex==-1) {
					loopContent.append(line.substring(braceIndex+1)+LINE_SEPARATOR);
					while (endIndex==-1&&(++i<contentList.size())) {
						line=contentList.get(i);
						endIndex=line.indexOf(LOOP_LABEL_END);
						// 没有结束符，则将内容添加到loopContent
						if (endIndex==-1) {
							loopContent.append(line+LINE_SEPARATOR);
						} else {
							loopContent.append(line.substring(0,endIndex));
						}
					}
				}
				// 当前行存在结束符
				else {
					loopContent.append(line.substring(braceIndex+1,endIndex));
				}
				
				sb.append(makeLoopCode(startLabel,loopContent.toString()));
				sb.append(line.substring(endIndex+LOOP_LABEL_END.length())+LINE_SEPARATOR);
			}
		}
		String content=sb.toString();
		content=content.replace("${CLASS_PATH}",classPack);
		
		content=content.replace("${TABLE_NAME_JAVA_FU}",Util.toJavaFirstUpper(tableName));
		content=content.replace("${TABLE_NAME_JAVA_FL}",Util.toJavaFirstLower(tableName));
		content=content.replace("${TABLE_NAME_UP}",tableName.toUpperCase());
		content=content.replace("${TABLE_NAME_LOW}",tableName.toLowerCase());
		content=content.replace("${TABLE_COMMENT}",tableComment);
		
		content=content.replace("${MODEL_NAME_UP}",modelName.toUpperCase());
		content=content.replace("${MODEL_NAME_LOW}",modelName.toLowerCase());
		content=content.replace("${MODEL_NAME_JAVA_FL}",Util.toJavaFirstLower(modelName));
		content=content.replace("${MODEL_NAME_JAVA_FU}",Util.toJavaFirstUpper(modelName));
		
		content=content.replace("${SUBMODEL_NAME_LOW}",subModelName.toLowerCase());
		content=content.replace("${SUBMODEL_NAME_UP}",subModelName.toUpperCase());
		content=content.replace("${SUBMODEL_NAME_JAVA_FU}",Util.toJavaFirstUpper(subModelName));
		content=content.replace("${SUBMODEL_NAME_JAVA_FL}",Util.toJavaFirstLower(subModelName));
		
		content=content.replace("${SUBPACKAGE_LOW}",modelName.substring(0,modelName.indexOf("_")).toLowerCase());
		return content;
	}
	
	/**
	 * 读取文件，并保存到List每行为一单元 
	 */
	public static List<String> readFile(String filePath) {
		BufferedReader br=null;
		String str="";
		List<String> list=new ArrayList<String>();
		try {
			InputStreamReader isr=new InputStreamReader(new FileInputStream(filePath),"utf-8");
			br=new BufferedReader(isr);
			while ((str=br.readLine())!=null) {
				list.add(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br!=null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/**
	 * 子循环 
	 */
	public static String makeLoopCode(String startLabel,String loopContent) {
		StringBuffer sb=new StringBuffer();
		String type=getValue(startLabel,"TYPE");
		
		List<TableColumn> list=null;
		
		if (type.equals("COLUMN")) {
			list=listTableColumn;
		} else if (type.equals("PRIMARYKEY")) {
			list=listTablePrimaryKeyColumn;
		} else if (type.equals("NOPRIMARYKEY")) {
			list=listTableNoPKColumn;
		} else if (type.equals("CHARDATE")) {
			list=listTableCharDateColumn;
		}
		
		String splitStr=getValue(startLabel,"APPENDSPLIT").replace("\\n", LINE_SEPARATOR).replace("\\t", "\t");
		for (int i=0;i<list.size();i++) {
			StringBuffer strLoop=new StringBuffer();
			
			TableColumn tc=list.get(i);
			
			int tmpi=0;
			
			int ibs=loopContent.indexOf("<MC");
			
			while (ibs>=0) {
				strLoop.append(loopContent.substring(tmpi,ibs));
				
				int ibe=loopContent.indexOf(">", ibs+"<MC".length());
				String lb=loopContent.substring(ibs,ibe+1);
				int ics=loopContent.indexOf("</MC", ibe+1);
				int ice=loopContent.indexOf(">",ics+1);
				
				String mcontent=loopContent.substring(ibe+1,ics);
				
				if (lb.startsWith("<MCIF")) {
					strLoop.append(makeIfCode(lb,mcontent,tc,i,list.size()));
				} else if (lb.startsWith("<MCCASE")) {
					strLoop.append(makeCaseCode(lb,mcontent,tc));
				}
				
				ibs=loopContent.indexOf("<MC",ice+1);
				tmpi=ice+1;
			}
			
			strLoop.append(loopContent.substring(tmpi));
			
			String str=strLoop.toString();
			str=str.replace("${COLUMN_NAME_UP}", tc.getColumnName());
			str=str.replace("${COLUMN_NAME_LOW}", tc.getColumnName().toLowerCase());
			str=str.replace("${COLUMN_NAME_JAVA_FL}", Util.toJavaFirstLower(tc.getColumnName()));
			str=str.replace("${COLUMN_NAME_JAVA_FU}", Util.toJavaFirstUpper(tc.getColumnName()));
			str=str.replace("${COLUMN_GETMETHOD}", Util.toGetMethod(tc.getColumnName()));
			str=str.replace("${COLUMN_SETMETHOD}", Util.toSetMethod(tc.getColumnName()));
			str=str.replace("${COLUMN_TYPE_JAVA}", JavaOracleMap.getJavaType(tc.getDataType()));
			str=str.replace("${COLUMN_TYPE_JDBC}", JavaOracleMap.getJavaJdbcType(tc.getDataType()));
			str=str.replace("${COLUMN_COMMENT}", tc.getComments().indexOf(",")<0?tc.getComments():tc.getComments().substring(0,tc.getComments().indexOf(",")));
			str=str.replace("${COLUMN_LENGTH}", tc.getDataLength()+"");
			str=str.replace("${INDEX}",(i+1)+"");
			sb.append(str);
			if (splitStr.length()>0&&i<list.size()-1) {
				sb.append(splitStr);
			}
		}
		return sb.toString();
	}
	
	public static String makeCaseCode(String label,String content,TableColumn tc) {
		List<String> listType=new ArrayList<String>();
		List<String> listContent=new ArrayList<String>();
		int ibs=content.indexOf("<MI");
		
		while (ibs>=0) {
			int ibe=content.indexOf(">", ibs+"<MI".length());
			int ics=content.indexOf("</MI", ibe+1);
			int ice=content.indexOf(">",ics+1);
			
			String lb=content.substring(ibs,ibe+1);
			String type=getValue(lb,"TYPE");
			listType.add(type);
			listContent.add(content.substring(ibe+1,ics));
			
			ibs=content.indexOf("<MI",ice+1);
		}
		
		for (int i=0;i<listType.size();i++) {
			String type=listType.get(i);
			if ((","+type+",").contains(","+JavaOracleMap.getJavaJdbcType(tc.getDataType())+",")) {
				return listContent.get(i);
			}
		}
		
		for (int i=0;i<listType.size();i++) {
			String type=listType.get(i);
			if ((","+type+",").contains(",ELSE,")) {
				return listContent.get(i);
			}
		}
		
		return "";
	}
	
	public static String makeIfCode(String label,String content,TableColumn tc,int i,int n) {
		String columntype=getValue(label, "COLUMNTYPE");
		
		if (columntype.equals("DATE")&&tc.getDataType().equals("DATE")) {
			return content;
		} else if (columntype.equals("NOTDATE")&&(!tc.getDataType().equals("DATE"))) {
			return content;
		}
		
		String rollNum=getValue(label,"ROLLNUM");
		if (rollNum!=null&&rollNum.length()>0) {
			Integer r=new Integer(rollNum);
			if (i>0&&i<n-1&&((i+1)%r==0)) {
				return content;
			}
		}
		return "";
	}
	
	/**
	 * 如：<MCLOOP TYPE=COLUMN gogo=gogog>，TYPE
	 * 返回COLUMN
	 */
	public static String getValue(String label,String key) {
		int pos=label.indexOf(key+"=");
		if (pos==-1) {
			return "";
		}
		pos+=(key+"=").length();
		int end=-1;
		if (label.indexOf(" ", pos)==-1) {
			end=label.indexOf(">", pos);
		} else if (label.indexOf(">", pos)==-1) {
			end=label.indexOf(" ", pos);
		} else {
			end=Math.min(label.indexOf(" ", pos), label.indexOf(">", pos));
		}
		return label.substring(pos, end).trim();
	}
	
	public static String createPath(String basePath,String subPath) {
		String fullPath=basePath+"/"+subPath.replace(".", "/");
		if (!new File(fullPath).exists()) { 
			new File(fullPath).mkdirs();
		}
		return fullPath;
	}
	
	public static void main(String args[]) {
		writeFile();
	}
}
