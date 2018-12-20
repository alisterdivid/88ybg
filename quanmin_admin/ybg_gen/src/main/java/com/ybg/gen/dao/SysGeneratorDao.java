package com.ybg.gen.dao;
import java.util.List;
import java.util.Map;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.util.Page;
import com.ybg.gen.qvo.GeneratorQuery;

/** 代码生成器
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午3:32:04 */
public interface SysGeneratorDao {
	
	/** 分页查询
	 * 
	 * @param page
	 * @param qvo
	 * @return
	 * @throws Exception
	 */
	Page list(Page page, GeneratorQuery qvo) throws Exception;
	
	/** 查询表
	 * 
	 * @param tableName
	 * @return */
	Map<String, String> queryTable(String tableName);
	
	/** 查询列
	 * 
	 * @param tableName
	 * @return */
	List<Map<String, String>> queryColumns(String tableName);
	
	/** 获取生成配置 **/
	Map<String, String> queryGenSetting();
	
	/** 更新生成设置配置
	 * 
	 * @param updatemap
	 * @param wheremap
	 */
	void updateGenSetting(BaseMap<String, Object> updatemap, BaseMap<String, Object> wheremap);
}
