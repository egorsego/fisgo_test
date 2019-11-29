package application_manager.connections;

import application_manager.api_manager.events.EventsContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

@Log4j
public class TCPConnection {

    private Socket workSocket;
    private Socket eventsSocket;
    private Socket emulFnSocket;
    @Getter
    private EmulFNThread emulFNThread;

    private EventsContainer eventsContainer;

    public TCPConnection(EventsContainer eventsContainer) {
        this.eventsContainer = eventsContainer;
    }

    /**
     * Открытие соккета
     *
     * @param host - IP кассы
     * @param port - порт сервера кассы
     */
    public boolean createSocket(String host, int port) {
        boolean isConnected = false;
        int count = 0;
        while (!isConnected && count != 20) {
            try {
                Thread.sleep(1500);
                workSocket = new Socket(host, port);
                isConnected = true;
                log.info("Соединение установлено");
                return true;
            } catch (Exception e) {
                log.warn("Не удалось подключиться к кассе (" + count + ")");
                count++;
            }
        }
        return false;
    }

    /**
     * Отправляет команду закрытия соккета на кассу
     *
     * @param sendCloseSession - сообщение о закрытии в формате JSON
     */
    public void closeSocket(String sendCloseSession) {
        try {
            // передаем команду закрытия соединения
            workSocket.getOutputStream().write(sendCloseSession.getBytes());
            workSocket.close();
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    public String sendDataToSocket(String jsonCommand) {
        String dataUTF8 = "";
        try {
            // Берем входной и выходной потоки сокета
            InputStream sin = workSocket.getInputStream();
            OutputStream sout = workSocket.getOutputStream();
            // передаем данные, читаем ответ
            sout.write(jsonCommand.getBytes());
            byte[] bufClose = new byte[65535];
            int rClose = 0;

            StringBuilder rBuf = new StringBuilder();
            while (!rBuf.toString().contains("/r/n")) {
                rClose = sin.read(bufClose);
                if (rClose != 0) {
                    rBuf.append(new String(bufClose, 0, rClose));
                } else {
                    log.info("Buffer is empty!!!");
                }
                Thread.sleep(200);
            }
            Charset charset = Charset.forName("UTF-8");
            dataUTF8 = new String(rBuf.toString().getBytes(), charset);
            log.debug("data = " + dataUTF8);
        } catch (IOException | InterruptedException e) {
            log.error("IOException | InterruptedException e)", e);
        }
        return dataUTF8;
    }

    /**
     * Метод запускает поток, который слушает ответы от кассы и с помощью класса EventsContainer
     * раскидывает ответы по их типу
     */
    public void listenEvents(String host, int port) {
        boolean isConnected = false;
        int count = 0;
        while (!isConnected && count != 10) {
            try {
                Thread.sleep(1400);
                eventsSocket = new Socket(host, port);
                isConnected = true;
                log.info("EVENTS: Соединение установлено");
            } catch (Exception e) {
                log.warn("EVENTS: Не удалось подключиться к кассе (" + count + ")");
                count++;
            }
        }

        EventsThread eventsThread = new EventsThread();
        eventsThread.start();
    }

    public void enableEmulFn(String host, int port) {
        boolean isConnected = false;
        int count = 0;
        while (!isConnected && count != 3) {
            try {
                Thread.sleep(1400);
                emulFnSocket = new Socket(host, port);
                isConnected = true;
                log.info("EMULFN: Соединение установлено");
            } catch (Exception e) {
                log.warn("EMULFN: Не удалось подключиться к кассе (" + count + ")");
                count++;
            }
        }

        emulFNThread = new EmulFNThread();
        emulFNThread.start();
    }

    /**
     * поток, который слушает ответы от кассы и с помощью класса EventsContainer
     * раскидывает ответы по их типу
     */

    private class EventsThread extends Thread {
        @Override
        public void run() {
            String dataCP866;
            InputStream sin;
            while (true) {
                try {
                    sin = eventsSocket.getInputStream();
                    byte[] bufClose = new byte[65535];
                    int rClose = 0;
                    while (rClose == 0)
                        rClose = sin.read(bufClose);
                    dataCP866 = new String(bufClose, 0, rClose);
                    String[] arr = dataCP866.split("/r/n");
                    eventsContainer.parseEvents(arr);
                } catch (IOException e) {
                    log.error("IOException", e);
                }
            }
        }
    }

    @Setter
    private boolean isValidResponse = true;

    private class EmulFNThread extends Thread {

        @Override
        public void run() {
            InputStream sin;
            OutputStream sout;
            while (true) {
                try {
                    sin = emulFnSocket.getInputStream();
                    sout = emulFnSocket.getOutputStream();
                    byte[] bufClose = new byte[65535];
                    int rClose = 0;

                    while (rClose == 0) {
                        rClose = sin.read(bufClose);
                    }

                    log.info("КОМАНДА ОТ КАССЫ ->  ");
                    for (int i = 0; i < rClose; i++) {
                        log.info(bufClose[i] + "  ");
                    }

                    byte[] response = new byte[]{
                            0x04,
                            0x01, 0x00,
                            0x66,
                            0x22, 0x22, 0x22, 0x22,
                            0x22, 0x22};

                    byte[] invalidResponse = new byte[]{
                            0x04,
                            0x01, 0x00,
                            0x66,
                            0x22, 0x22, 0x22};

                    if (isValidResponse) {
                        sout.write(response);
                        log.info("Валидный ответ на запрос времени из ФН -> " + Arrays.toString(response));
                    } else {
                        sout.write(invalidResponse);
                        log.info("Невалидный ответ на запрос времени из ФН -> " + Arrays.toString(invalidResponse));
                    }

                } catch (IOException e) {
                    log.error("IOException", e);
                }
            }
        }
    }

}
