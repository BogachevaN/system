package ru.cft.coi.systemtests.tests.remote;

import com.jayway.restassured.response.Response;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.testng.annotations.Test;
import ru.cft.coi.systemtests.api.CoiEndpoints;
import ru.cft.coi.systemtests.api.RestApiCoordinator;
import ru.cft.coi.systemtests.api.RestApiFincert;
import ru.cft.coi.systemtests.tests.TestBase;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.RestAssured.given;
import static org.testng.Assert.fail;

@Slf4j
public class SynchronizationTests extends TestBase {

    @Test
    public void testWhenRequestCreatedInFincertThenIncidentCreatedInCoordinator() throws Exception {
        log.info("Получение токена доступа к API ФинЦЕРТ");
        val fincertToken = FINCERT_AUTH.getToken();

        log.info("Создание запроса в API ФинЦЕРТ");
        val createRequestRequestBody = getCreateIncidentRequestBody();
        var response = RestApiFincert.createRequest(fincertToken, createRequestRequestBody);
        val apiRequestId = response.jsonPath().getString("id");

        String fincertRequestId = null;

        for (int i = 0; i < 60; ++i) { // не более 5 минут
            log.info("Ожидание создания запроса в ФинЦЕРТ (5 секунд)");
            TimeUnit.SECONDS.sleep(5);

            log.info("Получение id запроса в API ФинЦЕРТ");
            response = RestApiFincert.getApiResponse(fincertToken, apiRequestId);
            if (response.statusCode() == 204) {
                continue;
            }
            if (response.jsonPath().get("status").equals("success")) {
                fincertRequestId = response.jsonPath().get("data.id");
            }
            break;
        }

        if (fincertRequestId == null) {
            fail("Ошибка создания запроса в ФинЦЕРТ");
        } else {
            log.info("fincertRequestId:  " + fincertRequestId);
        }

        log.info("Получение токена доступа к API ЦОИ");
        val coiToken = COI_AUTH.getToken();

        for (int i = 0; i < 10; ++i) { // не более 10 минут
            log.info("Ожидание завершения синхронизации (1 минута)");
            TimeUnit.MINUTES.sleep(1);

            log.info("Получение списка инцидентов в API ЦОИ");
            response = RestApiCoordinator.getIncidents(coiToken);
            val jsonPath = response.jsonPath();
            val incidentCount = jsonPath.getList("$").size();
            for (int j = 0; j < incidentCount; ++j) {
                val frid = it.jsonPath().get(String.format("[%d].fincertRequestId", j));
                if (frid != null && frid.equals(fincertRequestId)) {
                    return;
                }
            }
        }

        fail("Созданный в API ФинЦЕРТ инцидент не найден в API ЦОИ");
    }

    private String getCreateIncidentRequestBody() throws IOException {
        val ctx = getJsonResource("create-incident-in-fincert.json");
        ctx.set("$.attachments[0].data.header.sourceId", UUID.randomUUID());
        ctx.set("$.attachments[0].data.incident.antifraud.transfers[0].sourceId", UUID.randomUUID());

        return ctx.jsonString();
    }

    @Test
    public void csv() throws IOException {
        Response response = given()
            .log().all()
            .headers("Authorization", "Bearer " + COI_AUTH.getToken())
            .contentType(JSON)
            .parameter("count", 1000000)
            .when()
            .get(CoiEndpoints.GET_INCIDENTS);
        val jsonPath = response.jsonPath();
        val incidentCount = jsonPath.getList("$").size();

        CSVWriter writer = new CSVWriter(new FileWriter("C:\\COI\\incidents.csv"));
        for (int j = 0; j < incidentCount; ++j) {
            int incId = jsonPath.get(String.format("[%d].id", j));
            Response res = RestApiCoordinator.getMessagesByIncidentId(COI_AUTH.getToken(),incId);
            String[] header = {String.valueOf(incId)};
            if (res.jsonPath().getList("$").size()>0) {
                writer.writeNext(header);
            }

        }
        writer.close();

    }
}
