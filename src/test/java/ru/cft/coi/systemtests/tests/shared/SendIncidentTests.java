package ru.cft.coi.systemtests.tests.shared;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.testng.annotations.Test;
import ru.cft.coi.systemtests.api.RestApiCoordinator;
import ru.cft.coi.systemtests.tests.TestBase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

@Slf4j
public class SendIncidentTests extends TestBase {

    @Test
    public void testWhenValidIncidentSentThenStatusChangedToSentAndIncidentBound() throws Exception {
        log.info("Получение токена доступа в API ЦОИ");
        val token = COI_AUTH.getToken();

        log.info("Создание инцидента в API ЦОИ");
        val requestBody = getJsonResource("create-incident-1.json")
                .set("$.assignee", COI_AUTH.getUsername())
                .set("$.attachment.header.sourceId", UUID.randomUUID())
                .set("$.attachment.incident.antifraud.transfers[0].sourceId", UUID.randomUUID())
                .jsonString();

        var response = RestApiCoordinator.createIncident(token, requestBody);
        var jsonPath = response.jsonPath();
        assertEquals(response.statusCode(), 200);
        assertEquals(jsonPath.getString("correspondenceStatus"), "NotStarted");
        assertNull(jsonPath.getString("fincertRequestId"));

        val incidentId = jsonPath.getInt("id");
        log.info("Отправка инцидента {} в ФинЦЕРТ", incidentId);
        response = RestApiCoordinator.sendIncident(token, incidentId);
        assertEquals(response.statusCode(), 200);

        log.info("Проверка наличия сообщения в БДИ");
        response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
        jsonPath = response.jsonPath();
        assertEquals(response.statusCode(), 200);
        assertNotNull(jsonPath.getString("[0].attachments[0]"));

        for (int i = 0; i < 30; ++i) { // не более 5 минут
            log.info("Ожидание завершения отправки инцидента в ФинЦЕРТ (10 секунд)");
            TimeUnit.SECONDS.sleep(10);

            log.info("Проверка обновления инцидента в БДИ");
            response = RestApiCoordinator.getIncident(token, incidentId);
            jsonPath = response.jsonPath();
            assertEquals(response.statusCode(), 200);
            val status = jsonPath.getString("correspondenceStatus");
            switch (status) {
                case "RequestSent":
                    break; // do assertions and return
                case "FormError":
                    fail("Отправка инцидента в ФинЦЕРТ завершилась с ошибкой");
                    return;
                default:
                    continue; // retry
            }
            assertNotNull(jsonPath.getString("fincertRequestId"));
            assertNotNull(jsonPath.getString("fincertRequestHrid"));
            assertNotNull(jsonPath.getString("fincertRequestCreationDatetime"));

            log.info("Проверка обновления сообщения в БДИ");
            response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
            assertEquals(response.statusCode(), 200);
            assertNotNull(response.jsonPath().getString("[0].message.fincertMessageId"));

            return;
        }

        fail("Инцидент или сообщение в БДИ не были обновлены");
    }

    @Test
    public void testWhenInvalidIncidentSentThenStatusChangedToFormError() throws Exception {
        log.info("Получение токена доступа в API ЦОИ");
        val token = COI_AUTH.getToken();

        log.info("Создание инцидента в API ЦОИ");
        val requestBody = getJsonResource("create-incident-1.json")
                .set("$.assignee", COI_AUTH.getUsername())
                .set("$.attachment.incident.description", "[fail]")
                .jsonString();

        var response = RestApiCoordinator.createIncident(token, requestBody);
        var jsonPath = response.jsonPath();

        assertEquals(response.statusCode(), 200);
        assertEquals(jsonPath.getString("correspondenceStatus"), "NotStarted");
        assertNull(jsonPath.getString("fincertRequestId"));

        val incidentId = jsonPath.getInt("id");

        log.info("Отправка инцидента {} в ФинЦЕРТ", incidentId);
        response = RestApiCoordinator.sendIncident(token, incidentId);
        assertEquals(response.statusCode(), 200);

        log.info("Проверка наличия сообщения в БДИ");
        response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
        jsonPath = response.jsonPath();
        assertEquals(response.statusCode(), 200);
        assertNotNull(jsonPath.getString("[0].attachments[0]"));

        for (int i = 0; i < 30; ++i) { // не более 5 минут
            log.info("Ожидание завершения отправки инцидента в ФинЦЕРТ (10 секунд)");
            TimeUnit.SECONDS.sleep(10);

            log.info("Проверка обновления инцидента в БДИ");
            response = RestApiCoordinator.getIncident(token, incidentId);
            jsonPath = response.jsonPath();
            assertEquals(response.statusCode(), 200);
            String status = jsonPath.getString("correspondenceStatus");
            switch (status) {
                case "NotStarted":
                    continue; // retry
                case "FormError":
                    break; // do assertions and return
                default:
                    fail("Отправка инцидента в ФинЦЕРТ завершилась успешно");
                    return;
            }

            log.info("Проверка обновления сообщения в БДИ");
            response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
            jsonPath = response.jsonPath();
            assertEquals(response.statusCode(), 200);
            assertNotNull(jsonPath.getString("[0].message.errorMessage"));

            return;
        }

        fail("Инцидент или сообщение в БДИ не были обновлены");
    }
}
