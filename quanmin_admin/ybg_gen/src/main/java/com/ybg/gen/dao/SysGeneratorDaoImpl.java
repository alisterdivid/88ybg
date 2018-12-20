package com.ybg.gen.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.ybg.base.jdbc.BaseDao;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.jdbc.BaseQueryAble;
import com.ybg.base.jdbc.DataBaseConstant;
import com.ybg.base.util.Page;
import com.ybg.gen.entity.TableEntity;
import com.ybg.gen.qvo.GeneratorQuery;

/*** @author https://gitee.com/YYDeament/88ybg
 * 
 * @date 2016/10/1 */
@Repository
public class SysGeneratorDaoImpl extends BaseDao implements SysGeneratorDao {
	
	/** 默认使用sys 如果修改数据库 @Qualifier 修改 @Qualifier(DataBaseConstant.JD_EDU) */
	@Autowired
	JdbcTemplate	jdbcTemplate;
	@Autowired
	@Qualifier(value = DataBaseConstant.JD_EDU)
	JdbcTemplate	edujdbcTemplate;
	@Autowired
	@Qualifier(value = DataBaseConstant.JD_OA)
	JdbcTemplate	oajdbcTemplate;
	@Autowired
	@Qualifier(value = DataBaseConstant.JD_QUARTZ)
	JdbcTemplate	quartzjdbcTemplate;
	
	@Override
	public JdbcTemplate getJdbcTemplate() {
		// 切换数据源
		String dbname = DataBaseConstant.getJdbcTemplate();
		if (dbname == null) {
			dbname = DataBaseConstant.JD_SYS;
		}
		switch (dbname) {
		case DataBaseConstant.JD_SYS:
			return jdbcTemplate;
		case DataBaseConstant.JD_EDU:
			return edujdbcTemplate;
		case DataBaseConstant.JD_OA:
			return oajdbcTemplate;
		case DataBaseConstant.JD_QUARTZ:
			return quartzjdbcTemplate;
		default:
			return jdbcTemplate;
		}
	}
	
	@Override
	public Page list(Page page, GeneratorQuery qvo) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables ");
		sql.append(" 	where table_schema = (select database())");
		sqlappen(sql, "table_name", qvo.getTablename(), new BaseQueryAble() {
			
			@Override
			public boolean isBlurred() {
				return true;
			}
		});
		page.setTotals(queryForInt(sql));
		if (page.getTotals() > 0) {
			List<TableEntity> list = getJdbcTemplate().query(page.getPagesql(sql), new BeanPropertyRowMapper<TableEntity>(TableEntity.class));
			page.setResult(list);
		}
		else {
			page.setResult(new ArrayList<TableEntity>());
		}
		return page;
	}
	
	@Override
	public Map<String, String> queryTable(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables ");
		sql.append(" where table_schema = (select database()) and table_name = '" + tableName + "'");
		Map<String, String> map = new LinkedHashMap<String, String>();
		getJdbcTemplate().query(sql.toString(), new RowMapper<Object>() {
			
			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				map.put("tableName", rs.getString("tableName"));
				map.put("engine", rs.getString("engine"));
				map.put("tableComment", rs.getString("tableComment"));
				map.put("createTime", rs.getString("createTime"));
				return null;
			}
		});
		return map;
	}
	
	@Override
	public List<Map<String, String>> queryColumns(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns");
		sql.append(" where table_name = '" + tableName + "' and table_schema = (select database()) order by ordinal_position");
		return getJdbcTemplate().query(sql.toString(), new RowMapper<Map<String, String>>() {
			
			@Override
			public Map<String, String> mapRow(ResultSet rs, int index) throws SQLException {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("columnName", rs.getString("columnName"));
				map.put("dataType", rs.getString("dataType"));
				map.put("columnComment", rs.getString("columnComment"));
				map.put("columnKey", rs.getString("columnKey"));
				map.put("extra", rs.getString("extra"));
				return map;
			}
		});
	}
	
	@Override
	public Map<String, String> queryGenSetting() {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append("`key`,`value` ").append(FROM).append(" sys_gen gen");
		Map<String, String> map = new LinkedHashMap<String, String>();
		jdbcTemplate.query(sql.toString(), new RowMapper<Object>() {
			
			@Override
			public Map<String, String> mapRow(ResultSet rs, int index) throws SQLException {
				map.put(rs.getString("key"), rs.getString("value"));
				return null;
			}
		});
		return map;
	}
	
	@Override
	public void updateGenSetting(BaseMap<String, Object> updatemap, BaseMap<String, Object> wheremap) {
		baseupdate(updatemap, wheremap, "sys_gen");
	}
}
