package net.loyin.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 奖金记录
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "bonus_record")
public class BonusRecord extends Model<BonusRecord> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "bonus_record";
	public static BonusRecord dao=new BonusRecord();
	
	public Page<BonusRecord> page(int pageNo, int pageSize, Map<String, Object> filter) throws Exception {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" where user_id =? ");
		parame.add(filter.get("user_id"));
		String date=(String)filter.get("date");
		if(StringUtils.isNotEmpty(date)){
			sql.append(" and create_datetime between ? and ?");
			String[] as=date.split("/");
			if(as[1].equals("1")){//上半月
				parame.add(as[0]+"-01 00:00:00");
				parame.add(as[0]+"-15 23:59:59");
			}else{//下半月
				Date date_=BaseController.dateFormat.parse(as[0]+"-01");
				Calendar cal=Calendar.getInstance();
				cal.setTime(date_);
				cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
				cal.set(Calendar.DAY_OF_MONTH,1);
				cal.set(Calendar.DATE,cal.get(Calendar.DATE)-1);
				parame.add(as[0]+"-16 00:00:00");
				parame.add(as[0]+"-"+cal.get(Calendar.DAY_OF_MONTH)+" 23:59:59");
			}
		}
		sql.append(" order by create_datetime desc");
		return this.paginate(pageNo, pageSize, "select *",sql.toString(),parame.toArray());
	}
	/***
	 * 每天计算奖金
	 */
	@Before(Tx.class)
	public void fa(){
		//市场分红 0:市场分红
		Float a=12.415f,b=6.685f,c=19.1f;
		Db.update("INSERT INTO "+tableName+" (user_id,sn,bonus_type,gold_coins,gold_seeds,lj,create_datetime) select id,sn,0,"+a+","+b+","+c+",now() from "+User.tableName+" where status=1 and fhcj=0 ");
		Db.update("update "+User.tableName+" set gold_coin=gold_coin+"+a+",gold_seeds=gold_seeds+"+b+",bonus_0=bonus_0+"+c+" where status=1 and fhcj=0 ");
		//加入到金币明细表
		Db.update("INSERT INTO "+GoldTransDetail.tableName+" (user_id,sn,augment,remark,create_datetime) select id,sn,"+a+",'市场分红',now() from "+User.tableName+" where status=1 and fhcj=0 ");
		//领导奖1:领导奖 有效子账号数大于0
		Float a1=1.2415f,b1=0.6685f,c1=1.91f;
		Db.update("INSERT INTO "+tableName+" (user_id,sn,bonus_type,gold_coins,gold_seeds,lj,create_datetime) select id,sn,1,account_count_2*"+a1+",account_count_2*"+b1+",account_count_2*"+c1+",now() from "+User.tableName+" where status=1 and  account_count_2>0");
		Db.update("update "+User.tableName+" set gold_coin=gold_coin+account_count_2*"+a1+",gold_seeds=gold_seeds+account_count_2*"+b1+",bonus_1=bonus_1+account_count_2*"+c1+" where status=1 and account_count_2>0");
		//加入到金币明细表
		Db.update("INSERT INTO "+GoldTransDetail.tableName+" (user_id,sn,augment,remark,create_datetime) select id,sn,account_count_2*"+a1+",'领导奖',now() from "+User.tableName+" where status=1 and account_count_2>0");
	}
	public void clear(Date time) {
		Db.update("delete from "+tableName+" where create_datetime <? ",time);
	}
}
