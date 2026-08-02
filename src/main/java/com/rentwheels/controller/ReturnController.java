package com.rentwheels.controller;

import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Return;
import com.rentwheels.service.RentalService;
import com.rentwheels.service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/returns")
public class ReturnController {

    private static final double FINE_RATE_PER_DAY = 1000.00;

    @Autowired
    private ReturnService returnService;

    @Autowired
    private RentalService rentalService;

    @GetMapping
    public String returnForm(Model model) {
        model.addAttribute("activeRentals", rentalService.getActiveRentals());
        model.addAttribute("returnHistory", returnService.getAllReturns());
        model.addAttribute("activeTab", "return_vehicle");
        return "returns";
    }

    @PostMapping("/process")
    public String processReturn(
            @RequestParam("rentalId") int rentalId,
            @RequestParam("actualReturnDate") String actualReturnDate,
            RedirectAttributes redirectAttributes) {

        try {
            Return returnObj = returnService.processReturn(rentalId, actualReturnDate);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Return transaction processed successfully! Receipt: " + returnObj.getReceiptNumber());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/returns";
    }

    @GetMapping("/api/calculate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calculateReturnDetails(
            @RequestParam("rentalId") int rentalId,
            @RequestParam("actualReturnDate") String actualReturnDateStr) {

        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Rental> rentalOpt = rentalService.getRentalById(rentalId);
            if (rentalOpt.isEmpty()) {
                response.put("error", "Rental not found");
                return ResponseEntity.badRequest().body(response);
            }

            Rental r = rentalOpt.get();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date expectedDate = df.parse(r.getExpectedReturnDate());
            Date actualDate = df.parse(actualReturnDateStr);

            Calendar expectedCal = Calendar.getInstance();
            expectedCal.setTime(expectedDate);
            expectedCal.set(Calendar.HOUR_OF_DAY, 0);
            expectedCal.set(Calendar.MINUTE, 0);
            expectedCal.set(Calendar.SECOND, 0);

            Calendar actualCal = Calendar.getInstance();
            actualCal.setTime(actualDate);
            actualCal.set(Calendar.HOUR_OF_DAY, 0);
            actualCal.set(Calendar.MINUTE, 0);
            actualCal.set(Calendar.SECOND, 0);

            long diffInMillis = actualCal.getTimeInMillis() - expectedCal.getTimeInMillis();
            long diffDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

            int lateDays = (diffDays > 0) ? (int) diffDays : 0;
            double fine = lateDays * FINE_RATE_PER_DAY;
            double totalBill = r.getTotalRent() + fine;

            response.put("expectedReturnDate", r.getExpectedReturnDate());
            response.put("lateDays", lateDays);
            response.put("fine", fine);
            response.put("totalRent", r.getTotalRent());
            response.put("totalBill", totalBill);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
