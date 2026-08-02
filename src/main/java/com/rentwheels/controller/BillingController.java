package com.rentwheels.controller;

import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Return;
import com.rentwheels.service.RentalService;
import com.rentwheels.service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ReturnService returnService;

    @GetMapping
    public String billingPage(Model model) {
        model.addAttribute("rentals", rentalService.getAllRentals());
        model.addAttribute("activeTab", "billing");
        return "billing";
    }

    @GetMapping("/invoice/{rentalId}")
    @ResponseBody
    public ResponseEntity<String> getInvoice(@PathVariable("rentalId") int rentalId) {
        Optional<Rental> rentalOpt = rentalService.getRentalById(rentalId);
        if (rentalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Rental r = rentalOpt.get();
        Optional<Return> returnOpt = returnService.getReturnByRentalId(rentalId);
        Return ret = returnOpt.orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("                 RENTWHEELS                       \n");
        sb.append("          VEHICLE RENTAL INVOICE RECEIPT          \n");
        sb.append("==================================================\n\n");

        sb.append(String.format("Rental ID      : #%06d\n", r.getId()));
        sb.append(String.format("Status         : %s\n", r.getStatus()));
        if (ret != null) {
            sb.append(String.format("Receipt Number : %s\n", ret.getReceiptNumber()));
        }
        sb.append("Date Generated : ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

        sb.append("--- CUSTOMER INFORMATION -------------------------\n");
        sb.append("ID             : ").append(r.getCustomer().getId()).append("\n");
        sb.append("Name           : ").append(r.getCustomer().getName()).append("\n");
        sb.append("Phone Number   : ").append(r.getCustomer().getPhone()).append("\n");
        if (r.getCustomer().getEmail() != null) {
            sb.append("Email Address  : ").append(r.getCustomer().getEmail()).append("\n");
        }
        sb.append("Driver License : ").append(r.getCustomer().getDriverLicense()).append("\n\n");

        sb.append("--- VEHICLE DETAILS ------------------------------\n");
        sb.append("ID             : ").append(r.getVehicle().getVehicleId()).append("\n");
        sb.append("Name           : ").append(r.getVehicle().getVehicleName()).append("\n");
        sb.append("Category       : ").append(r.getVehicle().getCategory()).append("\n");
        sb.append("Rate Per Day   : Rs. ").append(String.format("%.2f", r.getVehicle().getRentPerDay())).append("\n\n");

        sb.append("--- TRANSACTION PRICING DETAILS ------------------\n");
        sb.append("Rental Date    : ").append(r.getRentalDate()).append("\n");
        sb.append("Return Expect  : ").append(r.getExpectedReturnDate()).append("\n");
        sb.append("Days Reserved  : ").append(r.getDays()).append("\n");
        sb.append("Base Rent Cost : Rs. ").append(String.format("%.2f", r.getTotalRent())).append("\n");

        if (ret != null) {
            sb.append("Actual Return  : ").append(ret.getReturnDate()).append("\n");
            sb.append("Late Days Count: ").append(ret.getLateDays()).append("\n");
            sb.append("Assessed Fine  : Rs. ").append(String.format("%.2f", ret.getFine())).append("\n");
            sb.append("--------------------------------------------------\n");
            sb.append("TOTAL BILL COST: Rs. ").append(String.format("%.2f", ret.getTotalBill())).append("\n");
        } else {
            sb.append("--------------------------------------------------\n");
            sb.append("ESTIMATED CHARGES: Rs. ").append(String.format("%.2f", r.getTotalRent())).append("\n");
            sb.append("(*Fines assessed upon return if overdue)\n");
        }
        sb.append("--------------------------------------------------\n\n");
        sb.append("       Thank you for choosing RentWheels!\n");
        sb.append("         Safe travels and drive alert.\n");
        sb.append("==================================================\n");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(sb.toString());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<Rental> list = rentalService.getAllRentals();
        StringBuilder csv = new StringBuilder();
        csv.append("Rental ID,Customer Name,Vehicle Name,Status,Total Cost\n");

        for (Rental r : list) {
            csv.append(String.format("%d,\"%s\",\"%s\",%s,%.2f\n",
                    r.getId(),
                    r.getCustomer().getName(),
                    r.getVehicle().getVehicleName(),
                    r.getStatus(),
                    r.getTotalRent()
            ));
        }

        byte[] content = csv.toString().getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rent_transactions_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}
