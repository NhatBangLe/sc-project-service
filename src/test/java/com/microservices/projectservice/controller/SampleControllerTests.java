package com.microservices.projectservice.controller;

import com.microservices.projectservice.ProjectServiceApplicationTests;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

class SampleControllerTests extends ProjectServiceApplicationTests {

    private String projectId;
    private String stageId;
    private final String attachmentId = "f1c23a7a-9205-47fc-9e22-dbbe7d30874e";
    private List<String> fieldIds; // min size 3

    @BeforeEach
    void initialize() {
        // init project
        this.projectId = given(requestSpecification)
                .body("""
                        {
                            "name": "Test project",
                            "description": "Some description",
                            "startDate": "2024-07-23",
                            "endDate": "2024-07-24",
                            "ownerId": "%s"
                        }""".formatted("ff394849-1f55-4b8b-bf56-956c43cfff56"))
                .when()
                .post("/project")
                .thenReturn()
                .print();
        // init form
        String formId = given(requestSpecification)
                .body("""
                        {
                            "title": "Form 1",
                            "description": "Some description",
                            "projectOwnerId": "%s"
                        }""".formatted(projectId))
                .when()
                .post("/form")
                .thenReturn()
                .print();
        // init stage
        this.stageId = given(requestSpecification)
                .body("""
                        {
                            "name": "Stage 1",
                            "description": "Some description",
                            "startDate": "2024-07-23",
                            "endDate": "2024-07-24",
                            "formId": "%s",
                            "projectOwnerId": "%s"
                        }""".formatted(formId, projectId))
                .post("/stage")
                .thenReturn()
                .print();
        // init fields
        List<RequestSpecification> requests = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
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
        fieldIds = requests.stream()
                .map(request -> request
                        .when()
                        .post("/field/{formId}")
                        .thenReturn()
                        .print()
                )
                .toList();
    }

    @Test
    void getAllSamplesWithUnavailableProjectId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("projectId", "not-found")
                .when()
                .get("/sample/{projectId}/project")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllSamplesWithAvailableProjectId_shouldReturnOK() {
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        for (int i = 1; i <= 3; i++) {
            given(requestSpecification)
                    .body(requestBody)
                    .when()
                    .post("/sample");
        }
        var response = given(requestSpecification)
                .pathParam("projectId", projectId)
                .when()
                .get("/sample/{projectId}/project");
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test
    void getAllSamplesWithUnavailableStageId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("stageId", "not-found")
                .when()
                .get("/sample/{stageId}/stage")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllSamplesWithAvailableStageId_shouldReturnOK() {
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        for (int i = 1; i <= 3; i++) {
            given(requestSpecification)
                    .body(requestBody)
                    .when()
                    .post("/sample");
        }
        var response = given(requestSpecification)
                .pathParam("stageId", stageId)
                .when()
                .get("/sample/{stageId}/stage");
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test
    void getSampleWithUnavailableSampleId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("sampleId", "not-found")
                .when()
                .get("/sample/{sampleId}")
                .then()
                .statusCode(404);
    }

    @Test
    void getSample_shouldReturnOK() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .when()
                .get("/sample/{sampleId}")
                .then()
                .statusCode(200);
    }

    @Test
    void createSampleWithNullAttachmentId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(projectId, stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithBlankAttachmentId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "  ",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(projectId, stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithNullProjectOwnerId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "stageId": "%s"
                }""".formatted(attachmentId, stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithBlankProjectOwnerId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, "           ", stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithUnavailableProjectOwnerId_shouldReturnNotFound() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, "not-found", stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(404);
    }

    @Test
    void createSampleWithNullStageId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s"
                }""".formatted(attachmentId, projectId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithBlankStageId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, projectId, "            ");
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(400);
    }

