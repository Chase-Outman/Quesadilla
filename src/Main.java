import com.chaseoutman.service_util.ServiceUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import com.chaseoutman.option.Option;
import com.chaseoutman.stock.Stock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<Long> datesInRange = new ArrayList<>();
        Stock stock = new Stock();
        int minDaysToExpire = 0;
        int maxDaysToExpire = 0;
        int dateSelect = 0;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Iron Condor Picker\n" +
                        "Enter stock ticker"
        );
        String stockTicker = scanner.next();
        System.out.println("Enter minimum days to Expiration: ");
        minDaysToExpire = scanner.nextInt();
        System.out.println("Enter maximum days to Expiration: ");
        maxDaysToExpire = scanner.nextInt();



        ServiceUtil.getStockData(stock, stockTicker);
        printStockOptionDates(stock, format, minDaysToExpire, maxDaysToExpire, datesInRange);

        System.out.println("Enter number to show puts and calls for selected date");
        dateSelect = scanner.nextInt();

        displayCallsAndPuts(stock, dateSelect, datesInRange);
    }

    private static void displayCallsAndPuts(Stock stock, int dateSelect, ArrayList<Long> datesInRange) {
        long date = datesInRange.get(dateSelect-1);
        double daysToExpire = calculateDaysToExpire(date+14400);
        double year = 365;
        double timeToExpiration = daysToExpire / year;
        ArrayList<Option> listOfCallOptions = new ArrayList<>();
        ArrayList<Option> listOfPutlOptions = new ArrayList<>();
        try {
            Document doc = Jsoup
                    .connect("https://finance.yahoo.com/quote/SPY/options?date="+ date + "&p=SPY")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();

            Elements testCalls = doc.select("table.calls").select("tbody").select("tr");
            Elements testPuts = doc.select("table.puts").select("tbody").select("tr");

            for(Element t : testCalls) {
                double interestRate = 0.03773;
                Option option = new Option();
                option.setOptionPrice(Double.parseDouble(t.select(".data-col3").text()));
                option.setImpliedVolatility((t.select(".data-col10").text()).replace("%", ""));
                option.setStrikePrice(Double.parseDouble(t.select("td").select("[class=\"C($linkColor) Fz(s)\"]").text()));
                Scanner scanner = new Scanner(option.getImpliedVolatility());
                double impliedVolatility = scanner.nextDouble() /100.0;
                System.out.println(option.getStrikePrice() + " " + impliedVolatility);
                double d1 = (Math.log(stock.getPrice() / option.getStrikePrice()) + (interestRate + (Math.pow(impliedVolatility, 2) / 2)) * timeToExpiration) / (impliedVolatility * Math.sqrt(timeToExpiration));
                double delta = normalDistribution(d1);
                option.setDelta(delta);
                listOfCallOptions.add(option);
            }

            for(Element t : testPuts) {
                double interestRate = 0.03773;
                Option option = new Option();

                option.setOptionPrice(Double.parseDouble(t.select(".data-col3").text()));
                option.setImpliedVolatility((t.select(".data-col10").text()).replace("%", ""));
                option.setStrikePrice(Double.parseDouble(t.select("td").select("[class=\"C($linkColor) Fz(s)\"]").text()));
                Scanner scanner = new Scanner(option.getImpliedVolatility());
                double impliedVolatility = scanner.nextDouble() / 100.0;
                double d1 = (Math.log(stock.getPrice() / option.getStrikePrice()) + (interestRate + (Math.pow(impliedVolatility, 2) / 2)) * timeToExpiration) / (impliedVolatility * Math.sqrt(timeToExpiration));
                double delta = normalDistribution(d1) - 1;
                option.setDelta(delta);
                listOfPutlOptions.add(option);
            }


            System.out.println("\nList of Call Options");
            for (Option listOfCallOption : listOfCallOptions) {
                System.out.print(listOfCallOption.getStrikePrice() + " : ");
                System.out.print(listOfCallOption.getOptionPrice() + " : ");
                System.out.print(listOfCallOption.getImpliedVolatility() + " : ");
                System.out.println(listOfCallOption.getDelta());

            }

            System.out.println("\nList of Put Options : ");
            for (Option listOfPutlOption : listOfPutlOptions) {
                System.out.print(listOfPutlOption.getStrikePrice() + " : ");
                System.out.print(listOfPutlOption.getOptionPrice() + " : ");
                System.out.print(listOfPutlOption.getImpliedVolatility() + " : ");
                System.out.println(listOfPutlOption.getDelta());

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void printStockOptionDates(Stock stock, SimpleDateFormat format, int min, int max, ArrayList<Long> datesInRange) {
        int i = 1;
        System.out.println(stock.getPrice());

        for (Long date : stock.getOptionDates()) {
            int daysToExpire = calculateDaysToExpire(date+14400);
            if (daysToExpire >= min && daysToExpire <= max) {
                datesInRange.add(date);
                System.out.println("\t" + i++ + ": " +  convertLongToDate(date, format) +
                        " (" + daysToExpire + "d) ");
            }

        }

    }

    private static int calculateDaysToExpire(Long date) {
        long SECONDS_IN_DAY = 86400;
        Date currentDate = new Date();
        long currentDateLong = currentDate.getTime() / 1000;
        long daysToExpire = ((date - currentDateLong)/SECONDS_IN_DAY)+1;
        return (int) daysToExpire;
    }

    private static String convertLongToDate(Long date, SimpleDateFormat format) {
        return format.format(new Date((date + 14400)*1000));
    }

    public static double normalDistribution(double x) {
        double pi = Math.PI;
        double a1 = 0.31938153;
        double a2 = -0.356563782;
        double a3 = 1.781477937;
        double a4 = -1.821255978;
        double a5 = 1.330274429;
        double k = 1 / (1 + 0.2316419 * Math.abs(x));
        double n = 1 / Math.sqrt(2 * pi) * Math.exp(-0.5 * Math.pow(x, 2));
        double nPrime = n * (a1 * k + a2 * Math.pow(k, 2) + a3 * Math.pow(k, 3) + a4 * Math.pow(k, 4) + a5 * Math.pow(k, 5));
        if (x < 0) {
            return nPrime;
        } else {
            return 1 - nPrime;
        }
    }
}