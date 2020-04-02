package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: DateFormat
 * @Author: lanhb
 * @Description: 用来定义日期格式的配置类
 * @Date: 2020/4/2 15:27
 * @Version: 1.0
 */
public class DateFormat {

    private String date = "yyyyMMdd";

    private String yearAndMonth = "yyyyMM";

    private String monthAndDay = "MMdd";

    private String year = "yyyy";

    private String month = "MM";

    private String day = "dd";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public String getMonthAndDay() {
        return monthAndDay;
    }

    public void setMonthAndDay(String monthAndDay) {
        this.monthAndDay = monthAndDay;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
