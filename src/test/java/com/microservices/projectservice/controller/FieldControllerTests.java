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
    void getField_shouldReturnOk() {
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
    void createField_shouldReturnCreated() {
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
                .post("/field/{fieldId}")
                .thenReturn()
                .print();
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
                .post("/field/{fieldId}")
                .thenReturn()
                .print();
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
                .post("/field/{fieldId}")
                .thenReturn()
                .print();
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
                .post("/field/{fieldId}")
                .thenReturn()
                .print();
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

}