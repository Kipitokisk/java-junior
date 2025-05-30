package com.java.test.junior.controller;

import com.java.test.junior.BaseIntegrationTest;
import com.java.test.junior.util.TestDataHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordResetControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestDataHelper testDataHelper;
    String validResetToken;

    @BeforeEach
    void setUpTest() {
        RestAssured.port = port;
        testDataHelper.cleanupAllTables();
        testDataHelper.createTestUser(2, "testuser1");
        userAuth = createBasicAuthHeader("testuser1", "123456");
        validResetToken = testDataHelper.createPasswordResetToken("testuser1@gmail.com");
    }

    @Test
    void testForgotPassword_InvalidEmail_NotFound() {
        given()
                .queryParam("email", "nonexistent@example.com")
                .when()
                .post(getApiUrl("/forgot-password"))
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", containsString("User not found"));
    }

    @Test
    void testResetPassword_ValidToken_Success() {
        String newPassword = "newStrongPass!";

        given()
                .queryParam("token", validResetToken)
                .queryParam("newPassword", newPassword)
                .when()
                .post(getApiUrl("/reset-password"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Password reset successfully"));
    }

    @Test
    void testResetPassword_InvalidToken_NotFound() {
        given()
                .queryParam("token", "invalid-token-123")
                .queryParam("newPassword", "anyPassword")
                .when()
                .post(getApiUrl("/reset-password"))
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", containsString("No token found"));
    }

    @Test
    void testResetPassword_ExpiredToken_Failure() {
        // Use helper to create expired token
        String expiredToken = testDataHelper.createExpiredPasswordResetToken("testuser1@gmail.com");

        given()
                .queryParam("token", expiredToken)
                .queryParam("newPassword", "anyPassword")
                .when()
                .post(getApiUrl("/reset-password"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Token has expired"));
    }

    @Test
    void testResetPassword_MissingParameters_Failure() {
        given()
                .when()
                .post(getApiUrl("/reset-password"))
                .then()
                .statusCode(400);
    }

    @Test
    void testForgotPassword_MissingEmailParameter_Failure() {
        given()
                .when()
                .post(getApiUrl("/forgot-password"))
                .then()
                .statusCode(400);
    }


}
