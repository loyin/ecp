
-- DROP VIEW "v_position";
CREATE OR REPLACE VIEW "v_position" AS 
 SELECT d.company_id,
    d.name,
    d.id,
        CASE
            WHEN d.type = 0 THEN d.parent_id
            ELSE d.department_id
        END AS pid,
    d.type,d.department_id
    d.sort_num
   FROM sso_position d;
ALTER TABLE "v_position"
  OWNER TO ecp;
COMMENT ON VIEW "v_position"
  IS '岗位及部门';

-- View: v_user_position

-- DROP VIEW v_user_position;

CREATE OR REPLACE VIEW v_user_position AS 
            SELECT u.id,
            ps.realname AS name,
	    	u.uname as uname,
            p.id AS pid,
            10 AS type,
            1 AS sort_num,
            p.company_id
           FROM sso_user u,
            sso_person ps,
            sso_position p
          WHERE p.id::text = u.position_id::text AND u.id::text = ps.id::text AND u.status=1
UNION
         SELECT p1.id,
            p1.name,
	    '' as uname,
            p1.parent_id pid,
            p1.type,
            p1.sort_num,
            p1.company_id
           FROM sso_position p1 where p1.type=1;

ALTER TABLE v_user_position
  OWNER TO ecp;
COMMENT ON VIEW v_user_position
  IS '用户岗位';

  
CREATE OR REPLACE VIEW v_user_department AS 
            SELECT u.id,
            ps.realname AS name,
	    	u.uname as uname,
            p.department_id AS pid,
            10 AS type,
            1 AS sort_num,
            p.company_id
           FROM sso_user u,
            sso_person ps,
            sso_position p
          WHERE p.id::text = u.position_id::text AND u.id::text = ps.id::text AND u.status=1
UNION
         SELECT p1.id,
            p1.name,
	    '' as uname,
            p1.parent_id pid,
            p1.type,
            p1.sort_num,
            p1.company_id
           FROM sso_position p1;

ALTER TABLE v_user_department
  OWNER TO ecp;
COMMENT ON VIEW v_user_department
  IS '用户部门';
  
---用户的上级 含岗位及部门、用户
WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id='用户id'
union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.id = d.pid) 
SELECT * FROM d
---用户的下级 含岗位及部门、用户 
WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id='用户id'
union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) 
SELECT * FROM d

---参数下级 
WITH RECURSIVE d AS (SELECT d1.id,d1.parent_id as pid,d1.name,d1.sort_num,d1.typ,d1.is_end FROM sso_parame d1 where d1.id=? and d1.typ=?
union ALL SELECT d2.id,d2.parent_id as pid,d2.name,d2.sort_num,d2.typ,d2.is_end FROM sso_parame d2, d WHERE d2.parent_id = d.id) 
SELECT * FROM d
