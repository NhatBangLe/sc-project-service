package com.microservices.projectservice.controller;

import com.microservices.projectservice.ProjectServiceApplicationTests;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

class FieldControllerTests extends ProjectServiceApplicationTests {

    private String formId;
    private String sampleId;

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
        var projectId = given(requestSpecification)
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
        String stageRequestBody = """
                {
                    "name": "Stage 1",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "formId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(formId, projectId);
        var stageId = given(requestSpecification)
                .body(stageRequestBody)
                .post("/stage")
                .thenReturn()
                .print();
        String sampleRequestBody = """
                {
                    "stageId": "%s",
                    "projectOwnerId": "%s"
                }""".formatted(stageId, projectId);
        this.sampleId = given(requestSpecification)
                .body(sampleRequestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
    }

    @Test
    void getFieldWithUnavailableFieldId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("fieldId", "not-found")
                .when()
                .get("/field/{fieldId}")
                .then()
                .statusCode(404);
    }

    @Test
    void getFieldWithAvailableFieldId_shouldReturnOk() {
        String requestBody = """
                {
                    "fieldName": "Test field",
                    "numberOrder": 0
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .when()
                .get("/field/{fieldId}")
                .then()
                .statusCode(200);
    }

    @Test
    void getAllFieldsWithUnavailableFormId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("formId", "not-found")
                .when()
                .get("/field/{formId}/form")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllFieldsWithAvailableFormId_shouldReturnOk() {
        List<RequestSpecification> requests = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            requests.add(given(requestSpecification)
                    .pathParam("formId", formId)
                    .body("""
                            {
                                "fieldName": "Test field %d",
                                "numberOrder": %d
                            }""".formatted(i, i)
                    )
            );
        }
        requests.forEach(request -> request
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print()
        );

        var getRequest = given(requestSpecification)
                .pathParam("formId", formId)
                .when()
                .get("/field/{formId}/form");
        getRequest.prettyPrint();
        getRequest.then().statusCode(200);
    }

    @Test
    void createFieldWithNullFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "fieldName": null,
                    "numberOrder": 0
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(400);
    }

    @Test
    void createFieldWithEmptyFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "fieldName": "",
                    "numberOrder": 0
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(400);
    }

    @Test
    void createFieldWithBlankFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "fieldName": "         ",
                    "numberOrder": 0
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(400);
    }

    @Test
    void createFieldWithUnavailableFormId_shouldReturnNotFound() {
        String requestBody = """
                {
                    "fieldName": "Field 1",
                    "numberOrder": 0
                }""";
        given(requestSpecification)
                .pathParam("formId", "not-found")
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(404);
    }

    @Test
    void createFieldWithNullNumberOrder_shouldReturnCreated() {
        String requestBody = """
                {
                    "fieldName": "Field 1"
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(201);
    }

    @Test
    void createFieldWithValidData_shouldReturnCreated() {
        String requestBody = """
                {
                    "fieldName": "Field 1",
                    "numberOrder": 0
                }""";
        given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .then()
                .statusCode(201);
    }

    @Test
    void updateFieldWithBlankFieldName_shouldReturnBadRequest() {
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body("""
                        {
                            "fieldName": "Field",
                            "numberOrder": 0
                        }""")
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "fieldName": "         "
                        }""")
                .when()
                .patch("/field/{fieldId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateFieldWithEmptyFieldName_shouldReturnBadRequest() {
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body("""
                        {
                            "fieldName": "Field",
                            "numberOrder": 0
                        }""")
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "fieldName": ""
                        }""")
                .when()
                .patch("/field/{fieldId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateFieldWithNewFieldName_shouldReturnNoContent() {
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body("""
                        {
                            "fieldName": "Field",
                            "numberOrder": 0
                        }""")
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "fieldName": "Field Updated"
                        }""")
                .when()
                .patch("/field/{fieldId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateFieldWithNewNumberOrder_shouldReturnNoContent() {
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body("""
                        {
                            "fieldName": "Field",
                            "numberOrder": 0
                        }""")
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "numberOrder": 1
                        }""")
                .when()
                .patch("/field/{fieldId}")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteFieldWithUnavailableFieldId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("fieldId", "not-found")
                .when()
                .delete("/field/{fieldId}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteField_shouldReturnNoContent() {
        String requestBody = """
                {
                    "fieldName": "Field 1",
                    "numberOrder": 0
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("formId", formId)
                .body(requestBody)
                .when()
                .post("/field/{formId}")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .when()
                .delete("/field/{fieldId}")
                .then()
                .statusCode(204);
    }

    @Test
    void createDynamicFieldWithNullFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": null,
                    "value": ""
                }""";
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(400);
    }

    @Test
    void createDynamicFieldWithEmptyFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "",
                    "value": ""
                }""";
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(400);
    }

    @Test
    void createDynamicFieldWithBlankFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "          ",
                    "value": ""
                }""";
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(400);
    }

    @Test
    void createDynamicFieldWithUnavailableSampleId_shouldReturnNotFound() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": ""
                }""";
        given(requestSpecification)
                .pathParam("sampleId", "not-found")
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(404);
    }

    @Test
    void createDynamicFieldWithNullNumberOrder_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": ""
                }""";
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(201);
    }

    @Test
    void createDynamicFieldWithValidData_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .then()
                .statusCode(201);
    }

    @Test
    void updateDynamicFieldWithBlankFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "name": "         "
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(400);
    }

    @Test
    void updateDynamicFieldWithEmptyFieldName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "name": ""
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(400);
    }

    @Test
    void updateDynamicFieldWithNewFieldName_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "name": "Dynamic field updated"
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

    @Test
    void updateDynamicFieldWithBlankFieldValue_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "value": "         "
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

    @Test
    void updateDynamicFieldWithEmptyFieldValue_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "value": ""
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

    @Test
    void updateDynamicFieldWithNewFieldValue_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "value": "Dynamic field value updated"
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

    @Test
    void updateDynamicFieldWithNewNumberOrder_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .body("""
                        {
                            "numberOrder": 2
                        }""")
                .when()
                .patch("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteDynamicFieldWithUnavailableFieldId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("fieldId", "not-found")
                .when()
                .delete("/field/{fieldId}/dynamic")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteDynamicField_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": "Dynamic field 1",
                    "value": "Field value",
                    "numberOrder": 1
                }""";
        var fieldId = given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body(requestBody)
                .when()
                .post("/field/{sampleId}/dynamic")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("fieldId", fieldId)
                .when()
                .delete("/field/{fieldId}/dynamic")
                .then()
                .statusCode(204);
    }

}