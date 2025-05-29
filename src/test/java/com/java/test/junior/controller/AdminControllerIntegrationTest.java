package com.java.test.junior.controller;

import com.java.test.junior.BaseIntegrationTest;
import com.java.test.junior.util.TestDataHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestDataHelper testDataHelper;

    private String validCsvPath;

    @BeforeEach
    void setUpTest(){
        RestAssured.port = port;
        testDataHelper.cleanupAllTables();
        testDataHelper.createAdminUser();
        adminAuth = createBasicAuthHeader("admin", "123456");

        Path resourcePath = Paths.get("C:\\GlobalDatabaseInternship\\java-junior\\src\\test\\java\\resources\\products.csv");
        validCsvPath = resourcePath.toString();

    }

    @Test
    void testLoadProducts_ValidLocalFile_Success() {
        given()
                .header("Authorization", adminAuth)
                .queryParam("path", validCsvPath)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("CSV file copied successfully"));
    }

    @Test
    void testLoadProducts_ValidUrl_Success() {
        given()
                .header("Authorization", adminAuth)
                .queryParam("path", validCsvPath)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("CSV file copied successfully"));
    }

    @Test
    void testLoadProducts_InvalidFilePath_Failure() {
        String invalidCsvPath = "/invalid/nonexistent.csv";

        given()
                .header("Authorization", adminAuth)
                .queryParam("path", invalidCsvPath)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(500)
                .body("success", equalTo(false))
                .body("message", equalTo("Failed to read the CSV file"));
    }

    @Test
    void testLoadProducts_Unauthorized() {
        given()
                .queryParam("path", validCsvPath)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(401);
    }

    @Test
    void testLoadProducts_Forbidden() {
        testDataHelper.createTestUser(2, "testuser");
        String userAuth = createBasicAuthHeader("testuser", "123456");

        given()
                .header("Authorization", userAuth)
                .queryParam("path", validCsvPath)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(403);
    }

    @Test
    void testLoadProducts_MissingPathParameter() {
        given()
                .header("Authorization", adminAuth)
                .when()
                .post(getApiUrl("/admin/loading/products"))
                .then()
                .statusCode(400);
    }
}