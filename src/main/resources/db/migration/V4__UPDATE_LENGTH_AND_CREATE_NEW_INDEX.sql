ALTER TABLE project_service.project
    ADD CONSTRAINT uc_project_thumbnailid UNIQUE (thumbnail_id);

ALTER TABLE project_service.sample
    ADD CONSTRAINT uc_sample_attachmentid UNIQUE (attachment_id);

CREATE INDEX attachmentId_idx ON project_service.sample (attachment_id);

ALTER TABLE project_service.sample
    MODIFY attachment_id VARCHAR(36) NOT NULL;

ALTER TABLE project_service.dynamic_field
    MODIFY name VARCHAR(100) NOT NULL;

ALTER TABLE project_service.field
    MODIFY name VARCHAR(100) NOT NULL;

ALTER TABLE project_service.project
    MODIFY name VARCHAR(100) NOT NULL;

ALTER TABLE project_service.stage
    MODIFY name VARCHAR(100) NOT NULL;

ALTER TABLE project_service.form
    MODIFY title VARCHAR(100) NOT NULL;