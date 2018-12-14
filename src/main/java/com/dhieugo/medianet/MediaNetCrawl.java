package com.dhieugo.medianet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MediaNetCrawl {

    private final String homepageURL = "https://thongtin.medinet.org.vn/Gi%E1%BA%A5y-ph%C3%A9p-ho%E1%BA%A1t-%C4%91%E1%BB%99ng";
    private final String EVENT_TARGET = "__EVENTTARGET";

    public static void main (String args []) throws Exception {

        MediaNetCrawl crawl = new MediaNetCrawl();
        crawl.saveAllLicensesToFile();
    }

    private void saveAllLicensesToFile() throws IOException {

        System.out.println("Start crawl data at: " + System.currentTimeMillis());
        // all offices will be stored into this variable
        // we will save it to csv file later
        List<MediaOffice> mediaOffices = new ArrayList<MediaOffice>();

        // find all offices by first homepageURL
        // add them to global variable, return the next pages maps
        int p = 1; // page = 1

        Document doc = Jsoup.connect(homepageURL).post();
        List<MediaOffice> foundOffices = findOfficeByDoc(doc);
        Map<Integer, String> pageIndexs = Collections.synchronizedMap(new ConcurrentHashMap<Integer, String>());
        pageIndexs = findPages(doc, p);
        // find all input of the form
        Map<String, String> formData = findNextForm(doc, p + 1, pageIndexs);
        // add first list
        mediaOffices.addAll(foundOffices);

        Iterator<Map.Entry<Integer, String>> pageIterator = pageIndexs.entrySet().iterator();
        while (pageIterator.hasNext()) {

            Map.Entry<Integer, String> pi = pageIterator.next();
            System.out.println("Key : " + pi.getKey() + " Value :" + pi.getValue());
            try {
                doc = Jsoup.connect(homepageURL)
                        .data(formData)
                        .post();
            } catch (Exception e) {
                System.out.println("Having issue at page: " + p);
                p ++;
                continue;
            }
            p ++;

            foundOffices = findOfficeByDoc(doc);
            pageIndexs.putAll(findPages(doc, p));
            formData = findNextForm(doc, p + 1, pageIndexs);
            mediaOffices.addAll(foundOffices);
            pageIterator.remove();
            pageIterator = pageIndexs.entrySet().iterator();
        }
        System.out.println("End crawl data at: " + System.currentTimeMillis());
        System.out.println("Start writing data to file at: " + System.currentTimeMillis());

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("giayphephoatdong_medianet.csv"), "UTF-8"));
        CSVPrinter csvPrinter = new CSVPrinter(bw, CSVFormat.EXCEL
                .withHeader(
                        "Tên cơ sở",
                        "Số giấy phép",
                        "Địa chỉ",
                        "Ngày cấp",
                        "Tình trạng",
                        "Người phụ trách chuyên môn",
                        "Ngày sinh",
                        "Giới tính",
                        "Quốc tịch",
                        "Số chứng chỉ hành nghề",
                        "Phạm vi hoạt động",
                        "Văn bằng",
                        "Đơn vị công tác"
                )
                .withDelimiter(',')
                .withQuote('"')
                .withRecordSeparator("\r\n"));

        for(MediaOffice mo: mediaOffices) {
            csvPrinter.printRecord(
                    mo.getName(),
                    mo.getLicenseNumber(),
                    mo.getAddress(),
                    mo.getIssueDate(),
                    mo.getStatus(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getFullname(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getDob(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getGender(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getNation(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getLicenseNumber(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getScope(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getDegree(),
                    mo.getExpertPerson() == null ? "" : mo.getExpertPerson().getWorkHistories()
            );
        }
        csvPrinter.flush();
        System.out.println("End writing data to file at: " + System.currentTimeMillis());


    }

    private Map<String, String> findNextForm(Document doc, int p, Map<Integer, String> pageIndexs) {

        Elements formElements = doc.select("form .aspNetHidden");
        Map<String, String> formData = new HashMap<String, String>();

        for (Element e: formElements.select("input")) {
            if (e.attr("name").equals(EVENT_TARGET)) {
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

    private List<MediaOffice> findOfficeByDoc(Document doc) {

        List<MediaOffice> returnItems = new ArrayList<MediaOffice>();
        Elements officeTable = doc.select("#dnn_ctr422_TimKiemGPHD_grvGPHN tbody");

        // find details info of the office
        Elements formElements = doc.select("form .aspNetHidden");
        Map<String, String> formData = new HashMap<String, String>();

        for (Element e: formElements.select("input")) {
            formData.put(e.attr("name"), e.attr("value"));

        }

        MediaOffice office;
        // list all TR
        // skip first item
        for (Element e: officeTable.get(0).children()) {
            if (e.select("td").hasClass("GridViewCell")) {
                office = new MediaOffice();
                // set id
                office.setSiteId(MediaNetStringUtils.getEventTarget(e.select("td:nth-child(2) a").attr("href")));
                office.setName(e.select("td:nth-child(2) a").text());
                office.setLicenseNumber(e.select("td:nth-child(3) a").text());
                office.setAddress(e.select("td:nth-child(4)").html());
                office.setIssueDate(e.select("td:nth-child(5)").html());
                office.setStatus(e.select("td:nth-child(6)").html());
                office.setInternalSiteId(Integer.parseInt(e.select("td:nth-child(7)").html()));
                office.setHiddenField2(e.select("td:nth-child(8)").html());

                formData.put(EVENT_TARGET, office.getSiteId());
                try {
                    Document docDetails = Jsoup.connect(homepageURL)
                            .data(formData)
                            .post();

                    setExpertPerson(docDetails, office);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Having issue at office site id: " + office.getSiteId());
                }

                System.out.println(office.toString());

                returnItems.add(office);
            }
        }

        return returnItems;
    }

    private void setExpertPerson(Document doc, MediaOffice office) {
        Elements fieldsets = doc.select("div#dnn_ctr422_TimKiemGPHD_upGPHDModal fieldset.search_box");

        Elements officeTypeTable = fieldsets.get(0).children().select("div.table-responsive > table.table-border tbody:first-child");

        office.setType(officeTypeTable.select("tr:nth-child(5) td:nth-child(2)").text());

        Elements tables = fieldsets.get(1).select("div.table-responsive table.table tbody");
        // expert person
        ExpertPerson ep = new ExpertPerson();
        ep.setFullname(tables.get(0).select("tr:nth-child(1) td:nth-child(2)").text());
        ep.setDob(tables.get(0).select("tr:nth-child(2) td:nth-child(2) span").text());
        ep.setGender(tables.get(0).select("tr:nth-child(2) td:nth-child(4) span").text());
        ep.setNation(tables.get(0).select("tr:nth-child(3) td:nth-child(2) span").text());
        ep.setLicenseNumber(tables.get(0).select("tr:nth-child(3) td:nth-child(4) span").text());
        ep.setScope(tables.get(0).select("tr:nth-child(4) td:nth-child(2) span").text());
        ep.setDegree(tables.get(0).select("tr:nth-child(5) td:nth-child(2) span").text());

        try {
            Elements workHistoryElements = tables.get(1).select("tr");
            List<String> workHistories = new ArrayList<String>();
            for (Element workElement : workHistoryElements) {
                if (workElement.children().is("td")) {
                    workHistories.add(workElement.select("td:nth-child(2)").text());
                }
            }
            ep.setWorkHistories(workHistories);
        } catch (Exception ew) {
            System.out.println("Having issue while collect work history at: " + office.getSiteId());
        }
        office.setExpertPerson(ep);
    }
}
