package com.ybg.oa.department.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.util.Json;
import com.ybg.base.util.Page;
import com.ybg.oa.department.domain.DepartmentVO;
import com.ybg.oa.department.service.DepartmentService;
import com.ybg.oa.company.domain.CompanyVO;
import com.ybg.oa.constant.CompanyConstant;
import com.ybg.oa.department.qvo.DepartmentQuery;

/** @author Deament
 * @email 591518884@qq.com
 * @date 2017-07-15 */
@Api(tags = "Department管理")
@Controller
@RequestMapping("/oa/department_do/")
public class DepartmentController {
	
	@Autowired
	private DepartmentService departmentService;
	
	@ApiOperation(value = "Department管理页面", notes = "", produces = MediaType.TEXT_HTML_VALUE)
	@RequestMapping(value = { "index.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String index(ModelMap map) {
		return "/company/department/department";
	}
	
	// @ApiOperation(value = "Department分页列表", notes = "JSON ", produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// @RequestMapping(value = { "list.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	// public Page list(@ModelAttribute DepartmentQuery qvo, @ModelAttribute Page page, ModelMap map) throws Exception {
	// qvo.setBlurred(true);
	// page = departmentService.list(page, qvo);
	// page.init();
	// return page;
	// }
	@ApiOperation(value = "Department分页列表", notes = "JSON ", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = { "treelist.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public List<DepartmentVO> treelist(@ModelAttribute DepartmentQuery qvo, ModelMap map) throws Exception {
		qvo.setBlurred(true);
		return departmentService.list(qvo);
	}
	
	@ApiOperation(value = "更新Department", notes = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = { "update.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public Json update(@ModelAttribute DepartmentVO department) {
		Json j = new Json();
		j.setSuccess(true);
		try {
			BaseMap<String, Object> updatemap = new BaseMap<String, Object>();
			updatemap.put("name", department.getName());
			updatemap.put("parentid", department.getParentid());
			// updatemap.put("companyname", department.getCompanyname());
			BaseMap<String, Object> wheremap = new BaseMap<String, Object>();
			wheremap.put("id", department.getId());
			departmentService.update(updatemap, wheremap);
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("操作失败");
			return j;
		}
		j.setMsg("操作成功");
		return j;
	}
	
	@ApiOperation(value = "根据ID删除department", notes = " ", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParam(name = "ids", value = "删除department", required = true, dataType = "java.lang.String")
	@ResponseBody
	@RequestMapping(value = { "remove.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public Json remove(@RequestParam(name = "ids", required = true) String ids2) {
		Json j = new Json();
		j.setSuccess(true);
		try {
			String[] ids = ids2.split(",");
			for (String id : ids) {
				if (id.equals(CompanyConstant.DEFAULT_DEPARTMENT_ID)) {
					// 不删除默认设置
					continue;
				}
				BaseMap<String, Object> wheremap = new BaseMap<String, Object>();
				wheremap.put("id", id);
				departmentService.remove(wheremap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("操作失败");
			return j;
		}
		j.setMsg("操作成功");
		return j;
	}
	
	@ApiOperation(value = "创建department", notes = " ", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = { "create.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public Json create(@ModelAttribute DepartmentVO bean) {
		Json j = new Json();
		j.setSuccess(true);
		try {
			CompanyVO company = CompanyConstant.getDefaultCompany();
			bean.setCompanyid(company.getId());
			bean.setCompanyname(company.getName());
			departmentService.save(bean);
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("操作失败");
			return j;
		}
		j.setMsg("操作成功");
		return j;
	}
	
	@ApiOperation(value = "获取单个OaDepartment", notes = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "java.lang.String") })
	@RequestMapping(value = { "get.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<Map<String, Object>> get(@RequestParam(name = "id", required = true) String id) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		DepartmentVO bean = departmentService.get(id);
		result.put("oaDepartment", bean);
		ResponseEntity<Map<String, Object>> map = new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		return map;
	}
}
