package com.microservices.projectservice.controller;

import com.microservices.projectservice.ProjectServiceApplicationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class FormControllerTests extends ProjectServiceApplicationTests {

    private String projectId;

    @BeforeEach
    void initProject() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "ff394849-1f55-4b8b-bf56-956c43cfff56"
                }""";
        this.projectId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .thenReturn()
                .body()
                .print();
    }

    @Test
    void getForm_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("formId", 1)
                .when()
                .get("/form/{formId}")
                .then()
                .statusCode(404);
    }

    @Test
    void getForm_shouldReturnOk() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        var formId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("formId", formId)
                .when()
                .get("/form/{formId}")
                .then()
                .statusCode(200);
    }

    @Test
    void createFormWithNullDesc_shouldReturnCreated() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(201);
    }

    @Test
    void createFormWithBlankProjectOwnerId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "     "
                }""";
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(400);
    }

    @Test
    void createFormWithNullProjectOwnerId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description"
                }""";
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(400);
    }

    @Test
    void createFormWithBlankFormTitle_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "title": "       ",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(400);
    }

    @Test
    void createFormWithNullFormTitle_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(400);
    }

    @Test
    void createForm_shouldReturnCreated() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .then()
                .statusCode(201);
    }

    @Test
    void updateFormWithBlankTitle_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        var formId = given(requestSpecification)
                .body(createRequestBody)
                .when()
                .post("/form")
                .thenReturn()
                .print();

        var updateRequestBody = """
                {
                    "title": "    ",
                    "description": "Edited description"
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(updateRequestBody)
                .when()
                .patch("/form/{formId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateFormWithNullTitle_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        var formId = given(requestSpecification)
                .body(createRequestBody)
                .when()
                .post("/form")
                .thenReturn()
                .print();

        var updateRequestBody = """
                {
                    "description": "Edited description"
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(updateRequestBody)
                .when()
                .patch("/form/{formId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateFormWithNullDesc_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        var formId = given(requestSpecification)
                .body(createRequestBody)
                .when()
                .post("/form")
                .thenReturn()
                .print();

        var updateRequestBody = """
                {
                    "title": "Edited Form"
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(updateRequestBody)
                .when()
                .patch("/form/{formId}")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteForm_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("formId", 1)
                .when()
                .delete("/form/{formId}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteForm_shouldReturnNoContent() {
        String requestBody = """
                {
                    "title": "Form 1",
                    "description": "Some description",
                    "projectOwnerId": "%s"
                }""".formatted(projectId);
        var formId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/form")
                .thenReturn()
                .body()
                .print();
        given(requestSpecification)
                .pathParam("formId", formId)
                .when()
                .delete("/form/{formId}")
                .then()
                .statusCode(204);
    }

}