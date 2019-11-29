package application_manager.api_manager.json.request;

import application_manager.api_manager.json.CommandEnum;
import application_manager.api_manager.json.request.data.CfgData;
import application_manager.api_manager.json.request.data.KeypadData;
import application_manager.api_manager.json.request.data.LeafData;
import application_manager.api_manager.json.request.data.SetPollData;
import application_manager.api_manager.json.request.data.enums.CountersFieldsEnum;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

public class TasksRequest {

    @SerializedName("task_id")
    private int taskId;

    @SerializedName("command")
    private CommandEnum command;

    @SerializedName("data")
    private KeypadData keypadData;

    @SerializedName("cfg_data")
    private CfgData cfgData;

    @SerializedName("counters")
    private List<CountersFieldsEnum> countersData;

    @SerializedName("code")
    private String goodsCode;

    @SerializedName("pos_num")
    private Integer positionNumber;

    @SerializedName("leaf_data")
    private LeafData leafData;

    @SerializedName("url")
    private URL url;

    @SerializedName("poll_time")
    private Integer pollTime;

    /**
     * Конструктор на создание таски для нажатия кнопки
     * @param taskId - номер такси
     * @param command - команда
     * @param keypadData - данные для нажатия кнопок
     */
    public TasksRequest(int taskId, CommandEnum command, KeypadData keypadData) {
        this.taskId = taskId;
        this.command = command;
        this.keypadData = keypadData;
    }

    /**
     * Конструктор на создание таски для получения конфига
     * @param taskId - номер таски
     * @param command - команда
     * @param cfgData - данные для получения конфига
     */
    public TasksRequest(int taskId, CommandEnum command, CfgData cfgData) {
        this.taskId = taskId;
        this.command = command;
        this.cfgData = cfgData;
    }

    public TasksRequest(int taskId, CommandEnum command, LeafData leafData) {
        this.taskId = taskId;
        this.command = command;
        this.leafData = leafData;
    }

    /**
     * Конструктор для создания таски для команд без данных (получение экрана, режим клавиатуры и т.д)
     * @param taskId - номер таски
     * @param command - команда
     */
    public TasksRequest(int taskId, CommandEnum command) {
        this.taskId = taskId;
        this.command = command;
    }

    public TasksRequest(int taskId, CommandEnum command, URL url) {
        this.taskId = taskId;
        this.command = command;
        this.url = url;
    }

    /**
     * Конструктор для создания таски для получения счетчиков
     * @param taskId - номер
     * @param command - команда
     * @param countersData - список необходимых счетчиков
     */
    public TasksRequest(int taskId, CommandEnum command, List<CountersFieldsEnum> countersData){
        this.taskId = taskId;
        this.countersData = countersData;
        this.command = command;
    }

    /**
     * Конструктор для создание таски на получение информации о товаре.
     * @param taskId - номер
     * @param command - команда
     * @param goodsCode - комер товара
     */
    public TasksRequest(int taskId, CommandEnum command, String goodsCode) {
        this.taskId = taskId;
        this.command = command;
        this.goodsCode = goodsCode;
    }

    /**
     * Конструктор на создание таски для получаения информации о позиции в чеке
     * @param taskId - номер
     * @param command - команда
     * @param positionNumber - номер позиции
     */
    public TasksRequest(int taskId, CommandEnum command, Integer positionNumber) {
        this.taskId = taskId;
        this.command = command;
        this.positionNumber = positionNumber;
    }

    /**
     * Конструктор на создание таски для установки времени запроса poll
     * @param setPollData -
     */
    public TasksRequest(SetPollData setPollData){
        this.taskId = setPollData.getTaskId();
        this.command = setPollData.getCommand();
        this.pollTime = setPollData.getPollTime();
    }

}
