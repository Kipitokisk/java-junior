package com.java.test.junior.controller;

import com.java.test.junior.BaseIntegrationTest;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.util.TestDataHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUpTest() {
        RestAssured.port = port;
        testDataHelper.cleanupAllTables();
    }

    @Test
    void testRegister_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpass123");
        userDTO.setEmail("testuser1@gmail.com");

        given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post(getApiUrl("/auth/register"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User registered successfully"))
                .body("data.username", equalTo("testuser"));
    }

    @Test
    void testRegister_DuplicateUsername() {
        testDataHelper.createTestUser(2, "testuser");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("differentpass123");
        userDTO.setEmail("testuser2@gmail.com");

        given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post(getApiUrl("/auth/register"))
                .then()
                .statusCode(409)
                .body("success", equalTo(false))
                .body("message", containsString("Username 'testuser' is already taken"));
    }

    @Test
    void testRegister_ValidationError_EmptyUsername() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("");
        userDTO.setPassword("testpass123");

        given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post(getApiUrl("/auth/register"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Validation failed"));
    }

    @Test
    void testRegister_ValidationError_NullPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword(null);

        given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post(getApiUrl("/auth/register"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Validation failed"));
    }

    @Test
    void testRegister_ValidationError_ShortPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("short");

        given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post(getApiUrl("/auth/register"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Validation failed"));
    }
}