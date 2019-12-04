package hub_emulator;

import application_config.AppConfig;
import com.google.gson.Gson;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.ResponseHub;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.aeonbits.owner.ConfigFactory;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.ClearType.LOG;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;

/**
 * Конструктор запускает сервер для эмуляции hub. Чтобы остановить сервер необходимо вызвать метод stop. Чтобы отвечать
 * на poll нужными сообщениями, неоходимо изменить поле класса responsePoll.
 */

@Log4j
public class Server {

    @Getter
    private static URL url;
    private static int port;

    private ClientAndServer emulHubServer;

    @Getter
    private static ResponseHub responseHub;

    @Getter
    private HttpRequest[] requests;

    private static EnumMap<MethodsEnum, ArrayList<HttpRequest>> recordedRequests;

    public Server() {
        initMapRecordedRequests();
        responseHub = new ResponseHub();
        initURL();
        emulHubServer = startClientAndServer(port);
        log.info("Сервер HUB-EMUL: " + url.toString());
        addControllers();
    }

    private void initMapRecordedRequests() {
        recordedRequests = new EnumMap<>(MethodsEnum.class);
        MethodsEnum[] methodsEnums = MethodsEnum.values();
        for (MethodsEnum method : methodsEnums) {
            recordedRequests.put(method, new ArrayList<>());
        }
    }

    public void stop() {
        emulHubServer.stop();
    }

    private void initURL() {
        AppConfig config = ConfigFactory.create(AppConfig.class);
        port = config.hubPort();
        try {
            url = new URL("http://" + config.hubIp() + ":" + port);
        } catch (MalformedURLException e) {
            log.error("MalformedURLException", e);
        }
    }

    /**
     * Метод добавляет контроллеры для обработки реквестов на все необходимые URL
     */
    private void addControllers() {
        MethodsEnum[] methodsEnums = MethodsEnum.values();
        for (MethodsEnum method : methodsEnums) {
            emulHubServer.when(request().withPath(method.getPath()))
                    .callback(callback().withCallbackClass("hub_emulator.Server$Callback"));
        }
    }

    /**
     * Метод проверяет приходил ли на сервер нужный JSON. Например вызов checkReceivedRequest(POLL, 2) возвращает true,
     * если на сервер два раза приходил POLL.
     *
     * @param methods       - тип метода (например POLL)
     * @param countRequests - необходимое колличество которое необходимо валидировать.
     */
    public boolean checkReceivedRequest(MethodsEnum methods, int countRequests) {
        boolean isReceived = false;
        int count = 0;
        while (count != 150) {
            requests = getHttpRequests(methods);
            log.info("[" + count + "] Count requests on " + methods + " -> " + requests.length);
            count++;
            if (requests.length >= countRequests) {
                isReceived = true;
                break;
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
                Thread.currentThread().interrupt();
            }
        }
        return isReceived;
    }

    public HubRequest getLastRequest(MethodsEnum method) {
        String lastRequest = (String) recordedRequests.get(method).get(recordedRequests.get(method).size() - 1).getBody().getValue();
        return new Gson().fromJson(lastRequest, HubRequest.class);
    }

    public String getLastRequest() {
        return (String) getRequests()[getRequests().length - 1].getBody().getValue();
    }

    /**
     * Метод возвращает все реквесты которые были присланы на адрес указанный в аргументах (MethodsEnum method)
     *
     * @param method - MethodsEnum - адрес по которому запрашиваются все полученные реквесты
     */
    private HttpRequest[] getHttpRequests(MethodsEnum method) {
        return recordedRequests.get(method).toArray(new HttpRequest[recordedRequests.get(method).size()]);
    }

    private HttpRequest getLastHttpRequest(MethodsEnum method) {
        return recordedRequests.get(method).get(recordedRequests.get(method).size() - 1);
    }

    public List<HubRequest> getRequests(MethodsEnum method) {
        ArrayList<HubRequest> arrHubRequests = new ArrayList<>();

        ArrayList<HttpRequest> httpRequests = recordedRequests.get(method);

        for (HttpRequest request : httpRequests) {
            arrHubRequests.add(new Gson().fromJson((String) request.getBody().getValue(), HubRequest.class));
        }
        return arrHubRequests;
    }

    public void clearRequests() {
        MethodsEnum[] methodsEnums = MethodsEnum.values();
        for (MethodsEnum m : methodsEnums) {
            emulHubServer.clear(
                    request().withPath(m.getPath()).withMethod("POST"), LOG
            );
            recordedRequests.get(m).clear();
        }
    }

    public void clearRequests(MethodsEnum method) {
        recordedRequests.get(method).clear();
        emulHubServer.clear(request().withPath(method.getPath()).withMethod("POST"), LOG);
    }

    /**
     * Класс необходимый для ответа на poll, в зависимости от условий
     */
    public static class Callback implements ExpectationCallback {
        @Override
        public HttpResponse handle(HttpRequest httpRequest) {
            MethodsEnum method = getValue(httpRequest.getPath().getValue());
            recordedRequests.get(method).add(httpRequest);
            return responseHub.getResponse(httpRequest);
        }

        private MethodsEnum getValue(String path) {
            for (MethodsEnum e : MethodsEnum.values()) {
                if (e.getPath().equals(path)) {
                    return e;
                }
            }
            return null;// not found
        }
    }
}



