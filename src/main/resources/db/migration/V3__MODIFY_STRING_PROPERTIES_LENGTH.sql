ALTER TABLE project_service.sample
    MODIFY attachment_id VARCHAR(36);

ALTER TABLE project_service.dynamic_field
    MODIFY name VARCHAR(100);

ALTER TABLE project_service.field
    MODIFY name VARCHAR(100);

ALTER TABLE project_service.project
    MODIFY name VARCHAR(100);

ALTER TABLE project_service.project
    MODIFY thumbnail_id VARCHAR(36);

ALTER TABLE project_service.form
    MODIFY title VARCHAR(100);