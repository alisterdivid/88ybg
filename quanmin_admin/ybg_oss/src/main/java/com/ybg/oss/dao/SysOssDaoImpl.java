package com.ybg.oss.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.ybg.base.jdbc.BaseDao;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.jdbc.util.QvoConditionUtil;
import com.ybg.base.util.Page;
import com.ybg.oss.domian.SysOssEntity;
/*** @author https://gitee.com/YYDeament/88ybg 

 * @date 2016/10/1 */
@Repository
public class SysOssDaoImpl extends BaseDao implements SysOssDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	@Override
	public SysOssEntity queryObject(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("	select id,url,create_date from sys_oss oss where id = " + id);
		List<SysOssEntity> list = getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<SysOssEntity>(SysOssEntity.class));
		return QvoConditionUtil.checkList(list) ? list.get(0) : null;
	}
	
	@Override
	public void save(SysOssEntity sysOss) throws Exception {
		Map<String, Object> createmap = new BaseMap<String, Object>();
		createmap.put("url", sysOss.getUrl());
		createmap.put("create_date", sysOss.getCreatedate());
		basecreate(createmap, "sys_oss", false, null);
	}
	
	@Override
	public void update(SysOssEntity sysOss) {
		BaseMap<String, Object> wheremap = new BaseMap<>();
		BaseMap<String, Object> updatemap = new BaseMap<>();
		updatemap.put("url", sysOss.getUrl());
		updatemap.put("create_date", sysOss.getCreatedate());
		wheremap.put("id", sysOss.getId());
		baseupdate(updatemap, wheremap, "sys_oss");
	}
	
	@Override
	public void delete(Long id) {
		BaseMap<String, Object> conditionmap = new BaseMap<>();
		conditionmap.put("id", id);
		if (id != null) {
			baseremove(conditionmap, "sys_oss");
		}
	}
	
	@Override
	public void deleteBatch(Long[] ids) {
		for (Long id : ids) {
			if (id != null) {
				BaseMap<String, Object> conditionmap = new BaseMap<>();
				conditionmap.put("id", id);
				baseremove(conditionmap, "sys_oss");
			}
		}
	}
	
	@Override
	public Page list(Page page, SysOssEntity qvo) {
		StringBuilder sql = new StringBuilder();
		sql.append("	select id,url,create_date from sys_oss oss ");
		sql.append(" where 1=1 ");
		page.setTotals(queryForInt(sql));
		if (page.getTotals() > 0) {
			List<SysOssEntity> list = getJdbcTemplate().query(page.getPagesql(sql), new BeanPropertyRowMapper<SysOssEntity>(SysOssEntity.class));
			page.setResult(list);
		}
		else {
			page.setResult(new ArrayList<SysOssEntity>());
		}
		return page;
	}
}
