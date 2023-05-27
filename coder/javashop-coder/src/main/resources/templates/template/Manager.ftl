package ${packageStr};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.Page;
${importStr}

/**
 * ${model.model_name}业务类
 * @author ${username}
 * @version v${version}
 * @since v${since}
 * ${time}
 */
@Service
public class ${className}ManagerImpl implements ${className}Manager{

	@Autowired
	private	DaoSupport	daoSupport;
	
	@Override
	public Page list(int page,int pageSize){
		
		String sql = "select * from ${model.table_name}  ";
		Page  webPage = this.daoSupport.queryForPage(sql,page, pageSize ,${className}.class );
		
		return webPage;
	}
	
	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	${className}  add(${className}	${model.english_name})	{
		this.daoSupport.insert(${model.english_name});
		
		return ${model.english_name};
	}
	
	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	${className}  edit(${className}	${model.english_name},Integer id){
		this.daoSupport.update(${model.english_name}, id);
		return ${model.english_name};
	}
	
	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Integer id)	{
		this.daoSupport.delete(${className}.class,	id);
	}
	
	@Override
	public	${className} getModel(Integer id)	{
		return this.daoSupport.queryForObject(${className}.class, id);
	}
}
