package com.rentwheels.controller;

import com.rentwheels.service.CustomerService;
import com.rentwheels.service.RentalService;
import com.rentwheels.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String rentForm(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("vehicles", vehicleService.getAvailableVehicles());
        model.addAttribute("recentRentals", rentalService.getAllRentals());
        model.addAttribute("activeTab", "rent_vehicle");
        return "rentals";
    }

    @PostMapping("/book")
    public String bookVehicle(
            @RequestParam("customerId") String customerId,
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("rentalDate") String rentalDate,
            @RequestParam("expectedReturnDate") String expectedReturnDate,
            @RequestParam("days") int days,
            RedirectAttributes redirectAttributes) {

        try {
            rentalService.bookVehicle(customerId, vehicleId, rentalDate, expectedReturnDate, days);
            redirectAttributes.addFlashAttribute("successMessage", "Booking saved successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/rentals";
    }
}
