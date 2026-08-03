package com.rentwheels.controller;

import com.rentwheels.entity.Vehicle;
import com.rentwheels.service.VehicleService;
import com.rentwheels.util.DatabaseErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String listVehicles(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false, defaultValue = "All") String category,
            @RequestParam(value = "status", required = false, defaultValue = "All") String status,
            Model model) {

        model.addAttribute("vehicles", vehicleService.searchVehicles(search, category, status));
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("newVehicle", new Vehicle());
        model.addAttribute("activeTab", "vehicles");
        return "vehicles";
    }

    @PostMapping("/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            vehicleService.addVehicle(vehicle, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/vehicles";
    }

    @PostMapping("/update")
    public String updateVehicle(@ModelAttribute Vehicle vehicle,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            vehicleService.updateVehicle(vehicle, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/vehicles";
    }

    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/vehicles";
    }
}
