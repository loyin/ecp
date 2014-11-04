package net.loyin.model.sso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
/**
 * 序列号生成
 * @author 龙影
 * 2014年9月15日
 */
@TableBind(name="base_sncreater")
public class SnCreater extends Model<SnCreater> {
	private static final long serialVersionUID = -7717028702408247594L;
	public static final String tableName="base_sncreater";
	public static SnCreater dao=new SnCreater();
	public static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	public List<SnCreater> list(String company_id){
		return dao.find("select * from "+tableName+" where company_id=?",company_id);
	}
	public SnCreater findById(String id,String company_id){
		return dao.findFirst("select * from "+tableName+" where company_id=? and id=?",company_id,id);
	}
	public String create(String code,String company_id){
		Date now=new Date();
		SnCreater po=dao.findFirst("select * from "+tableName+" where code=? and company_id=?", code,company_id);
		Integer dqxh=po.getInt("dqxh")+1;//当前序号
		StringBuffer xh=new StringBuffer();
		xh.append(po.getStr("qz"));//前缀
		//判断是否需要日期加入
		Integer qyrq=po.getInt("qyrq");
		if(qyrq==1){
			String rqgs=po.getStr("rqgs");//日期格式
			String rq=new SimpleDateFormat(rqgs).format(now);
			xh.append(rq);
			//是否每日更新
			Integer sfmrgx=po.getInt("sfmrgx");
			String udate=po.getStr("udate");
			Date udate_=new Date();
			if(StringUtils.isNotEmpty(udate)){
				try {
					udate_=sdf.parse(udate);
				} catch (ParseException e) {
				}
			}
			switch(sfmrgx){
				case 0:
					break;
				case 1://每天更新
					if(udate!=null&&(sdf.format(udate_).equals(sdf.format(now))))
					break;
				case 2://每月
					if(udate!=null&&(udate_.getMonth()==now.getMonth()))
					break;
				case 3://每年
					if(udate!=null&&(udate_.getYear()==now.getYear()))
					break;
				default:
					dqxh=1;
			}
		}
		Integer ws=po.getInt("ws");//序号位数
		DecimalFormat nf=new DecimalFormat("0000");
		if(ws!=null&&ws!=4)
		{
			StringBuffer ss=new StringBuffer();
			for(int i=0;i<ws;i++){
				ss.append("0");
			}
			nf=new DecimalFormat(ss.toString());
		}
		xh.append(nf.format(dqxh));//序号
		/**最后更新*/
		Db.update("update "+tableName+" set dqxh=?,udate=? where id=?",dqxh,sdf.format(now),po.getStr("id"));
		return xh.toString();
	}
}