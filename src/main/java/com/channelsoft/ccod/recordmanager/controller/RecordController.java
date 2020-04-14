package com.channelsoft.ccod.recordmanager.controller;

import com.channelsoft.ccod.recordmanager.monitor.po.AjaxResultPo;
import com.channelsoft.ccod.recordmanager.monitor.po.CheckFailRecordDetailPo;
import com.channelsoft.ccod.recordmanager.monitor.po.EntRecordCheckResultPo;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordCheckResultPo;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: RecordController
 * @Author: lanhb
 * @Description: 用来管理同录音管理相关的api
 * @Date: 2020/4/14 9:29
 * @Version: 1.0
 */
@RestController
@RequestMapping("/record/api")
public class RecordController {

    private final static Logger logger = LoggerFactory.getLogger(RecordController.class);

    private String apiBasePath = "/record/api";

    @Autowired
    IPlatformRecordService platformRecordService;

    @RequestMapping(value = "/checkResult/{yearAndMonth}", method = RequestMethod.GET)
    public AjaxResultPo queryPlatformRecordMonthCheckResult(@PathVariable String yearAndMonth) {
        String uri = String.format("GET %s/checkResult/%s", this.apiBasePath, yearAndMonth);
        logger.debug(String.format("enter %s controller", uri));
        Date beginTime;
        Date endTime;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try
        {
            beginTime = sf.parse(String.format("%s01", yearAndMonth));
            Calendar ca = Calendar.getInstance();
            ca.setTime(beginTime);
            ca.add(Calendar.MONTH, 1);
            ca.add(Calendar.MILLISECOND, -1);
            endTime = ca.getTime();
        }
        catch (Exception ex)
        {
            logger.error(String.format("%s is error year and month", yearAndMonth), ex);
            return new AjaxResultPo(false, String.format("%s is error year and month, want format \"yyyyMM\"", yearAndMonth));
        }
        AjaxResultPo resultPo;
        try
        {
            List<PlatformRecordCheckResultPo> list = platformRecordService.queryPlatformRecordCheckResult(beginTime, endTime);
            logger.debug(String.format("query %d platform check record from %s to %s",
                    list.size(), sf.format(beginTime), sf.format(endTime)));
            resultPo = new AjaxResultPo(true, "query platform check result success", list.size(), list);
        }
        catch (Exception ex)
        {
            logger.error(String.format("query platform check record exception"), ex);
            resultPo = new AjaxResultPo(false, String.format("query  exception : %s", ex.getMessage()));
        }
        logger.debug(String.format("quit %s controller with result %b", uri, resultPo.isSuccess()));
        return resultPo;
    }

    @RequestMapping(value = "/checkResult/{yearAndMonth}/{day}", method = RequestMethod.GET)
    public AjaxResultPo queryEntRecordDayCheckResult(@PathVariable String yearAndMonth, @PathVariable String day) {
        String uri = String.format("GET %s/checkResult/%s/%s", this.apiBasePath, yearAndMonth, day);
        logger.debug(String.format("enter %s controller", uri));
        Date beginTime;
        Date endTime;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try
        {
            beginTime = sf.parse(String.format("%s%s", yearAndMonth, day));
            Calendar ca = Calendar.getInstance();
            ca.setTime(beginTime);
            ca.add(Calendar.DATE, 1);
            ca.add(Calendar.MILLISECOND, -1);
            endTime = ca.getTime();
        }
        catch (Exception ex)
        {
            logger.error(String.format("%s and %s is error yearAndMonth and day", yearAndMonth, day), ex);
            return new AjaxResultPo(false, String.format("/%s/%s is error year, month and day, for example /202004/02", yearAndMonth, day));
        }
        AjaxResultPo resultPo;
        try
        {
            List<EntRecordCheckResultPo> list = platformRecordService.queryEntRecordCheckResult(beginTime, endTime);
            logger.debug(String.format("query %d enterprise check record from %s to %s",
                    list.size(), sf.format(beginTime), sf.format(endTime)));
            resultPo = new AjaxResultPo(true, "query enterprise check result success", list.size(), list);
        }
        catch (Exception ex)
        {
            logger.error(String.format("query enterprise check record exception"), ex);
            resultPo = new AjaxResultPo(false, String.format("query  exception : %s", ex.getMessage()));
        }
        logger.debug(String.format("quit %s controller with result %b", uri, resultPo.isSuccess()));
        return resultPo;
    }

    @RequestMapping(value = "/checkResult/{yearAndMonth}/{day}/{enterpriseId}", method = RequestMethod.GET)
    public AjaxResultPo queryEntCheckFailRecord(@PathVariable String yearAndMonth, @PathVariable String day, @PathVariable String enterpriseId) {
        String uri = String.format("GET %s/checkResult/%s/%s/%s", this.apiBasePath, yearAndMonth, day, enterpriseId);
        logger.debug(String.format("enter %s controller", uri));
        Date beginTime;
        Date endTime;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try
        {
            beginTime = sf.parse(String.format("%s%s", yearAndMonth, day));
            Calendar ca = Calendar.getInstance();
            ca.setTime(beginTime);
            ca.add(Calendar.DATE, 1);
            ca.add(Calendar.MILLISECOND, -1);
            endTime = ca.getTime();
        }
        catch (Exception ex)
        {
            logger.error(String.format("%s and %s is error yearAndMonth and day", yearAndMonth, day), ex);
            return new AjaxResultPo(false, String.format("/%s/%s is error year, month and day, for example /202004/02", yearAndMonth, day));
        }
        AjaxResultPo resultPo;
        try
        {
            List<CheckFailRecordDetailPo> list = platformRecordService.queryEntRecordCheckDetail(enterpriseId, beginTime, endTime);
            logger.debug(String.format("query %s %d check fail record from %s to %s",
                    enterpriseId, list.size(), sf.format(beginTime), sf.format(endTime)));
            resultPo = new AjaxResultPo(true, "query check fail record success", list.size(), list);
        }
        catch (Exception ex)
        {
            logger.error(String.format("query check fail record exception"), ex);
            resultPo = new AjaxResultPo(false, String.format("query  exception : %s", ex.getMessage()));
        }
        logger.debug(String.format("quit %s controller with result %b", uri, resultPo.isSuccess()));
        return resultPo;
    }
}
