package com.java.test.junior.controller;

import com.java.test.junior.BaseIntegrationTest;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.util.TestDataHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUpTest() {
        RestAssured.port = port;

        testDataHelper.cleanupAllTables();

        testDataHelper.createTestUser(2, "testuser");
        userAuth = createBasicAuthHeader("testuser", "123456");
    }

    @Test
    void testCreateProduct_Success() {
        ProductDTO productDTO = new ProductDTO("Test", 20.0, "Test");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userAuth)
                .body(productDTO)
                .when()
                .post(getApiUrl("/products"))
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("Product created successfully"))
                .body("data.name", equalTo(productDTO.getName()))
                .body("data.price", equalTo(productDTO.getPrice().floatValue()))
                .body("data.description", equalTo(productDTO.getDescription()));
    }

    @Test
    void testCreateProduct_ValidationError() {
        ProductDTO invalidProduct = new ProductDTO();
        invalidProduct.setName("");
        invalidProduct.setPrice(-10.0);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userAuth)
                .body(invalidProduct)
                .when()
                .post(getApiUrl("/products"))
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateProduct_Unauthorized() {
        ProductDTO productDTO = new ProductDTO("Test", 20.0, "Test");

        given()
                .contentType(ContentType.JSON)
                .body(productDTO)
                .when()
                .post(getApiUrl("/products"))
                .then()
                .statusCode(401);
    }

    @Test
    void testFindProductById_Success() {
        Product product = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = product.getId();

        given()
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products/" + productId))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product retrieved successfully"))
                .body("data.id", equalTo(productId.intValue()))
                .body("data.name", equalTo("Test"));
    }

    @Test
    void testFindProductById_NotFound() {
        given()
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products/99999"))
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateProduct_Success() {
        Product originalProduct = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = originalProduct.getId();

        ProductDTO updatedProduct = new ProductDTO("Updated Product", 199.99, "Updated Description");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userAuth)
                .queryParam("id", productId)
                .body(updatedProduct)
                .when()
                .put(getApiUrl("/products/" + productId))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product updated successfully"))
                .body("data.name", equalTo(updatedProduct.getName()))
                .body("data.price", equalTo(updatedProduct.getPrice().floatValue()));
    }

    @Test
    void testUpdateProduct_Forbidden() {
        Product originalProduct = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = originalProduct.getId();

        testDataHelper.createTestUser(3, "testuser2");
        String anotherUserAuth = createBasicAuthHeader("testuser2", "123456");

        ProductDTO updatedProduct = new ProductDTO("HackedTest", 30.0, "HackedTest");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", anotherUserAuth)
                .body(updatedProduct)
                .when()
                .put(getApiUrl("/products/" + productId))
                .then()
                .statusCode(403);
    }

    @Test
    void testDeleteProduct_Success() {
        Product product = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = product.getId();

        given()
                .header("Authorization", userAuth)
                .when()
                .delete(getApiUrl("/products/" + productId))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product deleted successfully"));

        given()
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products/" + productId))
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteProduct_Forbidden() {
        Product originalProduct = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = originalProduct.getId();

        testDataHelper.createTestUser(3, "testuser2");
        String anotherUserAuth = createBasicAuthHeader("testuser2", "123456");

        given()
                .header("Authorization", anotherUserAuth)
                .when()
                .delete(getApiUrl("/products/" + productId))
                .then()
                .statusCode(403);
    }

    @Test
    void testFindAll_Success() {
        testDataHelper.createTestProduct("Test1", 10.0, "Test1", "testuser");
        testDataHelper.createTestProduct("Test2", 20.0, "Test2", "testuser");
        testDataHelper.createTestProduct("Test3", 30.0, "Test3", "testuser");

        given()
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Products retrieved successfully"))
                .body("data", hasSize(greaterThanOrEqualTo(3)))
                .body("page", equalTo(1))
                .body("pageSize", equalTo(10));
    }

    @Test
    void testFindAll_Pagination() {
        given()
                .queryParam("page", 1)
                .queryParam("pageSize", 5)
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products"))
                .then()
                .statusCode(200)
                .body("page", equalTo(1))
                .body("pageSize", equalTo(5));
    }

    @Test
    void testFindAll_InvalidPagination() {
        given()
                .header("Authorization", userAuth)
                .queryParam("page", 0)
                .queryParam("pageSize", 5)
                .when()
                .get(getApiUrl("/products"))
                .then()
                .statusCode(400);
    }

    @Test
    void testFindByName_Success() {
        String uniqueName = "UniqueProduct_" + System.currentTimeMillis();
        testDataHelper.createTestProduct(uniqueName, 20.0, "Test", "testuser");

        given()
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products/name/" + uniqueName))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product retrieved successfully"))
                .body("data.name", equalTo(uniqueName));
    }

    @Test
    void testFindByName_NotFound() {
        given()
                .header("Authorization", userAuth)
                .when()
                .get(getApiUrl("/products/name/NonExistentProduct"))
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", containsString("Product not found"));
    }

    @Test
    void testLikeProduct_Success() {
        Product product = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = product.getId();

        given()
                .header("Authorization", userAuth)
                .when()
                .post(getApiUrl("/products/like/" + productId))
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void testDislikeProduct_Success() {
        Product product = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = product.getId();

        given()
                .header("Authorization", userAuth)
                .when()
                .post(getApiUrl("/products/like/" + productId))
                .then()
                .statusCode(200);

        given()
                .header("Authorization", userAuth)
                .when()
                .delete(getApiUrl("/products/dislike/" + productId))
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void testLikeProduct_Unauthorized() {
        Product product = testDataHelper.createTestProduct("Test", 20.0, "Test", "testuser");
        Long productId = product.getId();

        given()
                .when()
                .post(getApiUrl("/products/like/" + productId))
                .then()
                .statusCode(401);
    }
}