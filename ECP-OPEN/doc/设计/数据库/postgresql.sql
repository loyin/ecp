--建一个序列
create sequence id_seq increment 1 minvalue 1 maxvalue 9223372036854775807  cache 1;
--然后在id的默认值上设置：nextval('id_seq')
----部门
INSERT INTO sso_department(id,pid, name, description, del) VALUES(1,0,'总经理室','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(3,0,'技术部','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(4,0,'财务部','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(5,0,'行政部','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(6,0,'销售部','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(7,0,'商务部','',false);
INSERT INTO sso_department(id,pid, name, description, del) VALUES(8,0,'仓管部','',false);

--岗位
INSERT INTO sso_position(id, pid, depart_id, name, description, del)VALUES (?, ?, ?, ?, ?, ?);
INSERT INTO sso_position(id, pid, depart_id, name, description, del)VALUES (1, 0, 1, 'CEO', '', false);
---模块
INSERT INTO sso_module(id, name, description, url, sort)VALUES (?, ?, ?, ?, ?);
---用户
INSERT INTO sso_user(id,department_id,position_id,adm,emp_id,status,uname,pwd,sex,navigation,simple_menu,dashboard,login_ip,reg_time,last_login_time,lostpw_time,headimg,wxopenid,wxnickname,del)
VALUES(1,1,1,FALSE,,1,'loyin','265dcf7e0aed4b6be1c126d3f17fe52f','男','','','','','2014-01-01 00:00:00','','','','','')
--VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);