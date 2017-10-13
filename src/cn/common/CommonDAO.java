package cn.common;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommonDAO {
	
	@Autowired
	public SqlSessionTemplate sqlSessionTemplate;
	
}
