package ru.cft.coi.systemtests.tests.shared;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.testng.annotations.Test;
import ru.cft.coi.systemtests.api.RestApiCoordinator;
import ru.cft.coi.systemtests.tests.TestBase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class UpdateIncidentTests extends TestBase {

    @Test
    public void testWhenIncidentSentAgainThenSameSourceIdAndIncVersion() throws Exception {
        log.info("Получения токена доступа в API ЦОИ");
        val token = COI_AUTH.getToken();

        log.info("Создание инцидента в API ЦОИ");
        val createIncidentRequestBody = getJsonResource("create-incident-1.json")
                .set("$.assignee", COI_AUTH.getUsername())
                .set("$.attachment.header.sourceId", UUID.randomUUID())
                .set("$.attachment.incident.antifraud.transfers[0].sourceId", UUID.randomUUID())
                .jsonString();

        var response = RestApiCoordinator.createIncident(token, createIncidentRequestBody);
        assertEquals(response.statusCode(), 200);
        val incidentId = response.jsonPath().getInt("id");

        log.info("Отправка инцидента {} в ФинЦЕРТ", incidentId);
        response = RestApiCoordinator.sendIncident(token, incidentId);
        assertEquals(response.statusCode(), 200);

        log.info("Проверка наличия сообщения в БДИ");
        response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
        var jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("[0].attachments[0].version"), 1);
        val origSourceId = jsonPath.getString("[0].attachments[0].sourceId");
        val origTransferId = jsonPath.getString("[0].attachments[0].incident.antifraud.transfers[0].sourceId");
        boolean incidentSent = false;

        loop:
        for (int i = 0; i < 30; ++i) { // не более 5 минут
            log.info("Ожидание завершения отправки инцидента в ФинЦЕРТ (10 секунд)");
            TimeUnit.SECONDS.sleep(10);

            log.info("Проверка обновления инцидента в БДИ");
            response = RestApiCoordinator.getIncident(token, incidentId);
            jsonPath = response.jsonPath();
            val status = jsonPath.getString("correspondenceStatus");
            switch (status) {
                case "NotStarted":
                    continue; // retry
                case "FormError":
                    fail("Ошибка отправки инцидента в ФинЦЕРТ");
                    return;
                default:
                    incidentSent = true;
                    break loop;
            }
        }

        if (!incidentSent) {
            fail("Инцидент не был отправлен в ФинЦЕРТ");
            return;
        }

        log.info("Обновление инцидента в БДИ");
        val updateIncidentRequestBody = getJsonResource("create-incident-1.json")
                .set("$.attachment.header.sourceId", origSourceId)
                .set("$.attachment.incident.antifraud.transfers[0].sourceId", origTransferId)
                .jsonString();

        response = RestApiCoordinator.updateIncident(token, incidentId, updateIncidentRequestBody);
        assertEquals(response.statusCode(), 200);

        log.info("Отправка обновлённого инцидента в ФинЦЕРТ");
        response = RestApiCoordinator.sendIncident(token, incidentId);
        assertEquals(response.statusCode(), 200);

        log.info("Проверка наличия сообщения в БДИ");
        response = RestApiCoordinator.getMessagesByIncidentId(token, incidentId);
        jsonPath = response.jsonPath();
        val messageCount = jsonPath.getList("$").size();
        for (int i = 0; i < messageCount; ++i) {
            val attachmentCount = jsonPath.getList(String.format("[%d].attachments", i)).size();
            for (int j = 0; j < attachmentCount; ++j) {
                val version = jsonPath.getString(String.format("[%d].attachments[%d].version", i, j));
                val sourceId = jsonPath.getString(String.format("[%d].attachments[%d].sourceId", i, j));
                if (origSourceId.equals(sourceId) && version.equals("2")) {
                    return;
                }
            }
        }
        fail(String.format("Вложение с sourceId '%s' и версией 2 не найдено", origSourceId));
    }
}
