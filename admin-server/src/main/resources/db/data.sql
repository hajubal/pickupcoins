delete from cookie;
delete from site_user;
insert into site_user (ID, LOGIN_ID, USER_NAME, SLACK_WEBHOOK_URL, PASSWORD, ACTIVE, CREATED_BY, CREATED_DATE) values (1, 'user', '사용자', 'url', '{noop}user', 1, 'system', now());
insert into cookie (ID, SITE_USER_ID, SITE_NAME, USER_NAME, IS_VALID, CREATED_DATE, CREATED_BY) values (1, 1, 'naver', 'ha', 1, now(), 'application');