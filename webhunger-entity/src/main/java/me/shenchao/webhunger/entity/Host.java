package me.shenchao.webhunger.entity;

import me.shenchao.webhunger.util.common.UrlUtils;

import java.util.Date;

/**
 * 封装一个站点的信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class Host {

    private String hostId;

    private transient Task task;

    private String hostName;

    private String hostDomain;

    private String hostSecondDomain;

    private String hostIndex;

    private HostSnapshot latestSnapshot;

    private HostConfig hostConfig;

    private transient Date startTime;

    private transient Date finishTime;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostDomain() {
        return hostDomain;
    }

    private void setHostDomain(String hostDomain) {
        this.hostDomain = hostDomain;
    }

    public String getHostSecondDomain() {
        return hostSecondDomain;
    }

    private void setHostSecondDomain(String hostSecondDomain) {
        this.hostSecondDomain = hostSecondDomain;
    }

    public String getHostIndex() {
        return hostIndex;
    }

    public void setHostIndex(String hostIndex) {
        this.hostIndex = hostIndex;
        setHostDomain(UrlUtils.removePort(UrlUtils.getDomain(hostIndex)));
        setHostSecondDomain(UrlUtils.getHostSecondDomain(getHostDomain()));
    }

    public HostSnapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    public void setLatestSnapshot(HostSnapshot latestSnapshot) {
        this.latestSnapshot = latestSnapshot;
    }

    public HostConfig getHostConfig() {
        return hostConfig != null ? hostConfig : task.getHostConfig();
    }

    public void setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public Date getStartTime() {
        return startTime != null ? startTime : task.getStartTime();
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime != null ? finishTime : task.getFinishTime();

    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Host host = (Host) o;

        return hostIndex != null ? hostIndex.equals(host.hostIndex) : host.hostIndex == null;
    }

    @Override
    public int hashCode() {
        return hostIndex != null ? hostIndex.hashCode() : 0;
    }
}
