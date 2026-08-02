package com.rentwheels.service;

import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Return;
import com.rentwheels.entity.Vehicle;
import com.rentwheels.repository.RentalRepository;
import com.rentwheels.repository.ReturnRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnService {

    private static final double FINE_RATE_PER_DAY = 1000.00;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Return> getAllReturns() {
        return returnRepository.findAllByOrderByIdDesc();
    }

    public Optional<Return> getReturnByRentalId(int rentalId) {
        return returnRepository.findByRental_Id(rentalId);
    }

    @Transactional
    public Return processReturn(int rentalId, String actualReturnDateStr) throws Exception {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental transaction #" + rentalId + " not found."));

        if (!"ACTIVE".equalsIgnoreCase(rental.getStatus())) {
            throw new IllegalStateException("Rental transaction #" + rentalId + " is already returned or inactive.");
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date expectedDate = df.parse(rental.getExpectedReturnDate());
        Date actualDate = df.parse(actualReturnDateStr);

        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(expectedDate);
        expectedCal.set(Calendar.HOUR_OF_DAY, 0);
        expectedCal.set(Calendar.MINUTE, 0);
        expectedCal.set(Calendar.SECOND, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);

        Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(actualDate);
        actualCal.set(Calendar.HOUR_OF_DAY, 0);
        actualCal.set(Calendar.MINUTE, 0);
        actualCal.set(Calendar.SECOND, 0);
        actualCal.set(Calendar.MILLISECOND, 0);

        long diffInMillis = actualCal.getTimeInMillis() - expectedCal.getTimeInMillis();
        long diffDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

        int lateDays = (diffDays > 0) ? (int) diffDays : 0;
        double fine = lateDays * FINE_RATE_PER_DAY;
        double totalBill = rental.getTotalRent() + fine;

        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String receiptNum = "REC-" + dateStamp + "-" + (1000 + (int) (Math.random() * 9000));

        Return returnRecord = new Return(0, rental, actualReturnDateStr, lateDays, fine, totalBill, receiptNum);

        // 1. Save Return Record
        Return savedReturn = returnRepository.save(returnRecord);

        // 2. Update Rental Status to 'RETURNED'
        rental.setStatus("RETURNED");
        rentalRepository.save(rental);

        // 3. Update Vehicle availability back to 1 (Available)
        Vehicle vehicle = rental.getVehicle();
        if (vehicle != null) {
            vehicle.setAvailable(1);
            vehicleRepository.save(vehicle);
        }

        return savedReturn;
    }

    public double getTotalFineRevenue() {
        Double sum = returnRepository.sumTotalFine();
        return (sum != null) ? sum : 0.0;
    }
}
