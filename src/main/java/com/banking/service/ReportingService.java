package com.banking.service;

import com.banking.model.entity.Transaction;
import com.banking.repository.TransactionRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reporting Service
 * Generates PDF and Excel reports
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportingService {

    private final TransactionRepository transactionRepository;

    /**
     * Generate transaction report in PDF format
     */
    public byte[] generateTransactionReportPDF(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Title
            document.add(new Paragraph("Transaction Report")
                    .setFontSize(20)
                    .setBold());

            document.add(new Paragraph("Period: " + startDate.format(DateTimeFormatter.ISO_DATE) +
                    " to " + endDate.format(DateTimeFormatter.ISO_DATE)));

            document.add(new Paragraph("\n"));

            // Get transactions
            List<Transaction> transactions = transactionRepository
                    .findAccountTransactionsBetweenDates(accountId, startDate, endDate);

            // Create table
            Table table = new Table(6);
            table.addHeaderCell("Date");
            table.addHeaderCell("Reference");
            table.addHeaderCell("Type");
            table.addHeaderCell("Amount");
            table.addHeaderCell("Status");
            table.addHeaderCell("Description");

            for (Transaction txn : transactions) {
                table.addCell(txn.getTransactionDate().format(DateTimeFormatter.ISO_DATE_TIME));
                table.addCell(txn.getTransactionReference());
                table.addCell(txn.getTransactionType().toString());
                table.addCell(txn.getAmount().toString());
                table.addCell(txn.getStatus().toString());
                table.addCell(txn.getDescription() != null ? txn.getDescription() : "");
            }

            document.add(table);

            document.close();
            log.info("Generated PDF report for account {} with {} transactions", accountId, transactions.size());

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage());
        }
    }

    /**
     * Generate transaction report in Excel format
     */
    public byte[] generateTransactionReportExcel(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Reference", "Type", "Amount", "Currency", "Status", "Description"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get transactions
            List<Transaction> transactions = transactionRepository
                    .findAccountTransactionsBetweenDates(accountId, startDate, endDate);

            // Add data rows
            int rowNum = 1;
            for (Transaction txn : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(txn.getTransactionDate().format(DateTimeFormatter.ISO_DATE_TIME));
                row.createCell(1).setCellValue(txn.getTransactionReference());
                row.createCell(2).setCellValue(txn.getTransactionType().toString());
                row.createCell(3).setCellValue(txn.getAmount().doubleValue());
                row.createCell(4).setCellValue(txn.getCurrency());
                row.createCell(5).setCellValue(txn.getStatus().toString());
                row.createCell(6).setCellValue(txn.getDescription() != null ? txn.getDescription() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            log.info("Generated Excel report for account {} with {} transactions", accountId, transactions.size());

            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generating Excel report", e);
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage());
        }
    }
}
