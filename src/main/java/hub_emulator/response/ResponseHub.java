package hub_emulator.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;

import hub_emulator.response.enums.MethodsEnum;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockserver.model.HttpResponse.response;


@Log4j
public class ResponseHub {

    private Map<MethodsEnum, Queue<HubRequest>> responseMap;

    @Setter
    private String responseFactoryFinish;

    public ResponseHub() {
        initMap();
    }

    private void initMap() {
        responseMap = new EnumMap<>(MethodsEnum.class);
        MethodsEnum[] methodsEnums = MethodsEnum.values();
        for (MethodsEnum method : methodsEnums) {
            responseMap.put(method, new LinkedList<>());
        }
    }

    public HttpResponse getResponse(HttpRequest request) {
        MethodsEnum method = getValue(request.getPath().getValue());

        if (method == null) {
            throw new NullPointerException("method not exist");
        }

        prettyPrintRequest(request);

        String response = null;
        //если ответа нет в очереди
        if (responseMap.get(method).peek() == null) {
            switch (method) {
                case WHO_AM_I:
                case REGISTER:
                    response = new Gson().toJson(HubRequest.builder().result("OK").data(HubData.builder()
                            .owner("x@x.com").build()).build());
                    break;

                case UPDATE_FISGO_VERSION:
                    response = responseFactoryFinish;
                    break;

                case FACTORY_FINISH:
                    response = null;
                    break;

                case DOWNLOAD_NEW_VERSION:
                    if (request.getPath().getValue().equals(MethodsEnum.DOWNLOAD_NEW_VERSION.getPath())) {
                        File file = new File("./src/main/resources/update.tar.gz");
                        try {
                            return response().withBody(Files.readAllBytes(file.toPath()));
                        } catch (IOException e) {
                            log.error(e);
                            return null;
                        }
                    }
                    break;

                case UPLOAD_FILE:
                case UPLOAD_BACKUP_FILE:
                    saveFile(request);
                    response = new Gson().toJson(HubRequest.builder().result("OK").build());
                    break;

                default:
                    response = new Gson().toJson(HubRequest.builder().result("OK").build());
            }
        } else {
            response = new Gson().toJson(responseMap.get(method).poll());
        }
        prettyPrint(response);
        return response().withBody(response, UTF_8);
    }

    private void prettyPrintRequest(HttpRequest request) {
        switch (request.getMethod().toString()){
            case "GET":
                log.info("\u001B[32m" + request.toString() + "\u001B[0m");
                break;
            case "POST":
                log.info("\u001B[32m" + request.getBody().getValue() + "\u001B[0m");
                break;
            case "PUT":
                log.info("\u001B[32m" + request.toString() + "\u001B[0m");
                break;
            default:
                log.error("Необрабатываемый тип http-запроса");
        }
    }

    private void saveFile(HttpRequest httpRequest) {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(
                        new File("./src/main/resources/file"));
                BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream)
        ) {
            byte[] bytes = httpRequest.getBodyAsRawBytes();
            stream.write(bytes);
        } catch (Exception e) {
            log.debug("Вам не удалось загрузить" + e.getMessage());
        }
    }

    private MethodsEnum getValue(String path) {
        for (MethodsEnum e : MethodsEnum.values()) {
            if (e.getPath().equals(path)) {
                return e;
            }
        }
        return null;// not found
    }

    public void addResponse(MethodsEnum method, HubRequest response) {
        responseMap.get(method).add(response);
    }

    public void addResponse(MethodsEnum method, HubRequest... responses) {
        responseMap.get(method).addAll(Arrays.asList(responses));
    }

    public void addResponse(MethodsEnum method, List<HubRequest> responses) {
        responseMap.get(method).addAll(responses);
    }

    private void prettyPrint(String jsonStr) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        log.info("\u001B[34m" + gson.toJson(new JsonParser().parse(jsonStr).getAsJsonObject()) + "\u001B[0m");
    }

}
