package com.microservices.projectservice.controller;

import com.microservices.projectservice.ProjectServiceApplicationTests;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

class ProjectControllerTests extends ProjectServiceApplicationTests {

    private final String ownerId = "ff394849-1f55-4b8b-bf56-956c43cfff56";

    @Test
    void getProject_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("projectId", 1)
                .when()
                .get("/project/{projectId}")
                .then()
                .statusCode(404);
    }

    @Test
    void getProject_shouldReturnOk() {
        String requestBody = """
                {
                    "name": 1,
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(requestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .when()
                .get("/project/{projectId}")
                .then()
                .statusCode(200);
    }

    @Test
    void getAllJoinProjectsWithUnavailableUserId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("userId", "not-found")
                .queryParam("isOwner", false)
                .when()
                .get("/project/{userId}/user")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllJoinProjectsWithUserId_shouldReturnOk() {
        List<RequestSpecification> requests = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            requests.add(given(requestSpecification)
                    .body("""
                            {
                                "name": "Project %d",
                                "description": "Testing project %d",
                                "startDate": "2024-07-23",
                                "endDate": "2024-07-24",
                                "ownerId": "%s"
                            }""".formatted(i, i, ownerId))
            );
        }
        var projectIds = requests.stream().map(request -> request
                .when()
                .post("/project")
                .thenReturn()
                .print()
        ).toList();

        var memberId = "mb394849-1f55-4b8b-bf56-956c43cfff56";
        projectIds.forEach(projectId -> given(requestSpecification)
                .pathParam("projectId", projectId)
                .body("""
                        {
                            "memberId": "%s",
                            "operator": "ADD"
                        }""".formatted(memberId))
                .when()
                .patch("/project/{projectId}/member")
        );

        var getRequest = given(requestSpecification)
                .pathParam("userId", memberId)
                .queryParam("isOwner", false)
                .when()
                .get("/project/{userId}/user");
        getRequest.prettyPrint();
        getRequest.then().statusCode(200);
    }

    @Test
    void getAllOwnProjectsWithUnavailableUserId_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("userId", "not-found")
                .when()
                .get("/project/{userId}/user")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllOwnProjectsWithUserId_shouldReturnOk() {
        List<RequestSpecification> requests = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            requests.add(given(requestSpecification)
                    .body("""
                            {
                                "name": "Project %d",
                                "description": "Testing project %d",
                                "startDate": "2024-07-23",
                                "endDate": "2024-07-24",
                                "ownerId": "%s"
                            }""".formatted(i, i, ownerId))
            );
        }
        requests.forEach(request -> request
                .when()
                .post("/project")
                .thenReturn()
                .print()
        );

        var getRequest = given(requestSpecification)
                .pathParam("userId", ownerId)
                .when()
                .get("/project/{userId}/user");
        getRequest.prettyPrint();
        getRequest.then().statusCode(200);
    }

    @Test
    void createProjectWithBlankOwnerId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test",
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "          "
                }""";
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(400);
    }

    @Test
    void createProjectWithBlankProjectName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "   ",
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);

        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(400);
    }

    @Test
    void createProjectWithNullOwnerId_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test",
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24"
                }""";
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(400);
    }

    @Test
    void createProjectWithNullProjectName_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(400);
    }

    @Test
    void createProjectWithStartDateGreaterEndDate_shouldReturnBadRequest() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "startDate": "2024-07-24",
                    "endDate": "2024-07-23",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(400);
    }

    @Test
    void createProjectWithNullStartDate_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(201);
    }

    @Test
    void createProjectWithNullEndDate_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "startDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(201);
    }

    @Test
    void createProjectWithBlankDesc_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(201);
    }

    @Test
    void createProject_shouldReturnCreated() {
        String requestBody = """
                {
                    "name": "Test project",
                    "description": "Some description",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        given(requestSpecification)
                .body(requestBody)
                .when()
                .post("/project")
                .then()
                .statusCode(201);
    }

    @Test
    void updateProject_shouldReturnNotFound() {
        String updateRequestBody = """
                {
                    "name": "Test project"
                }""";
        given(requestSpecification)
                .pathParam("projectId", "not-found-okay")
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(404);
    }

    @Test
    void updateProjectWithBlankProjectName_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "name": "      "
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateProjectWithNullProjectName_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "name": "Edited test project"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(204);
    }

    @Test
    void updateProjectWithStartDateGreaterEndDate_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-25"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateProjectWithStartDateGreaterProjectEndDate_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "startDate": "2024-07-25"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateProjectWithProjectStartDateGreaterEndDate_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "endDate": "2024-07-25"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void updateProject_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "endDate": "2024-07-25"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void addProjectMemberWithNullMemberId_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "operator": "ADD"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}/member")
                .then()
                .statusCode(400);
    }

    @Test
    void addProjectMemberWithBlankMemberId_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "memberId": "      ",
                    "operator": "ADD"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}/member")
                .then()
                .statusCode(400);
    }

    @Test
    void addProjectMemberWithWrongOperator_shouldReturnBadRequest() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "memberId": "7c48e304-5872-44b0-a6fb-aab31f045903",
                    "operator": "DELETE"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}/member")
                .then()
                .statusCode(400);
    }

    @Test
    void addProjectMember_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "memberId": "7c48e304-5872-44b0-a6fb-aab31f045903",
                    "operator": "ADD"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}/member")
                .then()
                .statusCode(204);
    }

    @Test
    void removeProjectMember_shouldReturnNoContent() {
        String createRequestBody = """
                {
                    "name": "Test project",
                    "startDate": "2024-07-26",
                    "endDate": "2024-07-28",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(createRequestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();

        String updateRequestBody = """
                {
                    "memberId": "7c48e304-5872-44b0-a6fb-aab31f045903",
                    "operator": "REMOVE"
                }""";
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .body(updateRequestBody)
                .when()
                .patch("/project/{projectId}/member")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteProject_shouldReturnNotFound() {
        given(requestSpecification)
                .pathParam("projectId", "5e6ebea5-c22d-4520-b954-6689ea70cf93")
                .when()
                .delete("/project/{projectId}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteProject_shouldReturnNoContent() {
        String requestBody = """
                {
                    "name": 1,
                    "description": 2,
                    "startDate": "2024-07-23",
                    "endDate": "2024-07-24",
                    "ownerId": "%s"
                }""".formatted(ownerId);
        var projectId = given(requestSpecification)
                .body(requestBody)
                .post("/project")
                .thenReturn()
                .body()
                .print();
        given(requestSpecification)
                .pathParam("projectId", projectId)
                .when()
                .delete("/project/{projectId}")
                .then()
                .statusCode(204);
    }
}
