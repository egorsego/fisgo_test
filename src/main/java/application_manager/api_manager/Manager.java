package application_manager.api_manager;

import application_manager.api_manager.events.EventsContainer;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.json.CommandEnum;
import application_manager.api_manager.json.request.Request;
import application_manager.api_manager.json.request.TasksRequest;
import application_manager.api_manager.json.request.data.CfgData;
import application_manager.api_manager.json.request.data.KeypadData;
import application_manager.api_manager.json.request.data.LeafData;
import application_manager.api_manager.json.request.data.SetPollData;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.api_manager.json.request.data.enums.CountersFieldsEnum;
import application_manager.api_manager.json.request.data.enums.KeypadActionEnum;
import application_manager.api_manager.json.request.data.enums.LeafEnum;
import application_manager.api_manager.json.response.Response;
import application_manager.api_manager.json.response.data.*;
import application_manager.connections.SSHConnection;
import application_manager.connections.TCPConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hub_emulator.Server;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j
public class Manager {

    @Getter
    private CashBox cashBox;

    private int taskId;
    private TCPConnection tcpConnection;
    private List<TasksRequest> tasksRequestList;
    private BuilderRegNum builderRegNum;
    private SSHConnection sshConnection;

    @Getter
    private EventsContainer eventsContainer;

    public Manager(CashBox cashBox) {
        this.cashBox = cashBox;
        eventsContainer = new EventsContainer();
        tasksRequestList = new ArrayList<>();
        tcpConnection = new TCPConnection(eventsContainer);
        builderRegNum = new BuilderRegNum();
        sshConnection = new SSHConnection();
    }

    /**
     * Метод для старта менеджера (установка tcp-соединения с сервером кассы)
     */
    public void start() {
        log.info(cashBox.getIpAddr());
        log.info(CashBox.TCP_PORT);
        if (tcpConnection.createSocket(cashBox.getIpAddr(), CashBox.TCP_PORT)) {
            if (cashBox.getBoxType().equals(CashBoxType.PULSE_FA)) {
                sleepPlease(10_000);
            }
            tcpConnection.listenEvents(cashBox.getIpAddr(), CashBox.EVENTS_PORT);
        } else {
            log.fatal("Касса недоступна!!!");
            System.exit(1);
        }
    }

    /**
     * Метод для закрытия соединения с кассой
     */
    public void stop() {
        taskId++;
        String close = "{\"tasks\":[{\"task_id\":" + taskId + ",\"command\":\"CLOSE_SESSION\"}]}";
        tcpConnection.closeSocket(close);
        sleepPlease(3000);
        log.info("SOCKET IS CLOSE");
        sshConnection.closeConnection();
    }

    public void pressKey(KeyEnum key) {
        pressKey(cashBox.getKeyboard().getKeyValue(key), 0, 1);
    }

    public void pressKey(KeyEnum key, int count) {
        pressKey(cashBox.getKeyboard().getKeyValue(key), 0, count);
    }

    public void holdKey(KeyEnum key) {
        holdKey(cashBox.getKeyboard().getKeyValue(key), 0);
    }

    /**
     * Нажатие клавиши на кассе.
     *
     * @param key1  - первая кнопка
     * @param key2  - вторая кнопка
     * @param count - количество нажатий
     */
    private void pressKey(int key1, int key2, int count) {
        for (int i = 0; i < count; i++) {
            taskId++;

            //Все кнопки на кассе работают на нажатие (KEY_DOWN), кнопка "меню" работает на отжатие. Отсюда след. условие
            KeypadActionEnum keypadAction;
            if (key1 == 0x04) {
                keypadAction = KeypadActionEnum.KEY_UP;
            } else {
                keypadAction = KeypadActionEnum.KEY_DOWN;
            }

            KeypadData keypadData = new KeypadData(key1, key2, keypadAction);
            TasksRequest taskPressKey = new TasksRequest(taskId, CommandEnum.KEYPAD_ACTION, keypadData);
            tasksRequestList.add(taskPressKey);
        }
    }

