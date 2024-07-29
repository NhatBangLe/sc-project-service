CREATE TABLE project.attachment
(
    id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);

CREATE TABLE project.field
(
    id      VARCHAR(36)  NOT NULL,
    content VARCHAR(255) NOT NULL,
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
    status        SMALLINT DEFAULT 0 NULL,
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
    id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_sample PRIMARY KEY (id)
);

CREATE TABLE project.stage
(
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(255) NULL,
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

ALTER TABLE project.form
    ADD CONSTRAINT FK_FORM_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project.project (id);

ALTER TABLE project.project
    ADD CONSTRAINT FK_PROJECT_ON_FK_OWNER FOREIGN KEY (fk_owner_id) REFERENCES project.user (id);

ALTER TABLE project.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_FORM FOREIGN KEY (fk_form_id) REFERENCES project.form (id);

ALTER TABLE project.stage
    ADD CONSTRAINT FK_STAGE_ON_FK_PROJECT FOREIGN KEY (fk_project_id) REFERENCES project.project (id);

ALTER TABLE project.project_member
    ADD CONSTRAINT fk_promem_on_project FOREIGN KEY (project_id) REFERENCES project.project (id);

ALTER TABLE project.project_member
    ADD CONSTRAINT fk_promem_on_user FOREIGN KEY (member_id) REFERENCES project.user (id);