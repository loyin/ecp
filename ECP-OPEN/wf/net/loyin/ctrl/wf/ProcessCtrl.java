package net.loyin.ctrl.wf;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.wf.WfProcess;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryOrder;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.AssertHelper;
import org.snaker.engine.model.ProcessModel;
import org.snaker.helper.SnakerHelper;
import org.snaker.jfinal.plugin.SnakerPlugin;

import com.alibaba.fastjson.JSON;
import com.jfinal.upload.UploadFile;

/**
 * 工作流管理--流程部署
 * @author 龙影
 */
@SuppressWarnings("rawtypes")
@RouteBind(path = "process")
public class ProcessCtrl extends AdminBaseController {
	private Logger log=Logger.getLogger(getClass());
	private SnakerEngine engine=SnakerPlugin.getEngine();
	/**查看当前部署的流程*/
	public void dataGrid() {
	 	Map<String,Object> filter=new HashMap<String,Object>();
	 	filter.put("company_id", this.getCompanyId());
	 	this.rendJson(true, null, "",WfProcess.dao.page(this.getPageNo(),this.getPageSize(), filter));
	}
	/**部署流程*/
	public void deploy(){
		UploadFile file=this.getFile();
		try{
			getId();
			if(StringUtils.isEmpty(id)){
				id=engine.process().deploy(new FileInputStream(file.getFile()));
			}else{
				engine.process().redeploy(id,new FileInputStream(file.getFile()));
			}
			WfProcess.dao.upCompanyId(id, this.getCompanyId());
			this.rendJson(true, null, id);
		}catch(Exception e){
			log.error("",e);
			this.rendJson(false, null, "部署流程异常！");
		}finally{
			file.getFile().delete();
		}
	}
	/**卸载流程*/
	public void unDeploy(){
		try{
			engine.process().undeploy(getId());
			this.rendJson(true, null,id);
		}catch(Exception e){
			log.error("",e);
			this.rendJson(false, null, "卸载流程异常！");
		}
	}
	public void qryOp(){
		String orderId=this.getPara("orderId");
		Map<String,Object> jsonMap = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(orderId)){
			HistoryOrder order = engine.query().getHistOrder(orderId);
			List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(orderId));
			//{"activeRects":{"rects":[{"paths":[],"name":"任务3"},{"paths":[],"name":"任务4"},{"paths":[],"name":"任务2"}]},"historyRects":{"rects":[{"paths":["TO 任务1"],"name":"开始"},{"paths":["TO 分支"],"name":"任务1"},{"paths":["TO 任务3","TO 任务4","TO 任务2"],"name":"分支"}]}}
			if(tasks != null && !tasks.isEmpty()) {
				jsonMap.put("active", SnakerHelper.getActiveJson(tasks));
			}
			id=order.getProcessId();
		}else{
			getId();
		}
		Process process = engine.process().getProcessById(id);
		AssertHelper.notNull(process);
		ProcessModel model = process.getModel();
		if(model != null) {
			
			jsonMap.put("process", JSON.parse(SnakerHelper.getModelJson(model)));
		}
		this.rendJson(true,null,"",jsonMap);
	}
	/**获取待办任务*/
	public void queryTaskDataGrid(){
		Page page=new Page();
	 	QueryFilter filter=new QueryFilter();
		engine.query().getWorkItems(page,filter);
		this.rendJson(true, null, "",page);
	}
	/**查询历史*/
	public void qryHistoryDataGrid(){
		Page page=new Page();
	 	QueryFilter filter=new QueryFilter();
		engine.query().getHistoryOrders(page,filter);
		this.rendJson(true, null, "",page);
	}
	/***
	 * 查看流程定义
	 */
	public void qryProcess(){
		engine.process().getProcessByName(getId());
	}
}
