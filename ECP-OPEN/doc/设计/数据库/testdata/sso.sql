--测试数据
-- 企业
INSERT INTO sso_company (id, code, name, short_name, industry, telephone, fax, address, reg_email, reg_date, expiry_date, status, file_storage_size) VALUES 
('0', '0', '系统', '系统', 'IT解决方案', '', '', '', '', '2014-08-28', '2100-12-31', '1', 0),
('0001', '0001', '北京朗天鑫业信息工程技术有限公司', '朗天鑫业', 'IT解决方案', '010-12345678', '010-12345678', '北京', 'liusf@cnlentis.com', '2014-08-28', '2100-12-31', '1', 0);
-- 部门/岗位
INSERT INTO sso_position (id, company_id, parent_id, department_id, name,type) VALUES 
('00011', '0001', null, '00011', '总经理室',0),
('000111', '0001', null, '00011', '管理员',1),
('000112', '0001', null, '00011', '技术员'1);
-- 用户
INSERT INTO sso_user (id, company_id, position_id, is_admin, status, uname,password, login_ip, last_login_time) VALUES 
('0', '0', null, '1', '1', 'system', '0', '', ''),
('00123', '0001', '000111', '1', '1', 'loyin', '265dcf7e0aed4b6be1c126d3f17fe52f', '', '' )
;
--realname,sex, email, mobile, telephone, address,
INSERT INTO SSO_PERSON(id,realname, sex, email, mobile, telephone, address,head_pic)VALUES
('00123','用户','1', 'liusf@cnlentis.com', '12345678901', '010-12345678', '北京','/assets/img/head.jpg');
