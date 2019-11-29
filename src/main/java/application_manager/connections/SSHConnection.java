package application_manager.connections;

import com.jcraft.jsch.*;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
public class SSHConnection {

    private static final int CONNECTION_TIMEOUT = 100000;
    private static final int BUFFER_SIZE = 1024;
    private Session sshSessionCahbox;

    /**
     * Открытие SSH соединения
     *
     * @param host     - IP кассы
     * @param username - логин SSH
     * @param port     - порт SSH
     * @param password - пароль SSH
     */
    public void openConnection(String host, String username, int port, String password) {
        Session session = null;
        JSch jsch;
        try {
            jsch = new JSch();
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect(CONNECTION_TIMEOUT);
            log.info("Connected to host: " + host + " under " + username + " user.");
        } catch (JSchException ex) {
            log.error("Exception", ex);
            System.exit(1);
        }
        sshSessionCahbox = session;
    }

    /**
     * Закрытие соединения SSH
     */
    public void closeConnection() {
        if (isConnected()) {
            sshSessionCahbox.disconnect();
        }
    }

    /**
     * Запрос состояния SSH соединения
     */
    public boolean isConnected() {
        if (sshSessionCahbox == null) {
            return false;
        }
        return sshSessionCahbox.isConnected();
    }

    /**
     * Выполнение команды по SSH
     *
     * @param commandInput - String команда
     * @return ListScreen<String> - список строк возвращаемый stdout
     */
    public List<String> executeCommand(String commandInput) {
        List<String> lines = new ArrayList<>();
        try {
            String cmd = new String(commandInput.getBytes(), "Cp866");
            Channel channel = initChannel(cmd, sshSessionCahbox);
            InputStream in = channel.getInputStream();
            channel.connect();
            String dataFromChannel = getDataFromChannel(channel, in);
            lines.addAll(Arrays.asList(dataFromChannel.split("\n")));
            channel.disconnect();
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return lines;
    }

    /**
     * Инициализация потока
     */
    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");

        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        return channel;
    }

    /**
     * Получение данных из буфера
     */
    private String getDataFromChannel(Channel channel, InputStream in) throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[BUFFER_SIZE];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(tmp, 0, i, "Cp866"));
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                log.info("exit-status: " + exitStatus);
                break;
            }
        }
        return result.toString();
    }

}
