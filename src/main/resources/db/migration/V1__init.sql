CREATE TABLE project.answer
(
    fk_sample_id VARCHAR(36)  NOT NULL,
    value      VARCHAR(255) NOT NULL,
    fk_field_id  VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_answer PRIMARY KEY (fk_sample_id)
);

CREATE TABLE project.dynamic_field
(
    id           VARCHAR(36)   NOT NULL,
    name         VARCHAR(255)  NOT NULL,
    value        VARCHAR(255)  NOT NULL,
    number_order INT DEFAULT 0 NOT NULL,
    fk_sample_id VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_dynamic_field PRIMARY KEY (id)
);

CREATE TABLE project.field
(
    id           VARCHAR(36)   NOT NULL,
    number_order INT DEFAULT 0 NOT NULL,
    name      VARCHAR(255)  NOT NULL,
    fk_form_id   VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_field PRIMARY KEY (id)
);

CREATE TABLE project.form
(
    id            VARCHAR(36)  NOT NULL,
    title         VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    fk_project_id VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_form PRIMARY KEY (id)
);

CREATE TABLE project.project
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

CREATE TABLE project.project_member
(
    member_id  VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_project_member PRIMARY KEY (member_id, project_id)
);

CREATE TABLE project.sample
(
    id                VARCHAR(36)  NOT NULL,
    position          VARCHAR(255) NULL,
    created_timestamp datetime     NULL,
    fk_project_id     VARCHAR(36)  NOT NULL,
    fk_stage_id       VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_sample PRIMARY KEY (id)
);

CREATE TABLE project.stage
(
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    start_date    date         NULL,
    end_date      date         NULL,
    fk_form_id    VARCHAR(36)  NOT NULL,
    fk_project_id VARCHAR(36)  NOT NULL,
    CONSTRAINT pk_stage PRIMARY KEY (id)
);

CREATE TABLE project.user
(
    id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE project.answer
    ADD CONSTRAINT CK_FIELD_ID_AND_SAMPLE_ID UNIQUE (fk_field_id, fk_sample_id);

ALTER TABLE project.answer
    ADD CONSTRAINT FK_ANSWER_ON_FK_FIELD FOREIGN KEY (fk_field_id) REFERENCES project.field (id);

ALTER TABLE project.answer
    ADD CONSTRAINT FK_ANSWER_ON_FK_SAMPLE FOREIGN KEY (fk_sample_id) REFERENCES project.sample (id);

ALTER TABLE project.dynamic_field
    ADD CONSTRAINT FK_DYNAMIC_FIELD_ON_FK_SAMPLE FOREIGN KEY (fk_sample_id) REFERENCES project.sample (id);

ALTER TABLE project.field
    ADD CONSTRAINT FK_FIELD_ON_FK_FORM FOREIGN KEY (fk_form_id) REFERENCES project.form (id);

ALTER TABLE project.form
    ADD CONSTRAINT FK_FORM_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project.project (id);

ALTER TABLE project.project
    ADD CONSTRAINT FK_PROJECT_ON_FK_OWNER FOREIGN KEY (fk_owner_id) REFERENCES project.user (id);

ALTER TABLE project.sample
    ADD CONSTRAINT FK_SAMPLE_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project.project (id);

ALTER TABLE project.sample
    ADD CONSTRAINT FK_SAMPLE_ON_FK_STAGE FOREIGN KEY (fk_stage_id) REFERENCES project.stage (id);

ALTER TABLE project.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_FORM FOREIGN KEY (fk_form_id) REFERENCES project.form (id);

ALTER TABLE project.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project.project (id);

ALTER TABLE project.project_member
    ADD CONSTRAINT FK_PRO_MEM_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project.project (id);

ALTER TABLE project.project_member
    ADD CONSTRAINT FK_PRO_MEM_ON_USER FOREIGN KEY (member_id) REFERENCES project.user (id);