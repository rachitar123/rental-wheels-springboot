package com.rentwheels.controller;

import com.rentwheels.service.CustomerService;
import com.rentwheels.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CustomerService customerService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalVehicles", vehicleService.getTotalVehiclesCount());
        model.addAttribute("availableVehicles", vehicleService.getAvailableVehiclesCount());
        model.addAttribute("rentedVehicles", vehicleService.getRentedVehiclesCount());
        model.addAttribute("totalCustomers", customerService.getTotalCustomersCount());
        // Match Swing DashboardPanel: show first 10 vehicles
        model.addAttribute("vehicles", vehicleService.getAllVehicles().stream().limit(10).toList());
        model.addAttribute("activeTab", "dashboard");
        return "dashboard";
    }
}
