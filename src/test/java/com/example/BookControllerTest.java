package com.example;

import com.example.model.Book;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class BookControllerTest extends APITestCase {

     private static final String BOOK_RESOURCE_URL = API_ROOT + "/api/books";


    @Test
    public void verifyAPIStartsWithEmptyStore() {
        given()

        .when()
                .get(BOOK_RESOURCE_URL)

        .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("", Matchers.hasSize(0));

    }

    @Test
    public void verifyTitleFieldIsRequired() {
        Book requestBook = createRequestBook("John Smith", null);

        given()
                .request()
                .body(requestBook)
                .contentType(JSON)

                .when()
                .put(BOOK_RESOURCE_URL)


        .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400)
                .body("error", Matchers.equalTo("Field 'title' is required."));

    }

    @Test
    public void verifyAuthorFieldIsRequired() {
        Book requestBook = createRequestBook(null, "Reliability of late night deployments");

        given()
                .request()
                .body(requestBook)
                .contentType(JSON)

                .when()
                .put(BOOK_RESOURCE_URL)


                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400)
                .body("error", Matchers.equalTo("Field 'author' is required."));

    }

    @Test
    public void verifyAuthorFieldCannotBeEmpty()  {
        Book requestBook = createRequestBook("", "Reliability of late night deployments");

         given()
                .request()
                .body(requestBook)
                .contentType(JSON)

         .when()
                .put(BOOK_RESOURCE_URL)


                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400)
                .body("error", Matchers.equalTo("Field 'author' cannot be empty."));

    }

    @Test
    public void verifyTitleFieldCannotBeEmpty()  {
        Book requestBook = createRequestBook("John Smith", "");

        given()
                .request()
                .body(requestBook)
                .contentType(JSON)

          .when()
                .put(BOOK_RESOURCE_URL)


         .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400)
                .body("error", Matchers.equalTo("Field 'title' cannot be empty."));

    }

    @Test
    public void verifyIdFieldIsReadOnly()  {
        Book requestBook = createRequestBook("John Smith", "Reliability of late night deployments", 1L);

         given()
                .request()
                .body(requestBook)
                .contentType(JSON)

         .when()
                .put(BOOK_RESOURCE_URL)


         .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400);

    }

    @Test
    public void verifyCreatingNewBook()  {
        Book requestBook = createRequestBook("John Smith", "Reliability of late night deployments");

        Integer bookId = given()
                .request()
                .body(requestBook)
                .contentType(JSON)

         .when()
                .put(BOOK_RESOURCE_URL)

         .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("author",Matchers.equalTo(requestBook.getAuthor()))
                .body("title",Matchers.equalTo(requestBook.getTitle()))
                .body("id",Matchers.notNullValue())
          .extract()
                .path("id");

           given()
                .request()
                .body(requestBook)
                .contentType(JSON)

           .when()
                .get(BOOK_RESOURCE_URL+"/"+bookId)


           .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("author",Matchers.equalTo(requestBook.getAuthor()))
                .body("title",Matchers.equalTo(requestBook.getTitle()))
                .body("id",Matchers.equalTo(bookId));


    }

    @Test
    public void verifyCannotCreateDuplicatedBooks()  {
        Book requestBook = createRequestBook("Jane Archer", "DevOps is a lie");

        given()
                .request()
                .body(requestBook)
                .contentType(JSON)

        .when()
                .put(BOOK_RESOURCE_URL)


        .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200);

        given()
                .request()
                .body(requestBook)
                .contentType(JSON)

                .when()
                .put(BOOK_RESOURCE_URL)


                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(400)
                .body("error", Matchers.equalTo("Another book with similar title and author already exists."));

    }

    private Book createRequestBook(String author, String title) {
        return createRequestBook(author, title, null);
    }

    private Book createRequestBook(String author, String title, Long id) {
        Book requestBook = new Book();
        requestBook.setId(id);
        requestBook.setAuthor(author);
        requestBook.setTitle(title);
        return requestBook;
    }

}
