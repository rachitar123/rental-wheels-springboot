// RentWheels Client Application Script

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Theme Mode
    initTheme();

    // 2. Table Filter Listeners
    initTableFilters();

    // 3. Live Rent Calculation Listeners
    initRentCalculator();

    // 4. Live Return Calculation Listeners
    initReturnCalculator();
});

/** Submit a POST form including Spring Security CSRF token */
function submitPostWithCsrf(actionUrl) {
    const form = document.createElement('form');
    form.method = 'post';
    form.action = actionUrl;

    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
    if (csrfToken) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = '_csrf';
        input.value = csrfToken.getAttribute('content');
        form.appendChild(input);
    }

    document.body.appendChild(form);
    form.submit();
}

// Theme Management
function initTheme() {
    const savedTheme = localStorage.getItem('rentwheels_theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-mode');
    }

    const themeBtn = document.getElementById('themeToggleBtn');
    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            document.body.classList.toggle('dark-mode');
            const isDark = document.body.classList.contains('dark-mode');
            localStorage.setItem('rentwheels_theme', isDark ? 'dark' : 'light');
        });
    }
}

// Table Search Filtering
function initTableFilters() {
    const searchInputs = document.querySelectorAll('.table-search-input');
    searchInputs.forEach(input => {
        const targetTableId = input.getAttribute('data-target-table');
        if (!targetTableId) return;

        input.addEventListener('keyup', () => {
            const filter = input.value.toLowerCase();
            const table = document.getElementById(targetTableId);
            if (!table) return;

            const rows = table.querySelectorAll('tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    });
}

// Live Rent Calculation
function initRentCalculator() {
    const vehicleSelect = document.getElementById('rentalVehicleSelect');
    const rentDateInput = document.getElementById('rentalDateInput');
    const returnDateInput = document.getElementById('returnDateInput');
    const daysLabel = document.getElementById('calculatedDaysLabel');
    const totalLabel = document.getElementById('calculatedTotalLabel');

    if (!vehicleSelect || !rentDateInput || !returnDateInput) return;

    function calculate() {
        const selectedOption = vehicleSelect.options[vehicleSelect.selectedIndex];
        if (!selectedOption || !selectedOption.value) {
            if (daysLabel) daysLabel.textContent = 'Total Days: 0';
            if (totalLabel) totalLabel.textContent = 'Calculated Rent: Rs. 0.00';
            return;
        }

        const rate = parseFloat(selectedOption.getAttribute('data-rate') || 0);
        const rentDate = new Date(rentDateInput.value);
        const returnDate = new Date(returnDateInput.value);

        if (isNaN(rentDate.getTime()) || isNaN(returnDate.getTime())) return;

        let diffInMillis = returnDate.getTime() - rentDate.getTime();
        let diffDays = Math.ceil(diffInMillis / (1000 * 3600 * 24));
        if (diffDays <= 0) diffDays = 1;

        const total = diffDays * rate;

        if (daysLabel) daysLabel.textContent = `Total Days: ${diffDays}`;
        if (totalLabel) totalLabel.textContent = `Calculated Rent: Rs. ${total.toFixed(2)}`;

        const hiddenDays = document.getElementById('hiddenDaysInput');
        if (hiddenDays) hiddenDays.value = diffDays;
    }

    vehicleSelect.addEventListener('change', calculate);
    rentDateInput.addEventListener('change', calculate);
    returnDateInput.addEventListener('change', calculate);
    calculate();
}

// Live Return Calculation (AJAX)
function initReturnCalculator() {
    const rentalSelect = document.getElementById('activeRentalSelect');
    const actualDateInput = document.getElementById('actualReturnDateInput');

    if (!rentalSelect || !actualDateInput) return;

    function calculateReturn() {
        const rentalId = rentalSelect.value;
        const actualDate = actualDateInput.value;

        if (!rentalId || !actualDate) return;

        fetch(`/returns/api/calculate?rentalId=${rentalId}&actualReturnDate=${actualDate}`)
            .then(res => res.json())
            .then(data => {
                if (data.error) return;

                const expLabel = document.getElementById('expectedDateLabel');
                const lateLabel = document.getElementById('lateDaysLabel');
                const fineLabel = document.getElementById('fineLabel');
                const totalBillLabel = document.getElementById('totalBillLabel');

                if (expLabel) expLabel.textContent = `Expected Return: ${data.expectedReturnDate}`;
                if (lateLabel) lateLabel.textContent = `Late Days: ${data.lateDays}`;
                if (fineLabel) fineLabel.textContent = `Accumulated Fine: Rs. ${data.fine.toFixed(2)}`;
                if (totalBillLabel) totalBillLabel.textContent = `Total Bill: Rs. ${data.totalBill.toFixed(2)}`;
            })
            .catch(err => console.error(err));
    }

    rentalSelect.addEventListener('change', calculateReturn);
    actualDateInput.addEventListener('change', calculateReturn);
    calculateReturn();
}

// Invoice preview & printing
function loadInvoice(rentalId) {
    fetch(`/billing/invoice/${rentalId}`)
        .then(res => res.text())
        .then(text => {
            const area = document.getElementById('invoiceArea');
            if (area) area.value = text;
            const printBtn = document.getElementById('printInvoiceBtn');
            if (printBtn) printBtn.disabled = false;
        });
}

function printText(elementId) {
    const el = document.getElementById(elementId);
    if (!el || !el.value) return;

    const printWin = window.open('', '', 'width=800,height=600');
    printWin.document.write('<pre>' + el.value + '</pre>');
    printWin.document.close();
    printWin.focus();
    printWin.print();
    printWin.close();
}
