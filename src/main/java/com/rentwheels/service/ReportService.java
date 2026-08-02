package com.rentwheels.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportService {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ReturnService returnService;

    public String generateSystemReport(String compiledBy) {
        long totalVehicles = vehicleService.getTotalVehiclesCount();
        long activeVehicles = vehicleService.getAvailableVehiclesCount();
        long rentedVehicles = vehicleService.getRentedVehiclesCount();
        long maintenanceVehicles = vehicleService.getMaintenanceVehiclesCount();
        long totalCustomers = customerService.getTotalCustomersCount();
        long totalRentals = rentalService.getAllRentals().size();

        double rentRevenue = rentalService.getTotalRentRevenue();
        double fineRevenue = returnService.getTotalFineRevenue();
        double totalRevenue = rentRevenue + fineRevenue;

        double utilization = totalVehicles > 0 ? ((double) rentedVehicles / totalVehicles) * 100 : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("                       RENTWHEELS ANALYTICS REPORT                       \n");
        sb.append("=========================================================================\n");
        sb.append("Date Run      : ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        sb.append("Compiled By   : ").append(compiledBy != null ? compiledBy : "System Operator").append("\n");
        sb.append("-------------------------------------------------------------------------\n\n");

        sb.append("[1] INVENTORY LEVEL MATRIX\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("   Total Registered Vehicles   : %d\n", totalVehicles));
        sb.append(String.format("   Active (Available to Rent)  : %d\n", activeVehicles));
        sb.append(String.format("   Leased (Currently Rented)   : %d\n", rentedVehicles));
        sb.append(String.format("   Out of Service (Maintenance): %d\n", maintenanceVehicles));
        sb.append(String.format("   Leasing Utilization Rate    : %.2f%%\n\n", utilization));

        sb.append("[2] CUSTOMER DEMOGRAPHICS & ENGAGEMENT\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("   Total Accounts Registered   : %d\n", totalCustomers));
        sb.append(String.format("   Total Rentals Completed     : %d\n\n", totalRentals));

        sb.append("[3] FINANCIAL REVENUE METRICS\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("   Base Rental Charges Accrued : Rs. %.2f\n", rentRevenue));
        sb.append(String.format("   Returned Overdue Fines Paid : Rs. %.2f\n", fineRevenue));
        sb.append("   ----------------------------------------------\n");
        sb.append(String.format("   TOTAL REVENUE GENERATED     : Rs. %.2f\n\n", totalRevenue));

        sb.append("=========================================================================\n");
        sb.append("                   END OF VEHICLE RENTAL MANAGEMENT REPORT               \n");
        sb.append("=========================================================================\n");

        return sb.toString();
    }
}
