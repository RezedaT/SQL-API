package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class
APIHelper {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void makeQueryToLogin(DataHelper.AuthInfo authInfo, Integer statusCode) {
        given()
                .spec(requestSpec)
                .body(authInfo)
                .when()
                .post("/api/auth")
                .then().log().all()
                .statusCode(statusCode);
    }

    public static APITokenInfo sendQueryToVerify(DataHelper.VerificationInfo verificationInfo, Integer statusCode) {
        return given()
                .spec(requestSpec)
                .body(verificationInfo)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .as(APITokenInfo.class);
    }

    public static Map<String, Integer> sendQueryToGetCardsBalances(String token, Integer statusCode) {
        APICardInfo[] cardsInfo = given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/cards")
                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .body()
                .as(APICardInfo[].class);
        Map<String, Integer> cardBalance = new HashMap<>();
        for (APICardInfo cardInfo : cardsInfo) {
            cardBalance.put(cardInfo.getId(), cardInfo.getBalance());
        }
        return cardBalance;
    }

    public static void generateQueryToTransfer(String token, APITransferInfo transferInfo, Integer statusCode) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transferInfo)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(statusCode);
    }
    @Value
    public static class APITokenInfo {
        String token;
    }

    @Value
    public static class APICardInfo {
        String id;
        String number;
        Integer balance;
    }

    @Value
    public static class APITransferInfo {
        String from;
        String to;
        int amount;
    }
}