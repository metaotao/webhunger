package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.control.util.DataTableCriterias;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostResult;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.entity.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jerry Shen
 * @since 0.1
 */
@Controller
public class HostController {

    private MasterController masterController = ControllerFactory.getController();

    @RequestMapping(value = "/task/{taskName}/host/list", method = RequestMethod.GET)
    public String viewHostTable(@PathVariable("taskName") String taskName, Model model) {
        model.addAttribute("taskName", masterController.getTaskByName(taskName).getTaskName());
        return "host/view_host.jsp";
    }

    @RequestMapping(value = "/task/{taskName}/host/list", method = RequestMethod.POST ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String viewHostData(@PathVariable("taskName") String taskName) {
        Task task = masterController.getTaskByName(taskName);
        JSONObject result = new JSONObject();
        result.put("data", task.getHosts());
        return result.toJSONString();
    }

    /**
     * 开始对该站点进行爬取
     *
     * @param hostId 站点id
     * @return msg
     */
    @RequestMapping(value = "/host/{hostId}/start", method = RequestMethod.POST)
    @ResponseBody
    public String start(@PathVariable String hostId) {
        masterController.start(hostId);
        return "success";
    }

    /**
     * 启动对该任务下的所有站点进行爬取
     *
     * @param taskName task name
     */
    @RequestMapping(value = "/task/{taskName}/start", method = RequestMethod.POST)
    @ResponseBody
    public String startTask(@PathVariable String taskName) {
        masterController.startTask(taskName);
        return "success";
    }

    /**
     * 重新对该站点进行爬取
     *
     * @param hostId 站点id
     * @return msg
     */
    @RequestMapping(value = "/host/{hostId}/restart", method = RequestMethod.POST)
    @ResponseBody
    public String reStart(@PathVariable String hostId) {
        masterController.reStart(hostId);
        return "success";
    }

    /**
     * 停止对该站点爬取
     *
     * @param hostId 站点id
     * @return msg
     */
    @RequestMapping(value = "/host/{hostId}/stop", method = RequestMethod.POST)
    @ResponseBody
    public String stopCrawler(@PathVariable String hostId) {
        masterController.stop(hostId);
        return "success";
    }

    @RequestMapping(value = "/host/{hostId}/config/show", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getHostConfig(@PathVariable String hostId) {
        JSONObject result = new JSONObject();
        result.put("data", masterController.getHostById(hostId));
        return result.toJSONString();
    }

    @RequestMapping(value = "/host/{hostId}/report", method = RequestMethod.GET)
    public String reportCrawler(@PathVariable String hostId, Model model) {
        boolean isDistributed = masterController.isDistributed();
        model.addAttribute("hostId", hostId);
        model.addAttribute("isDistributed", isDistributed);
        Host host = masterController.getHostById(hostId);
        if (host.getLatestSnapshot().getState() == HostState.Completed.getState()) {
            return "host/completed_report.jsp";
        } else {
            // 根据不同爬取模式，设置不同的站点进度刷新间隔
            if (isDistributed) {
                model.addAttribute("flushInterval", 6000);
            } else {
                model.addAttribute("flushInterval", 3000);
            }
            return "host/running_report.jsp";
        }
    }

    @RequestMapping(value = "/host/{hostId}/progress", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String pullProgress(@PathVariable String hostId) {
        JSONObject result = new JSONObject();
        HostCrawlingSnapshotDTO snapshot = masterController.getCurrentCrawlingSnapshot(hostId);
        result.put("data", snapshot);
        return result.toJSONString();
    }

    @RequestMapping(value = "/host/{hostId}/result", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getHostResult(@PathVariable String hostId) {
        JSONObject result = new JSONObject();
        HostResult hostResult = masterController.getHostResult(hostId);
        result.put("data", hostResult);
        return result.toJSONString();
    }

    @RequestMapping(value = "/host/{hostId}/error_pages", method = RequestMethod.POST)
    @ResponseBody
    public String pullErrorPage(@ModelAttribute DataTableCriterias dataTableCriterias, @PathVariable String hostId) {
        List<ErrorPageDTO> errorPages = masterController.getErrorPages(hostId, dataTableCriterias.getStart(), dataTableCriterias.getLength());
        int errorPageNum = masterController.getErrorPageNum(hostId);
        JSONObject result = new JSONObject();
        result.put("data", errorPages);
        result.put("draw", dataTableCriterias.getDraw());
        result.put("recordsTotal", errorPageNum);
        result.put("recordsFiltered", errorPageNum);
        return result.toJSONString();
    }
}
