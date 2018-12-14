package com.dhieugo.medianet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MediaNetCrawlChungChi {

    private final String url = "https://thongtin.medinet.org.vn/Ch%E1%BB%A9ng-ch%E1%BB%89-h%C3%A0nh-ngh%E1%BB%81";
    private final String EVENT_TARGET = "__EVENTTARGET";

    public static void main (String args []) throws Exception {

        MediaNetCrawlChungChi crawl = new MediaNetCrawlChungChi();
        crawl.saveAllLicensesToFile();
    }

    private void saveAllLicensesToFile() throws IOException {

        System.out.println("Start crawl data at: " + System.currentTimeMillis());
        // all offices will be stored into this variable
        // we will save it to csv file later
        List<ChungChi> chungchis = new ArrayList<ChungChi>();

        // find all offices by first url
        // add them to global variable, return the next pages maps
        int p = 1; // page = 1

        Document doc = Jsoup.connect(url).post();
        List<ChungChi> foundChungChiItems = findChungChiByDoc(doc);
        Map<Integer, String> pageIndexs = Collections.synchronizedMap(new ConcurrentHashMap<Integer, String>());
        pageIndexs = findPages(doc, p);
        // find all input of the form
        Map<String, String> formData = findNextForm(doc, p + 1, pageIndexs);
        // add first list
        chungchis.addAll(foundChungChiItems);

        Iterator<Map.Entry<Integer, String>> pageIterator = pageIndexs.entrySet().iterator();
        while (pageIterator.hasNext()) {

            Map.Entry<Integer, String> pi = pageIterator.next();
            System.out.println("Key : " + pi.getKey() + " Value :" + pi.getValue());
            try {
                doc = Jsoup.connect(url)
                        .data(formData)
                        .post();
            } catch (Exception e) {
                System.out.println("Having issue at page: " + p);
                p ++;
                continue;
            }
            p ++;

            foundChungChiItems = findChungChiByDoc(doc);
            pageIndexs.putAll(findPages(doc, p));
            formData = findNextForm(doc, p + 1, pageIndexs);
            chungchis.addAll(foundChungChiItems);
            pageIterator.remove();
            pageIterator = pageIndexs.entrySet().iterator();
        }
        System.out.println("End crawl data at: " + System.currentTimeMillis());
        System.out.println("Start writing data to file at: " + System.currentTimeMillis());

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("chungchinghey_medianet.csv"), "UTF-8"));
        CSVPrinter csvPrinter = new CSVPrinter(bw, CSVFormat.EXCEL
                .withHeader(
                        "Họ tên",
                        "Quốc tịch",
                        "Số chứng chỉ",
                        "Nơi cấp chứng chỉ",
                        "Ngày cấp chứng chỉ",
                        "Phạm vi hành nghề",
                        "Tình trạng",
                        "Đơn vị công tác"
                )
                .withDelimiter(',')
                .withQuote('"')
                .withRecordSeparator("\r\n"));

        for(ChungChi mo: chungchis) {
            csvPrinter.printRecord(
                    mo.getFullname(),
                    mo.getNation(),
                    mo.getLicenseNumber(),
                    mo.getNoicap(),
                    mo.getIssueDate(),
                    mo.getScope(),
                    mo.getStatus(),
                    mo.getDonviCongTac() == null ? "" : mo.getDonviCongTac().stream().map(DonviCongTac::toString).collect(Collectors.joining("\n"))
            );
        }
        csvPrinter.flush();
        System.out.println("End writing data to file at: " + System.currentTimeMillis());


    }

    private Map<String, String> findNextForm(Document doc, int p, Map<Integer, String> pageIndexs) {

        Elements formElements = doc.select("form .aspNetHidden");
        Map<String, String> formData = new HashMap<String, String>();

        for (Element e: formElements.select("input")) {
            if (e.attr("name").equals("__EVENTTARGET")) {
                formData.put(e.attr("name"), pageIndexs.get(p));
            } else {
                formData.put(e.attr("name"), e.attr("value"));
            }
        }
        return formData;
    }

    private Map<Integer, String> findPages(Document doc, int currentPage) {
        Map<Integer, String> pageIndexs = new ConcurrentHashMap<Integer, String>();
        // find next page
        // go to the navigation
        Elements navigationElements = doc.select("ul.pagination");
        for(Element e: navigationElements.get(0).children()) {
            Elements pageElement = e.select("a");
            if (pageElement.hasAttr("href")
                    && pageElement.text().matches("\\d+") ) {

                Integer page = Integer.parseInt(pageElement.text());
                String eventTarget = MediaNetStringUtils.getEventTarget(pageElement.attr("href"));

                if (currentPage < page) {
                    pageIndexs.put(page, eventTarget);
                }
            }
        }

        return pageIndexs;
    }

    private List<ChungChi> findChungChiByDoc(Document doc) {

        List<ChungChi> returnItems = new ArrayList<ChungChi>();
        Elements officeTable = doc.select("table tbody");

        // find details info of the office
        Elements formElements = doc.select("form .aspNetHidden");
        Map<String, String> formData = new HashMap<String, String>();

        for (Element e: formElements.select("input")) {
            formData.put(e.attr("name"), e.attr("value"));

        }

        ChungChi chungchi;
        // list all TR
        // skip first item
        for (Element e: officeTable.get(0).children()) {
            if (e.select("td").hasClass("GridViewCell")) {
                chungchi = new ChungChi();
                chungchi.setSiteId(MediaNetStringUtils.getEventTarget(e.select("td:nth-child(2) a").attr("href")));
                chungchi.setFullname(e.select("td:nth-child(2) a").text());
                chungchi.setNation(e.select("td:nth-child(3)").text());
                chungchi.setLicenseNumber(e.select("td:nth-child(4) a").text());
                chungchi.setScope(e.select("td:nth-child(5)").text());
                chungchi.setStatus(e.select("td:nth-child(6)").text());
                chungchi.setInternalSiteId(Integer.parseInt(e.select("td:nth-child(7)").text()));

                formData.put(EVENT_TARGET, chungchi.getSiteId());
                try {
                    Document docDetails = Jsoup.connect(url)
                            .data(formData)
                            .post();
                    setChungChi(docDetails, chungchi);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Having issue at office site id: " + chungchi.getSiteId());
                }
                returnItems.add(chungchi);
            }
        }
        return returnItems;
    }

    private void setChungChi(Document doc, ChungChi chungchi) {
        Elements tbodyElements = doc.select("div#dnn_ctr419_TimKiemCCHNY_UpdatePanelDetail div.panel-body tbody");

        chungchi.setNoicap(tbodyElements.get(0).select("tr:nth-child(4) td:nth-child(2) span").text());
        chungchi.setIssueDate(tbodyElements.get(0).select("tr:nth-child(5) td:nth-child(2) span").text());

        try {
            Elements workHistoryElements = tbodyElements.get(1).select("tr");
            List<DonviCongTac> workHistories = new ArrayList<DonviCongTac>();
            for (Element workElement : workHistoryElements) {
                if (workElement.children().is("td")) {

                    DonviCongTac dvct = new DonviCongTac();
                    dvct.setName(workElement.select("td:nth-child(2) a").text());
                    dvct.setAddress(workElement.select("td:nth-child(3) a").text());
                    workHistories.add(dvct);
                }
            }

            chungchi.setDonviCongTac(workHistories);
        } catch (Exception e) {
            System.out.println("Having some issue while loading don vi cong tac at: " + chungchi.getSiteId());
        }
    }
}
