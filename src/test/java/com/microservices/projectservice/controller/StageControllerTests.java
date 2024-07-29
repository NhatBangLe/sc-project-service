package com.microservices.projectservice.controller;

import com.microservices.projectservice.ProjectServiceApplicationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class StageControllerTests extends ProjectServiceApplicationTests {

    private String projectId;
    private String formId;

    @BeforeEach
    void initProjectAndForm() {
        String projectRequestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "ff394849-1f55-4b8b-bf56-956c43cfff56"
                }""";
        this.projectId = given(requestSpecification)
                .body(projectRequestBody)
                .when()
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String formRequestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        this.formId = given(requestSpecification)
                .body(formRequestBody)
                .when()
                .post("/form")
                .thenReturn()
                .print();
    }

    @Test
    void getStage_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("stageId", 1)
                .when()
                .get("/stage/{stageId}")
                .then()
                .statusCode(404);
    }

    @Test
    void getStage_shouldReturnOk() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .thenReturn()
                .body()
                .print();
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .when()
                .get("/stage/{stageId}")
                .then()
                .statusCode(200);
    }

    @Test
    void createStageWithNullStageName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithBlankStageName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "        ",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithProjectOwnerIdNotAvailable_shouldReturnNotFound() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, "not-available");
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(404);
    }

    @Test
    void createStageWithNullProjectOwnerId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s"
                }""".formatted(formId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithStartDateGreaterEndDate_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "description": "Some description",
                    "startDate": "2024-07-24",
                    "endDate": "2024-07-23",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithNullFormId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithBlankFormId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "      ",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(400);
    }

    @Test
    void createStageWithNullDesc_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Test stage",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(201);
    }

    @Test
    void createStage_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .then()
                .statusCode(201);
    }

    @Test
    void updateStageWithBlankFormName_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(createRequestBody)
                .post("/stage")
                .thenReturn()
                .print();

        String updateRequestBody = """
                {
                    "name": "        "
                }""";
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .body(updateRequestBody)
                .patch("/stage/{stageId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateStageWithNullFormName_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(createRequestBody)
                .post("/stage")
                .thenReturn()
                .print();

        String updateRequestBody = """
                {
                    "description": "Edited some description"
                }""";
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .body(updateRequestBody)
                .patch("/stage/{stageId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateStageWithStartDateGreaterEndDate_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(createRequestBody)
                .post("/stage")
                .thenReturn()
                .print();

        String updateRequestBody = """
                {
                    "startDate": "2024-07-24",
                    "endDate": "2024-07-23"
                }""";
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .body(updateRequestBody)
                .patch("/stage/{stageId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateStage_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(createRequestBody)
                .post("/stage")
                .thenReturn()
                .print();

        String updateRequestBody = """
                {
                    "name": "Edited stage 1",
                    "description": "Edited some description",
                    "startDate": "2024-07-25",
                    "endDate": "2024-07-28"
                }""";
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .body(updateRequestBody)
                .patch("/stage/{stageId}")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteStage_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("stageId", "ff394849-1f55-4b8b-bf56-956c43cfff56")
                .delete("/stage/{stageId}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteStage_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(requestBody)
                .post("/stage")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("stageId", stageId)
                .delete("/stage/{stageId}")
                .then()
                .statusCode(204);
    }
}