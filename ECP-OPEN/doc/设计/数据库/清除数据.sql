delete from fa_invoice;
delete from fa_pay_receiv_ables;
delete from fa_pay_receiv_order;
delete from fa_account_detail;
delete from fa_account;

delete from scm_stock_allot_list;
delete from scm_stock_allot;
delete from scm_stock_check_list;
delete from scm_storage_bill_list;
delete from scm_storage_bill;
delete from scm_stock_check;
delete from scm_stock;
delete from sso_daily_phrase;
delete from sso_person where id not in(select id from sso_user) ;
delete from wf_audit_detail;

delete from scm_competitor;
delete from scm_depot;
delete from scm_order_data;
delete from scm_order_product;
delete from scm_order;
delete from scm_product_price;
delete from scm_product_price_order;
delete from scm_product;

delete from crm_business_data;
delete from crm_business;
delete from crm_campaigns;
delete from crm_customer_record;
delete from crm_customer_data;
delete from crm_leads_data;
delete from crm_leads;
delete from crm_contacts;
delete from crm_customer;

update base_sncreater set dqxh=0