    @Test
    void createSampleWithUnavailableStageId_shouldReturnNotFound() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, projectId, "not-found");
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(404);
    }

    @Test
    void createSampleWithNullAnswers_shouldReturnCreated() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(201);
    }

    @Test
    void createSampleWithNullDynamicFields_shouldReturnCreated() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(201);
    }

    @Test
    void createSampleWithNullPosition_shouldReturnCreated() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(201);
    }

    @Test
    void createSampleWithBlankPosition_shouldReturnCreated() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "         ",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(201);
    }

    @Test
    void createSample_shouldReturnCreated() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .then()
                .statusCode(201);
    }

    @Test
    void updateSampleWithUnavailableSampleId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("sampleId", "not-found")
                .body("""
                        {
                          "position": "new longitude"
                        }""")
                .when()
                .patch("/sample/{sampleId}")
                .then()
                .statusCode(404);
    }

    @Test
    void updateSampleWithAvailableSampleId_shouldReturnNoContent() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, projectId, stageId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "position": "new longitude"
                        }""")
                .when()
                .patch("/sample/{sampleId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateSampleWithBlankPosition_shouldReturnNoContent() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, projectId, stageId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "position": "          "
                        }""")
                .when()
                .patch("/sample/{sampleId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateSampleWithBlankAttachmentId_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s"
                }""".formatted(attachmentId, projectId, stageId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "attachmentId": "          "
                        }""")
                .when()
                .patch("/sample/{sampleId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateAnswerWithNullValue_shouldReturnBadRequest() {
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(projectId, stageId, fieldIds.getFirst());
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "fieldId": "%s"
                        }""".formatted(fieldIds.getFirst()))
                .when()
                .patch("/sample/{sampleId}/answer")
                .then()
                .statusCode(400);
    }

    @Test
    void updateAnswerWithBlankValue_shouldReturnNoContent() {
        var fieldId = fieldIds.getFirst();
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "value": "        ",
                          "fieldId": "%s"
                        }""".formatted(fieldId))
                .when()
                .patch("/sample/{sampleId}/answer")
                .then()
                .statusCode(204);
    }

    @Test
    void updateAnswerWithNullFieldId_shouldReturnBadRequest() {
        var fieldId = fieldIds.getFirst();
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(projectId, stageId, fieldId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "value": "Updated value"
                        }""")
                .when()
                .patch("/sample/{sampleId}/answer")
                .then()
                .statusCode(400);
    }

    @Test
    void updateAnswerWithBlankFieldId_shouldReturnBadRequest() {
        var fieldId = fieldIds.getFirst();
        var requestBody = """
                {
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(projectId, stageId, fieldId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "value": "Updated value",
                          "fieldId": "        "
                        }""")
                .when()
                .patch("/sample/{sampleId}/answer")
                .then()
                .statusCode(400);
    }

    @Test
    void updateAnswer_shouldReturnNoContent() {
        var fieldId = fieldIds.getFirst();
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldId);
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .body("""
                        {
                          "value": "Updated value",
                          "fieldId": "%s"
                        }""".formatted(fieldId))
                .when()
                .patch("/sample/{sampleId}/answer")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteSampleWithUnavailableSampleId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("sampleId", "not-found")
                .when()
                .delete("/sample/{sampleId}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteSampleWithAvailableSampleId_shouldReturnNoContent() {
        var requestBody = """
                {
                  "attachmentId": "%s",
                  "position": "longitude",
                  "projectOwnerId": "%s",
                  "stageId": "%s",
                  "answers": [
                    {
                      "value": "Answer 1",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 2",
                      "fieldId": "%s"
                    },
                    {
                      "value": "Answer 3",
                      "fieldId": "%s"
                    }
                  ],
                  "dynamicFields": [
                    {
                      "name": "Field 1",
                      "value": "Answer field 1",
                      "numberOrder": 0
                    },
                    {
                      "name": "Field 2",
                      "value": "Answer field 2",
                      "numberOrder": 1
                    }
                  ]
                }""".formatted(attachmentId, projectId, stageId, fieldIds.get(0), fieldIds.get(1), fieldIds.get(2));
        var sampleId = given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/sample")
                .thenReturn()
                .print();
        given(requestSpecification)
                .pathParam("sampleId", sampleId)
                .when()
                .delete("/sample/{sampleId}")
                .then()
                .statusCode(204);
    }

}