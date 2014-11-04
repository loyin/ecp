package net.loyin.ctrl.oa;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.oa.Comment;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;

/**
 * 评论
 * @author 龙影 2014年9月19日
 */
@RouteBind(path = "comment")
public class CommentCtrl extends AdminBaseController<Comment> {
	public CommentCtrl() {
		this.modelClass = Comment.class;
	}
	public void dataGrid() {
		Page<Comment> page = Comment.dao.pageGrid(getPageNo(), getPageSize(),this.getPara("id"));
		this.rendJson(true,null, "success", page);
	}
	public void save() {
		try {
			Comment po = (Comment) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			if (StringUtils.isEmpty(id)) {
				pullUser(po,this.getCurrentUserId());
				po.save();
				id=po.getStr("id");
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存产品异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@RouteBind(code="000DEL", model = "", name = "")
	public void del() {
		try {
			getId();
			Comment.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
