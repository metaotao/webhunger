package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostConfig;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.FileUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 2017-11-22
 *
 * @author Jerry Shen
 */
class FileParser {

    /**
     * 解析task文件
     * @param taskFile *.task
     * @return parsed task
     */
    static Task parseTask(File taskFile) throws TaskParseException {

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new FileInputStream(taskFile));
            Element root = document.getRootElement();
            if (root.getName() != "task") {
                throw new TaskParseException("该文件不是task配置文件......");
            }
            Task task = new Task();
            // 使用task文件名作为task的 ID
            task.setTaskId(FileUtil.getFileName(taskFile));
            Element authorElement = root.element("author");
            if (authorElement != null) {
                task.setAuthor(authorElement.getText());
            }
            Element descElement = root.element("description");
            if (descElement != null) {
                task.setDescription(descElement.getText());
            }
            Element startTimeElement = root.element("startTime");
            if (startTimeElement != null) {
                task.setStartTime(FileAccessSupport.transferDate(startTimeElement.getText()));
            }
            Element finishTimeElement = root.element("finishTime");
            if (finishTimeElement != null) {
                task.setFinishTime(FileAccessSupport.transferDate(finishTimeElement.getText()));
            }
            Element clientJarDirElement = root.element("clientJarDir");
            if (clientJarDirElement != null) {
                task.setClientJarDir(clientJarDirElement.getText());
            }

            task.setHostConfig(parseHostConfig(root.element("config")));
            task.setHosts(parseHost(task, root.element("hosts")));

            return task;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TaskParseException("task配置文件解析失败......");
        }
    }

    private static HostConfig parseHostConfig(Element configElement) {
        HostConfig hostConfig = null;
        if (configElement != null) {
            hostConfig = new HostConfig();
            Element depthElement = configElement.element("depth");
            if (depthElement != null) {
                hostConfig.setDepth(Integer.parseInt(depthElement.getText()));
            }
            Element intervalElement = configElement.element("interval");
            if (intervalElement != null) {
                hostConfig.setInterval(Integer.parseInt(intervalElement.getText()));
            }
        }
        return hostConfig;
    }

    private static List<Host> parseHost(Task task, Element hostsElement) throws TaskParseException {
        if (hostsElement == null || hostsElement.elements("host").size() == 0)
            return new ArrayList<>();

        Set<Host> hosts = new HashSet<>();
        List<Element> hostElements = hostsElement.elements("host");
        for (Element hostElement : hostElements) {
            Element hostNameElement = hostElement.element("hostName");
            Element hostIndexElement = hostElement.element("hostIndex");
            if (hostNameElement == null || hostIndexElement == null) {
                throw new TaskParseException("hostIndex结点与hostName结点必须存在......");
            }
            Host host = new Host();
            host.setTask(task);
            host.setHostIndex(hostIndexElement.getText());
            host.setHostName(hostNameElement.getText());
            hosts.add(host);

            host.setHostConfig(parseHostConfig(hostElement.element("config")));
        }

        List<Host> hostList = new ArrayList<>();
        hostList.addAll(hosts);

        return hostList;
    }

    static HostSnapshot parseSnapshot(String snapshotStr) {
        String[] fields = snapshotStr.split("\t");
        HostSnapshot hostSnapshot = new HostSnapshot();
        hostSnapshot.setHostId(fields[0]);
        hostSnapshot.setSuccessPageNum(Integer.parseInt(fields[1]));
        hostSnapshot.setErrorPageNum(Integer.parseInt(fields[2]));
        hostSnapshot.setProcessedPageNum(Integer.parseInt(fields[3]));
        hostSnapshot.setState(Integer.parseInt(fields[4]));
        hostSnapshot.setCreateTime(FileAccessSupport.transferDate(fields[5]));

        return hostSnapshot;
    }
}
