package com.chaseoutman.service_util;

import com.chaseoutman.stock.Stock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class ServiceUtil {
    public static void getStockData(Stock stock, String ticker) {
        //gets html parse of the selected stock ticker
        Document doc = getDocument(ticker);

        //Displays the title of the selected webpage
        String title = Objects.requireNonNull(doc).title();
        System.out.println("Title: " + title);

        Elements option = doc.select("option");

        stock.setPrice(Double.parseDouble(
                          doc.selectFirst("[data-symbol=\"" + ticker +"\"]")
                             .attr("value")
        ));

        option.forEach(element -> {
            long date = Long.parseLong(element.selectFirst("option").attr("value"));
            stock.addOptionDate(date);
        });
    }
    private static Document getDocument(String ticker) {
        try {
            Document doc = Jsoup
                    .connect("https://finance.yahoo.com/quote/" + ticker + "/options")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();

            return doc;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
