package com.example.reversement_assurance.utils;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class GeneralUtils {
private GeneralUtils() {
}

 static Logger log = LoggerFactory.getLogger(GeneralUtils.class);
    public static @NotNull String getFirstDayOfNextMonth() {

        HashMap<String, String> revass = BatchContext.getInstance().getRevass();

        Map.Entry<String,String> entry = revass.entrySet().iterator().next();

        //get the first day of next month
        LocalDate today = new LocalDate(entry.getValue().substring(26, 36));
        LocalDate nextMonth = today.withDayOfMonth(1);

       return getFormatedDate(nextMonth.toString());
    }

    @NotNull
    public static String getFormatedDate(String date) {

        Assert.hasText(date, "Date must not be empty");
        LocalDate toFormat = new LocalDate(date);//+1 day

        DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyyyy");
        return toFormat.toString(fmt);
    }

    /**
     * Removes floating point from the string and converts it to a BigInteger
     * @param lsString
     * @return BigInteger with no floating point
     * @author ZIDANI El Mehdi
     */
    @NotNull
    public static BigInteger lsStringToBigInteger(@NotNull String lsString) {
        //remove floating point
        String lsStringClean = lsString.replace("\\.", "");
        return new BigInteger(lsStringClean);
    }
    /**
     * Convert string to BigDecimal
     * the 2 last digits of the string are considered float
     * ex: "12345678912" -> 123456789.12
     * ex: "123456789" -> 123456789.00
     *
     * @param string String formated with LS COBOL format
     * @return BigDecimal formated with 2 digits after the comma
     * @author ZIDANI El Mehdi
     */
    public static BigDecimal lsStringToBigDecimal(@NotNull String string) {
        String last2Digits = string.substring(string.length() - 2);
        BigDecimal bigDecimal = new BigDecimal(string.substring(0, string.length() - 2));
        bigDecimal = bigDecimal.add(new BigDecimal(last2Digits).divide(new BigDecimal(100), 2, RoundingMode.UNNECESSARY));
        return bigDecimal;
    }


/**
 * Calculate the number of months between 2 dates
 * @param startDate
 * @param endDate
 * @return number of months between 2 dates
 * @author ZIDANI El Mehdi
 */
    public static int getNumberOfMonthsBetween(@NotNull String startDate, @NotNull String endDate) {
        LocalDate start = new LocalDate(startDate);
        LocalDate end = new LocalDate(endDate);
        return Months.monthsBetween(start, end).getMonths();
    }

    /**
     * Calculate the number of months between a date and today
     * @param endDate
     * @return number of months between a date and today
     * @author ZIDANI El Mehdi
     */
    public static int getNumberofMonthsBetweenTodayAnd(@NotNull String endDate) {

        //format string to dd-MM-yyyy
        endDate = endDate.substring(0, 2) + "-" + endDate.substring(2, 4) + "-" + endDate.substring(4, 8);
        LocalDate end = new LocalDate().withDayOfMonth(Integer.parseInt(endDate.substring(0, 2))).withMonthOfYear(Integer.parseInt(endDate.substring(3, 5))).withYear(Integer.parseInt(endDate.substring(6, 10)));
        DateTime endTime = new DateTime().withDate(new LocalDate(end));
        DateTime today = new DateTime().withDate(new LocalDate());
        log.info("Today joda time: {}",today);
        log.info("End joda time: {}",endTime);
        return Months.monthsBetween(endTime,today).getMonths();
    }

}
