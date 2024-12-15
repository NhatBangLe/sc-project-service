CREATE TABLE project_service.stage_member
(
    member_id VARCHAR(36) NOT NULL,
    stage_id  VARCHAR(36) NOT NULL,
    CONSTRAINT pk_stage_member PRIMARY KEY (member_id, stage_id)
);

ALTER TABLE project_service.stage_member
    ADD CONSTRAINT fk_stamem_on_stage FOREIGN KEY (stage_id) REFERENCES project_service.stage (id);

ALTER TABLE project_service.stage_member
    ADD CONSTRAINT fk_stamem_on_user FOREIGN KEY (member_id) REFERENCES project_service.user (id);