    private void pressKey(List<String> keys) {
        String[] arrayKeys = keys.toArray(new String[keys.size()]);
        pressKey(arrayKeys);
    }

    /**
     * Нажатие клавиш на кассе
     *
     * @param setKeys - String множества кнопок, например "13463"
     */
    public void pressKey(String setKeys) {
        String[] integerStrings = setKeys.split("");
        pressKey(integerStrings);
    }

    private void pressKey(String[] integerStrings) {
        for (String key : integerStrings) {
            switch (key) {
                case "-1":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.keyCancel), 0, 1);
                    break;
                case "1":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key1), 0, 1);
                    break;
                case "2":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key2), 0, 1);
                    break;
                case "3":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key3), 0, 1);
                    break;
                case "4":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key4), 0, 1);
                    break;
                case "5":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key5), 0, 1);
                    break;
                case "6":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key6), 0, 1);
                    break;
                case "7":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key7), 0, 1);
                    break;
                case "8":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key8), 0, 1);
                    break;
                case "9":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key9), 0, 1);
                    break;
                case "0":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.key0), 0, 1);
                    break;
                case ".":
                    pressKey(cashBox.getKeyboard().getKeyValue(KeyEnum.keyComma), 0, 1);
                    break;
                default:
                    log.warn("unknown symbol");
            }
        }
        sendCommands();
    }

    /**
     * Зажатие клавиши на кассе.
     *
     * @param key1 - первая кнопка
     * @param key2 - вторая кнопка
     */
    private void holdKey(int key1, int key2) {
        taskId++;
        KeypadData keypadData = new KeypadData(key1, key2, KeypadActionEnum.KEY_HOLD);
        TasksRequest taskPressKey = new TasksRequest(taskId, CommandEnum.KEYPAD_ACTION, keypadData);
        tasksRequestList.add(taskPressKey);
    }

    /**
     * Отправить команды на кассу
     */
    public String sendCommands() {
        //создаём объект request
        Request request = new Request("", tasksRequestList);

        //парсим request в String формата json
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonStr = gson.toJson(request);
        log.debug("jsonStr = " + jsonStr);

        //обнуляем счетчик таск и очищаем лист тасок.
        taskId = 0;
        tasksRequestList.clear();

        //отправляем команду по tcp-соединению и возвращаем результат
        return tcpConnection.sendDataToSocket(jsonStr).split("/r/n")[0];
    }

    /**
     * Метод отправки команды в формате json на кассу. Предназначен для тестирования и дебага.
     */
    public String sendForTest(String command) {
        log.debug(command);
        return tcpConnection.sendDataToSocket(command);
    }

    /**
     * Статус лоадера.
     *
     * @return true - если лоадер включен.
     */
    public boolean getLoaderStatus() {
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.LOADER_STATUS);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getLoaderStatus().equals("ON");
    }

    /**
     * Возвращает пункты меню
     *
     * @param leaf - если CHILD, возвращает все дочерние пункты текущего
     *             если PARENT, возвращает родительский пункт
     *             если CURRENT, возвращает текущий пункт меню
     *             если ALL, возвращает всё
     */
    public Leaf getLeaf(LeafEnum leaf) {
        taskId++;
        LeafData leafData = new LeafData(leaf);
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.GET_LEAF, leafData);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getLeafData();
    }

    /**
     * Возращает необходимое поле из configDb
     *
     * @param configFieldsEnums - Enum нужного поля
     */
    public Map<ConfigFieldsEnum, String> getConfigFields(ConfigFieldsEnum... configFieldsEnums) {
        List<ConfigFieldsEnum> fieldsList = new ArrayList<>(Arrays.asList(configFieldsEnums));
        taskId++;
        CfgData cfgData = new CfgData(fieldsList);
        TasksRequest task = new TasksRequest(taskId, CommandEnum.CFG_GET, cfgData);
        tasksRequestList.add(task);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getConfigData();
    }

    /**
     * Возращает значения из счетчиков
     */
    public CountersResponse getCounters(CountersFieldsEnum... countersFieldsEnums) {
        List<CountersFieldsEnum> fieldsList = new ArrayList<>(Arrays.asList(countersFieldsEnums));
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.COUNTERS_GET, fieldsList);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getCountersData();
    }

    /**
     * Возращает чек
     */
    public Purchase getLastPurchase() {
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.GET_LAST_PURCHASE);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getPurchase();
    }

    /**
     * Команда говорит кассе что-бы она смотрела на эмулятор hub
     */
    public void iAmHub(boolean isEnable) {
        taskId++;
        TasksRequest tasks;
        if (!isEnable) {
            tasks = new TasksRequest(taskId, CommandEnum.HUB_MODE_OFF);
        } else {
            tasks = new TasksRequest(taskId, CommandEnum.HUB_MODE_ON, Server.getUrl());
        }
        tasksRequestList.add(tasks);
        sendCommands();
    }

    private URL getHubBetaUrl() {
        URL url = null;
        try {
            url = new URL("https://hub-beta.dreamkas.ru");
        } catch (MalformedURLException e) {
            log.warn("ошибка URL", e);
        }
        return url;
    }


    public void enableBetaHub(boolean isBeta) {
        if (!sshConnection.isConnected()) {
            sshConnection.openConnection(cashBox.getIpAddr(), CashBox.SSH_NAME, CashBox.SSH_PORT, cashBox.getSshPass());
        }
        int val = 1;
        if (!isBeta) {
            val = 0;
        }
        String got = sshConnection.executeCommand("echo \"attach '/FisGo/configDb.db' as application_config; update " +
                "application_config.CONFIG set connect_to = " + val + ";\" | sqlite3 /FisGo/configDb.db").get(0);
        log.debug(got);
        rebootFiscat();
    }

    /**
     * Получить РНМ для для регистрации кассы
     */
    public String getRegNumKKT() {
        Map<ConfigFieldsEnum, String> kktPlantNumMap = getConfigFields(ConfigFieldsEnum.KKT_PLANT_NUM);
        String kktPlantNum = kktPlantNumMap.get(ConfigFieldsEnum.KKT_PLANT_NUM);
        return builderRegNum.getRegNum(kktPlantNum);
    }

    /**
     * Метод ребутает кассовое ПО (fiscat и punix) и заново открывает соединение с кассой
     */
    public void rebootFiscat() {
        rebootFiscat("");
    }

    /**
     * Метод перезагружает кассовое ПО fiscat перезагружает с флагом из аргумента метода
     */
    public void rebootFiscat(String flagForFiscat) {
        if (!sshConnection.isConnected()) {
            sshConnection.openConnection(cashBox.getIpAddr(), CashBox.SSH_NAME, CashBox.SSH_PORT, cashBox.getSshPass());
        }

        switch (cashBox.getBoxType()) {
            case KASSA_F:
                executeSshCommand("killall fiscat");
                executeSshCommand("killall punix");
                executeSshCommand("sync");
                executeSshCommand("sync");
                executeSshCommand("cd /FisGo/ && ./punix >>/FisGo/outfp &");
                executeSshCommand("cd /FisGo/ && ./fiscat -t " + flagForFiscat + ">>/FisGo/outf &");
                break;
            case DREAMKAS_F:
                executeSshCommand("killall fiscat");
                executeSshCommand("killall wpa_supplicant");
                executeSshCommand("rmmod 8188eu");
                executeSshCommand("cd /FisGo/ && ./fiscat -t " + flagForFiscat + ">>/FisGo/outf &");
                break;
            case PULSE_FA:
                executeSshCommand("killall fiscat");
                executeSshCommand("killall punix");
                executeSshCommand("cd /FisGo/ && ./punix >>/FisGo/outfp &");
                executeSshCommand("cd /FisGo/ && ./fiscat -t " + flagForFiscat + " >>/FisGo/outf &");
                break;
            case KASSA_RB:
                executeSshCommand("killall fiscat");
                executeSshCommand("killall pilarus");
                executeSshCommand("cd /FisGo/ && ./pilarus >>/FisGo/outfp &");
                executeSshCommand("cd /FisGo/ && ./fiscat -t " + flagForFiscat + ">>/FisGo/outf &");
                break;
            default:
                log.warn("НЕИЗВЕСТНЫЙ ТИП КАССЫ");
        }
        sleepPlease(1_000);
        start();
    }

    public void resetPulseFA() {
        if (!sshConnection.isConnected()) {
            sshConnection.openConnection(cashBox.getIpAddr(), CashBox.SSH_NAME, CashBox.SSH_PORT, cashBox.getSshPass());
        }

        executeSshCommand("killall fiscat");
        executeSshCommand("cd /FisGo/ && ./reset_kkt >>/FisGo/resetkktlog &");

        sleepPlease(60_000);

        executeSshCommand("killall punix");
        executeSshCommand("killall reset_kkt");
        executeSshCommand("cd /FisGo/ && ./punix >>/FisGo/outfp &");
        executeSshCommand("cd /FisGo/ && ./fiscat -t >>/FisGo/outf &");

        start();
    }

    /**
     * Метод возвращает лист всех скидок на кассе
     */
    public List<Discount> getDiscounts() {
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.GET_DISCOUNTS);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getDiscounts();
    }

    /**
     * Метод устанавливает время отправки poll.
     */
    public void setPollTime(int sec) {
        taskId++;
        TasksRequest tasks = new TasksRequest(new SetPollData(taskId, CommandEnum.SET_POLL_TIME, sec));
        tasksRequestList.add(tasks);
        sendCommands();
    }

    /**
     * Получить количество товаров в бд на кассе
     */
    public String getCountGoodsFromCashbox() {
        if (!sshConnection.isConnected()) {
            sshConnection.openConnection(cashBox.getIpAddr(), CashBox.SSH_NAME, CashBox.SSH_PORT, cashBox.getSshPass());
        }
        return sshConnection.executeCommand("echo \"attach '/FisGo/goodsDb.db' as goods; select " +
                "count (*) from goods.GOODS;\" | sqlite3 /FisGo/goodsDb.db").get(0);
    }

    public List<String> executeSshCommand(String command) {
        if (!sshConnection.isConnected()) {
            sshConnection.openConnection(cashBox.getIpAddr(), CashBox.SSH_NAME, CashBox.SSH_PORT, cashBox.getSshPass());
        }
        return sshConnection.executeCommand(command);
    }

    /**
     * Возвращает информацию о позиции в чеке
     *
     * @param numberPosition - номер необходимой позиции
     */
    public Position getPositions(int numberPosition) {
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.SEARCH_POSITION, numberPosition);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return new Position(tasksResponse.getTaskResponseList().get(0).getGoodsData(),
                tasksResponse.getTaskResponseList().get(0).getPositionData());
    }

    public int getCountPositions() {
        taskId++;
        TasksRequest tasks = new TasksRequest(taskId, CommandEnum.GET_POS_CNT);
        tasksRequestList.add(tasks);
        String response = sendCommands();
        Response tasksResponse = new Gson().fromJson(response, Response.class);
        return tasksResponse.getTaskResponseList().get(0).getCountPosition();
    }

    /**
     * Возвращает фискальный документ по номеру со всеми необходимыми тегами
     */
    public List<Tag> getDocFromFs(int numberDoc) {
        String response = sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"GET_DOC_FROM_FS\", \"fd\": " + numberDoc + " } ] }");
        Response tasksResponse = new Gson().fromJson(response.split("/r/n")[0], Response.class);
        return tasksResponse.getTaskResponseList().get(0).getDocFromFs().get(0);
    }

    /**
     * Возвращает номер последнего документа
     */
    public int getLastFdNum() {
        String response = sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"LAST_FD_NUM_GET\"} ] }")
                .split("/r/n")[0];
        Response tasksResponse = new Gson().fromJson(response.split("/r/n")[0], Response.class);
        return tasksResponse.getTaskResponseList().get(0).getLastFdNum();
    }

    public int techZeroing() {
        String response = sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"RESET\"} ] }")
                .split("/r/n")[0];
        Response tasksResponse = new Gson().fromJson(response.split("/r/n")[0], Response.class);
        return tasksResponse.getTaskResponseList().get(0).getLastFdNum();
    }

    public DateTime getDateFromFs() {
        String response = sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"GET_DATE_FROM_FS\"} ] }")
                .split("/r/n")[0];
        Response tasksResponse = new Gson().fromJson(response.split("/r/n")[0], Response.class);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        return formatter.parseDateTime(tasksResponse.getTaskResponseList().get(0).getDateFromFs());
    }


    public void setDateFromFs(long dateUnix) {
        sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"SET_DATE_TO_FS\", \"date\": " + dateUnix + "} ] }");
    }

    /**
     * Метод позволяет эмулировать на кассе нажание сканера. Необходимо вызывать метод, когда касса находится в экране
     * свободной продажи
     *
     * @param barcode - баркод товара, либо датаматрикс
     */
    @Step("Пробить штрихкод сканнером")
    public void clickScanner(String barcode) {
        sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"SCANNER\", \"data\": { \"code\": \"" + barcode + "\" } } ] }");
    }

    /**
     * Метод возвращает текущее время на кассе
     */
    public DateTime getCurrentDate() {
        String response = sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"GET_CUR_DATE\" } ] }");
        Response tasksResponse = new Gson().fromJson(response.split("/r/n")[0], Response.class);
        DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        String dataStr = tasksResponse.getTaskResponseList().get(0).getDate();
        String sb = dataStr.substring(0, 4) +
                "-" +
                dataStr.substring(4, 6) +
                "-" +
                dataStr.substring(6, 11) +
                ":" +
                dataStr.substring(11, 13) +
                ":" +
                dataStr.substring(13, dataStr.length());
        return parser.parseDateTime(sb);
    }

    /**
     * Метод активирует режим эмуляции ФН
     */
    public void enableEmulFn(boolean isEnable) {
        if (isEnable) {
            sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"FS_MODE_ON\" } ] }");
            tcpConnection.enableEmulFn(cashBox.getIpAddr(), CashBox.EMUL_FN_PORT);
        } else {
            sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"FS_MODE_OFF\" } ] }");
        }
    }

    public void setResponseTimeFnEmul(boolean isValidResponse) {
        tcpConnection.setValidResponse(isValidResponse);
    }

    public void fsReset() {
        sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"FS_RESET\" } ] }");
    }

    /**
     * Метод удаляет ключи активации из кассы. Работает и нужен только для Дримкас-Ф
     */
    public void clearDreamkasKey() {
        String deleteKeyQuery = "echo \"attach '/FisGo/configDb.db' as DREAMKAS_KEY;" +
                " delete from dreamkas_key.DREAMKAS_KEY;\" | sqlite3 /FisGo/configDb.db";
        executeSshCommand(deleteKeyQuery);
    }

    public void clearVersionTables() {
        String deleteKeyQuery = "echo \"attach '/FisGo/configDb.db' as VERSIONS;" +
                " drop table versions.VERSIONS;\" | sqlite3 /FisGo/configDb.db";
        executeSshCommand(deleteKeyQuery);
    }

    public String getLicense() {
        return sendForTest("{ \"tasks\": [ { \"task_id\": 1, \"command\": \"GET_LICENSE\" } ] }");
    }

    /**
     * sleep)))))0)
     */
    public void sleepPlease(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }

}
