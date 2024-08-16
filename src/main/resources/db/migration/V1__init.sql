CREATE TABLE project_service.answer
(
    value        VARCHAR(255) NOT NULL,
    fk_sample_id VARCHAR(255) NOT NULL,
    fk_field_id  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_answer PRIMARY KEY (fk_sample_id, fk_field_id)
);

CREATE TABLE project_service.dynamic_field
(
    id           VARCHAR(36)   NOT NULL,
    name         VARCHAR(255)  NOT NULL,
    value        VARCHAR(255)  NOT NULL,
    number_order INT DEFAULT 0 NOT NULL,
    fk_sample_id VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_dynamic_field PRIMARY KEY (id)
);

CREATE TABLE project_service.field
(
    id           VARCHAR(36)   NOT NULL,
    number_order INT DEFAULT 0 NOT NULL,
    name         VARCHAR(255)  NOT NULL,
    fk_form_id   VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_field PRIMARY KEY (id)
);

CREATE TABLE project_service.form
(
    id            VARCHAR(36)  NOT NULL,
    title         VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    fk_project_id VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_form PRIMARY KEY (id)
);

CREATE TABLE project_service.project
(
    id            VARCHAR(36)        NOT NULL,
    name          VARCHAR(255)       NOT NULL,
    `description` VARCHAR(255)       NULL,
    status        SMALLINT DEFAULT 0 NOT NULL,
    start_date    date               NULL,
    end_date      date               NULL,
    fk_owner_id   VARCHAR(36)        NOT NULL,
    CONSTRAINT pk_project PRIMARY KEY (id)
);

CREATE TABLE project_service.project_member
(
    member_id  VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_project_member PRIMARY KEY (member_id, project_id)
);

CREATE TABLE project_service.sample
(
    id                VARCHAR(36)  NOT NULL,
    position          VARCHAR(255) NULL,
    attachment_id     VARCHAR(255) NOT NULL,
    created_timestamp datetime     NULL,
    fk_project_id     VARCHAR(36)  NOT NULL,
    fk_stage_id       VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_sample PRIMARY KEY (id)
);

CREATE TABLE project_service.stage
(
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    start_date    date         NULL,
    end_date      date         NULL,
    fk_form_id    VARCHAR(36)  NULL,
    fk_project_id VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_stage PRIMARY KEY (id)
);

CREATE TABLE project_service.user
(
    id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE project_service.answer
    ADD CONSTRAINT FK_ANSWER_ON_FK_FIELD FOREIGN KEY (fk_field_id) REFERENCES project_service.field (id);

ALTER TABLE project_service.answer
    ADD CONSTRAINT FK_ANSWER_ON_FK_SAMPLE FOREIGN KEY (fk_sample_id) REFERENCES project_service.sample (id);

ALTER TABLE project_service.dynamic_field
    ADD CONSTRAINT FK_DYNAMIC_FIELD_ON_FK_SAMPLE FOREIGN KEY (fk_sample_id) REFERENCES project_service.sample (id);

ALTER TABLE project_service.field
    ADD CONSTRAINT FK_FIELD_ON_FK_FORM FOREIGN KEY (fk_form_id) REFERENCES project_service.form (id);

ALTER TABLE project_service.form
    ADD CONSTRAINT FK_FORM_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project_service.project (id);

ALTER TABLE project_service.project
    ADD CONSTRAINT FK_PROJECT_ON_FK_OWNER FOREIGN KEY (fk_owner_id) REFERENCES project_service.user (id);

ALTER TABLE project_service.sample
    ADD CONSTRAINT FK_SAMPLE_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project_service.project (id);

ALTER TABLE project_service.sample
    ADD CONSTRAINT FK_SAMPLE_ON_FK_STAGE FOREIGN KEY (fk_stage_id) REFERENCES project_service.stage (id);

ALTER TABLE project_service.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_FORM FOREIGN KEY (fk_form_id) REFERENCES project_service.form (id);

ALTER TABLE project_service.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project_service.project (id);

ALTER TABLE project_service.project_member
    ADD CONSTRAINT fk_promem_on_project FOREIGN KEY (project_id) REFERENCES project_service.project (id);

ALTER TABLE project_service.project_member
    ADD CONSTRAINT fk_promem_on_user FOREIGN KEY (member_id) REFERENCES project_service.user (id);