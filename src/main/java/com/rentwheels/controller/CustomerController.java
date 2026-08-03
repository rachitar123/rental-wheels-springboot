package com.rentwheels.controller;

import com.rentwheels.entity.Customer;
import com.rentwheels.service.CustomerService;
import com.rentwheels.util.DatabaseErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listCustomers(@RequestParam(value = "search", required = false) String search, Model model) {
        model.addAttribute("customers", customerService.searchCustomers(search));
        model.addAttribute("searchQuery", search);
        model.addAttribute("newCustomer", new Customer());
        model.addAttribute("activeTab", "customers");
        return "customers";
    }

    @PostMapping("/add")
    public String addCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerService.addCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/customers";
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", DatabaseErrorMessages.toUserMessage(ex));
        }
        return "redirect:/customers";
    }
}
