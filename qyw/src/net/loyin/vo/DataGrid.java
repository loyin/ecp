package net.loyin.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

/**
 * jqGrid的datagrid模型
 * @author loyin
 */
public class DataGrid implements Serializable {
	private static final long serialVersionUID = 6732325477388107799L;
	/** 总记录数 */
	private int records;
	/**总页数*/
	private int total;
	/**当前页*/
	private int page;
	/**包含实际数据的数组*/
	private List<Map<String,Object>> rows;
	public DataGrid(){
	}
	/**
	 * 
	 * @param records_
	 * @param total_
	 * @param page_
	 * @param dataList_
	 * @param qryCols 查询字段 以逗号分隔
	 */
	public DataGrid(int records_,int total_,int page_,List<Record> dataList_,String qryCols){
		this.records=records_;
		this.total=total_;
		this.page=page_;
		if(dataList_!=null&&dataList_.isEmpty()==false){
			rows=new ArrayList<Map<String,Object>>(dataList_.size());
			String[] cols= qryCols==null?dataList_.get(0).getColumnNames():qryCols.split(",");
			int i=1;
			for(Record r:dataList_){
				Map<String,Object> row=new HashMap<String,Object>();
				row.put("id",i++);
				for(String col:cols){
					row.put(col,r.get(col));
				}
				rows.add(row);
			}
		}
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<?> getRows() {
		return rows;
	}
	public void setRows(List<Map<String,Object>> rows) {
		this.rows = rows;
	}
}
