package net.loyin.model.sso;

import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 操作日志
 * @author 龙影
 */
@TableBind(name="sso_action_log")
public class ActionLog extends Model<ActionLog> {
	private static final long serialVersionUID = -2062896551324797433L;
	public static final String tableName="sso_action_log";
	public static ActionLog dao=new ActionLog();
	/**其他*/
	public static final int OTH_=0;
	/**查询*/
	public static final int QRY_=1;
	/**添加*/
	public static final int ADD_=2;
	/**删除*/
	public static final int DEL_=3;
	/**编辑*/
	public static final int EDIT_=4;
	/**授权*/
	public static final int AUTH_=5;
	/**审批*/
	public static final int PIHENPI_=6;
	/**提交*/
	public static final int SUBMIT_=7;
	/**导入*/
	public static final int IMPL_=8;
	/**导出*/
	public static final int EXPOUT_=9;
	/**设置权限*/
	public static final int POWER_ = 10;
	public Page<ActionLog> page(int pageNo, int pageSize,StringBuffer where, List<Object> param) {
		return dao.paginate(pageNo,pageSize,"select  * ",	"from " + tableName + " d where 1=1 "+ where.toString(), param.toArray());
	}
	/**
	 * 添加日志
	 * @param modelName	模块名称
	 * @param func					功能
	 * @param uid					用户id
	 * @param exct					执行操作
	 * @param detail				操作内容
	 */
	public void addLog(String modelName,String func,String uid,String ip,int exct,String detail){
		
	}
	public Page<ActionLog> page(int pageNo, int pageSize, Map<String, Object> filter,Integer paraToInt) {
		return null;
	}
}
