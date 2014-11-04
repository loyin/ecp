package net.loyin.ctrl.scm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.scm.Stock;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
/**
 * 仓库库存
 * @author 龙影
 */
@RouteBind(path="stock",sys="仓库",model="库存")
public class StockCtrl extends AdminBaseController<Stock> {
	public StockCtrl(){
		this.modelClass=Stock.class;
	}
	/**当前库存*/
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("keyword",this.getPara("keyword"));
		filter.put("category_id",this.getPara("category_id"));
		filter.put("depot_id",this.getPara("depot_id"));
		filter.put("company_id",this.getCompanyId());
		this.sortField(filter);
		Page<Stock> page = Stock.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}
	/**上传初始化库存excel文件*/
	@PowerBind(code="A4_1_E",funcName="库存初始化")
	public void upfile(){
		UploadFile uf=this.getFile();
		if(uf==null||uf.getFile()==null){
			this.rendJson(false, null, "文件未上传！");
			return;
		}
		String company_id=this.getPara(0);
		if(StringUtils.isEmpty(company_id)){
			this.rendJson(false, null, "参数不正确！");
			return;
		}
		try {
			InputStream is=new FileInputStream(uf.getFile());
			Workbook wkb=null;
			try{
				wkb=new HSSFWorkbook(is);
			}catch(Exception e){
				is.close();
				try{
					wkb=new XSSFWorkbook(is);
				}catch(Exception e1){
					throw e1;
				}
			}
			List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
			Sheet sheet= wkb.getSheetAt(0);
			if(sheet.getLastRowNum()>1){
				for(int idx=1;idx<=sheet.getLastRowNum();idx++){
					Map<String,Object> map=new HashMap<String,Object>();
					Row row=sheet.getRow(idx);
					if(row==null)
						continue;
					map.put("product_sn",row.getCell(0).getStringCellValue());
					map.put("depot_name",row.getCell(5).getStringCellValue());
					map.put("amount",row.getCell(4).getNumericCellValue());
					dataList.add(map);
				}
				Stock.dao.init(dataList,company_id);
				is.close();
				this.rendJson(true, null, "库存初始化成功！");
			}else{
				this.rendJson(false,null,"文件数据为空！");
			}
		} catch (Exception e) {
			log.error(e);
			this.rendJson(false,null,"处理文件异常!请保证格式及数据是否正确!"+e.getMessage());
		}finally{
			uf.getFile().delete();
		}
	}
	/**库存预警*/
	public void warnDataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		String keyword=this.getPara("keyword");
		filter.put("keyword",keyword);
		String category_id=this.getPara("category_id");
		filter.put("category_id",category_id);
		String depot_id=this.getPara("depot_id");
		filter.put("depot_id",depot_id);
		filter.put("company_id",this.getCompanyId());
		this.sortField(filter);
		Page<Stock> page = Stock.dao.warnPageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}
}
