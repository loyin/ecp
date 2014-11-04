package net.loyin.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
/**
 *  jqGrid的datagrid模型
 * <pre>
 * {   
·   total: "xxx",   
·   page: "yyy",   
·   records: "zzz",  
·   rows : [  
·     {id:"1", cell:["cell11", "cell12", "cell13"]},  
·     {id:"2", cell:["cell21", "cell22", "cell23"]},  
·       ...  
·   ]  
· }</pre>
 * @author loyin
 */
public class CopyOfDataGrid implements Serializable {
	private static final long serialVersionUID = 6732325477388107799L;
	/** 总记录数 */
	private int records;
	/**总页数*/
	private int total;
	/**当前页*/
	private int page;
	/**包含实际数据的数组*/
	private List<Map<String,Object>> rows;
	public CopyOfDataGrid(){
	}
	public CopyOfDataGrid(int records_,int total_,int page_,List<Record> dataList_){
		this.records=records_;
		this.total=total_;
		this.page=page_;
		if(dataList_!=null&&dataList_.isEmpty()==false){
			rows=new ArrayList<Map<String,Object>>(dataList_.size());
			String[] cols= dataList_.get(0).getColumnNames();
			int i=1;
			for(Record r:dataList_){
				Map<String,Object> row=new HashMap<String,Object>();
				row.put("id",i++);
				List cell=new ArrayList();
				for(String col:cols){
					cell.add(r.get(col));
				}
				row.put("cell",cell);
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
