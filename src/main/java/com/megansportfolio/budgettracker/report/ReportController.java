package com.megansportfolio.budgettracker.report;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.user.UserDetails;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/reports")
public class ReportController {

    @Autowired
    private Configuration config;

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private ReportService reportService;

    @RequestMapping(method = RequestMethod.GET, value = "/year-end-report")
    @ResponseBody
    public HttpEntity<byte[]> makeYearEndReport(@RequestParam long budgetId, @RequestParam int year) throws IOException, TemplateException {

        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        Optional <Budget> budget = budgetDao.findById(budgetId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Map<String, Object> input = new HashMap<>();
        List<BudgetItem> budgetItems = reportService.getReportBudgetItems(budgetId, year, loggedInUserEmailAddress);
        input.put("budgetItems", budgetItems);
        //input.put report totals

        Template template = config.getTemplate("year-end-report.ftl");
        template.process(input, new OutputStreamWriter(byteArrayOutputStream));
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/zip");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=year-end-report-"
                + budget.get().getName() + "-" + year + ".csv");
        byte[] output = byteArrayOutputStream.toByteArray();
        headers.setContentLength(output.length);

        return new HttpEntity<>(output, headers);
    }

}
