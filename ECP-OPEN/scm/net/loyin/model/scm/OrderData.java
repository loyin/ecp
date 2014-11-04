package net.loyin.model.scm;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;
/**
 * 订单数据
 * @author 龙影
 * 2014年9月23日
 */
@TableBind(name="scm_order_data")
public class OrderData extends Model<OrderData> {
	private static final long serialVersionUID = 1694015863938065042L;
	public static final String tableName="scm_order_data";
	public static OrderData dao=new OrderData();
}
