package manu.oop.objectsandclasses;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class SmartPacking {
     void main(String[] args) {
        new ParkingApp();
    }

//  SMART PARKING KENYA —  (Location + Capacity Model)
    enum VehicleType {
        CAR,
        MOTORCYCLE,
        TRUCK,
        MATATU
    }

    enum PaymentStatus {
        PENDING,
        CONFIRMED,
        FAILED
    }

    class Vehicle {
        private String  regPlate;
        private String  ownerName;
        private VehicleType vehicleType;
        private String  phoneNumber;

        public Vehicle(String regPlate, String ownerName,
                       VehicleType vehicleType, String phoneNumber) {
            this.regPlate = regPlate;
            this.ownerName = ownerName;
            this.vehicleType = vehicleType;
            this.phoneNumber = phoneNumber;
        }

        public String getRegPlate() {
            return regPlate;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        @Override
        public String toString() {
            return vehicleType + regPlate + " — " + ownerName;
        }
    }

    class ParkingLocation {
        private String locationId;
        private String name;
        private String area;
        private int capacity;
        private int  occupied;

        private static int idCounter = 0;

        public ParkingLocation(String name, String area, int capacity) {
            idCounter++;
            this.locationId = String.format("LOC-%03d", idCounter);
            this.name  = name;
            this.area = area;
            this.capacity = capacity;
            this.occupied = 0;
        }

        // ── HELPER METHODS
        public boolean incrementOccupied() {
            if (occupied >= capacity) return false;
            occupied++;
            return true;
        }

        public void decrementOccupied() {
            if (occupied > 0) occupied--;
        }

        public int getAvailable() {
            return capacity - occupied;
        }

        public boolean isFull() {
            return occupied >= capacity;
        }

        public int getOccupancyPercent() {
            return (int) ((occupied / (double) capacity) * 100);
        }

        // ── GETTERS
        public String getLocationId() { return locationId; }
        public String getName() { return name; }
        public String getArea() { return area; }
        public int getCapacity() { return capacity; }
        public int getOccupied() { return occupied; }

        public String toDropdownString() {
            return name +  area + "  —  "
                    + getAvailable() + " spaces free";
        }

        @Override
        public String toString() {
            return name + " - " + area
                    + " - " + occupied + "/" + capacity + " occupied"
                    + " - " + getAvailable() + " free";
        }
    }

    class ParkingTicket {
        private String ticketId;
        private Vehicle vehicle;
        private ParkingLocation location;
        private LocalDateTime entryTime;
        private LocalDateTime  exitTime;
        private double amountDue;

        private static int ticketCounter = 0;
        private static final double RATE_CAR  = 100.0;
        private static final double RATE_MOTORCYCLE =  50.0;
        private static final double RATE_TRUCK = 200.0;
        private static final double RATE_MATATU = 150.0;

        public ParkingTicket(Vehicle vehicle, ParkingLocation location) {
            ticketCounter++;
            this.ticketId  = String.format("TKT-%04d", ticketCounter);
            this.vehicle  = vehicle;
            this.location = location;
            this.entryTime = LocalDateTime.now();
            this.exitTime = null;
            this.amountDue = 0.0;
        }

        public double checkout() {
            this.exitTime = LocalDateTime.now();
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            double hours = Math.max(1.0, minutes / 60.0);
            double rate;

            switch (vehicle.getVehicleType()) {
                case MOTORCYCLE: rate = RATE_MOTORCYCLE; break;
                case TRUCK: rate = RATE_TRUCK; break;
                case MATATU: rate = RATE_MATATU; break;
                default: rate = RATE_CAR;  break;
            }
            this.amountDue = Math.round(hours * rate);
            return amountDue;
        }

        // How long has this vehicle been parked (for live table display)
        public String getDuration() {
            LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
            long mins = Duration.between(entryTime, end).toMinutes();

            if (mins < 60) return mins + " min";
            return (mins / 60) + "h " + (mins % 60) + "m";
        }

        public String getSummary() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return "\n"
                    + "    SMART PARKING KENYA — TICKET \n"
                    + "  Ticket : " + ticketId + "\n"
                    + "  Location : " + location.getName() + "\n"
                    + "  Area : " + location.getArea() + "\n"
                    + "  Plate : " + vehicle.getRegPlate() + "\n"
                    + "  Owner : " + vehicle.getOwnerName() + "\n"
                    + "  Type : " + vehicle.getVehicleType() + "\n"
                    + "  Phone : " + vehicle.getPhoneNumber() + "\n"
                    + "  Entry : " + entryTime.format(fmt) + "\n"
                    + (exitTime != null
                    ? "  Exit : " + exitTime.format(fmt) + "\n"
                    + "  Duration : " + getDuration() + "\n"
                    + "  Amount   : KES " + (int) amountDue + "\n"
                    : "  Status   : PARKED — " + getDuration() + "\n")
                    ;
        }

        // ── GETTERS
        public String getTicketId() {
            return ticketId;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public ParkingLocation getLocation() {
            return location;
        }

        public LocalDateTime getEntryTime() {
            return entryTime;
        }

        public LocalDateTime getExitTime() {
            return exitTime;
        }

        public double getAmountDue() {
            return amountDue;
        }

        @Override
        public String toString() { return getSummary(); }
    }


// — SERVICE CLASSES
    class ParkingService {
        private ArrayList<ParkingLocation> locations;
        private ArrayList<ParkingTicket> activeTickets;

        public ParkingService() {
            this.locations = new ArrayList<>();
            this.activeTickets = new ArrayList<>();
            loadNairobiLocations();
        }

        private void loadNairobiLocations() {
            addLocation("West gate Mall", "Westlands",  500);
            addLocation("Garden City Mall", "Thika Road",800);
            addLocation("Two Rivers Mall", "Runda", 1200);
            addLocation("The Hub Karen", "Karen", 600);
            addLocation("Village Market", "Gigiri", 700);
            addLocation("Sarit Centre", "Westlands", 450);
            addLocation("Yaya Centre", "Hurlingham", 300);
            addLocation("Junction Mall", "Ngong Road", 550);
            addLocation("Prestige Plaza", "Upper Hill", 250);
            addLocation("Galleria Mall", "Langata Road", 900);
        }

        private void addLocation(String name, String area, int capacity) {
            locations.add(new ParkingLocation(name, area, capacity));
        }

        public ParkingTicket parkVehicle(Vehicle vehicle, String locationId) {
            ParkingLocation location = getLocationById(locationId);
            if (location == null) return null;
            if (!location.incrementOccupied()) return null;

            ParkingTicket ticket = new ParkingTicket(vehicle, location);
            activeTickets.add(ticket);
            return ticket;
        }

        public ParkingTicket exitVehicle(String regPlate) {
            ParkingTicket ticket = findActiveTicket(regPlate);
            if (ticket == null) return null;

            ticket.checkout();
            ticket.getLocation().decrementOccupied();
            activeTickets.remove(ticket);
            return ticket;
        }

        public ParkingTicket searchVehicle(String regPlate) {
            return findActiveTicket(regPlate);
        }

        // ── HELPERS
        private ParkingTicket findActiveTicket(String regPlate) {
            for (ParkingTicket t : activeTickets) {
                if (t.getVehicle().getRegPlate().equalsIgnoreCase(regPlate))
                    return t;
            }
            return null;
        }

        public ParkingLocation getLocationById(String locationId) {
            for (ParkingLocation loc : locations) {
                if (loc.getLocationId().equals(locationId)) return loc;
            }
            return null;
        }

        // ── GETTERS
        public ArrayList<ParkingLocation> getLocations() { return locations; }
        public ArrayList<ParkingTicket> getActiveTickets() { return activeTickets; }
    }

    class MPesaPaymentService {
        private String businessShortCode;
        private String accountName;

        public MPesaPaymentService(String businessShortCode, String accountName) {
            this.businessShortCode = businessShortCode;
            this.accountName = accountName;
        }

        public PaymentStatus stkPush(String phoneNumber, double amountKES,
                                     String ticketId) {
            System.out.println("MPesa Payment");
            System.out.println("Phone : " + phoneNumber);
            System.out.println("Amount : KES " + (int) amountKES);
            System.out.println("Ref : " + ticketId);
            return PaymentStatus.PENDING;
        }

        public PaymentStatus confirmPayment(String phoneNumber, double amountKES) {
            boolean paid = Math.random() < 0.90;
            if (paid) {
                System.out.println("CONFIRMED — " + generateCode());
                return PaymentStatus.CONFIRMED;
            }
            System.out.println("FAILED");
            return PaymentStatus.FAILED;
        }

        private String generateCode() {
            String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++)
                sb.append(chars.charAt((int)(Math.random() * chars.length())));
            return sb.toString();
        }

        public String getBusinessShortCode() { return businessShortCode; }
        public String getAccountName() { return accountName; }
    }

// — GUI CLASSES
    class MPesaDialog extends Dialog implements ActionListener {

        private ParkingTicket  ticket;
        private MPesaPaymentService mpesaService;
        private TextField phoneField;
        private Button confirmButton;
        private Button retryButton;
        private Button cancelButton;
        private Label statusLabel;
        private TextArea receiptArea;
        private PaymentStatus result = PaymentStatus.FAILED;

        public MPesaDialog(Frame parent, ParkingTicket ticket,
                           MPesaPaymentService mpesaService) {
            super(parent, "MPesa Payment — Smart Parking Kenya", true);
            this.ticket = ticket;
            this.mpesaService = mpesaService;
            buildUI();
            sendStkPush();
            setSize(430, 480);
            setLocationRelativeTo(parent);
            setResizable(false);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { cancelPayment(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(10, 10));
            setBackground(new Color(245, 250, 245));

            // Green header
            Panel header = new Panel();
            header.setBackground(new Color(0, 130, 60));
            Label title = new Label("  MPesa STK Push Payment", Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 15));
            title.setForeground(Color.WHITE);
            title.setBackground(new Color(0, 130, 60));
            header.add(title);
            add(header, BorderLayout.NORTH);

            // Payment details
            Panel center = new Panel(new GridBagLayout());
            center.setBackground(new Color(245, 250, 245));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 14, 5, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;

            int row = 0;
            row = infoRow(center, c, row, "Plate:",
                    ticket.getVehicle().getRegPlate());
            row = infoRow(center, c, row, "Owner:",
                    ticket.getVehicle().getOwnerName());
            row = infoRow(center, c, row, "Location:",
                    ticket.getLocation().getName());
            row = infoRow(center, c, row, "Area:",
                    ticket.getLocation().getArea());
            row = infoRow(center, c, row, "Duration:",
                    ticket.getDuration());
            row = infoRow(center, c, row, "Ticket ID:",
                    ticket.getTicketId());
            row = infoRow(center, c, row, "Amount Due:",
                    "KES " + (int) ticket.getAmountDue());

            // Divider
            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label div = new Label("");
            div.setForeground(new Color(200, 200, 200));
            center.add(div, c);

            // Phone field
            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label phoneLbl = new Label("MPesa Phone:");
            phoneLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            center.add(phoneLbl, c);
            c.gridx = 1;
            phoneField = new TextField(ticket.getVehicle().getPhoneNumber());
            phoneField.setBackground(new Color(255, 255, 240));
            center.add(phoneField, c);
            row++;

            // Status
            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            statusLabel = new Label("  Waiting for customer PIN...", Label.LEFT);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            statusLabel.setForeground(new Color(30, 100, 200));
            center.add(statusLabel, c);

            add(center, BorderLayout.CENTER);

            // Bottom: log + buttons
            Panel bottom = new Panel(new BorderLayout(4, 4));
            bottom.setBackground(new Color(245, 250, 245));

            receiptArea = new TextArea("", 4, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            receiptArea.setEditable(false);
            receiptArea.setBackground(new Color(240, 248, 240));
            bottom.add(receiptArea, BorderLayout.CENTER);

            Panel btnRow = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btnRow.setBackground(new Color(245, 250, 245));
            confirmButton = mkBtn("CONFIRM PAYMENT", new Color(0, 140, 60));
            retryButton = mkBtn("RETRY", new Color(140, 100, 0));
            cancelButton = mkBtn("CANCEL", new Color(180, 50, 50));
            confirmButton.addActionListener(this);
            retryButton.addActionListener(this);
            cancelButton.addActionListener(this);
            btnRow.add(confirmButton);
            btnRow.add(retryButton);
            btnRow.add(cancelButton);
            bottom.add(btnRow, BorderLayout.SOUTH);
            add(bottom, BorderLayout.SOUTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "CONFIRM PAYMENT": confirmPayment(); break;
                case "RETRY": retryStkPush();  break;
                case "CANCEL": cancelPayment(); break;
            }
        }

        private void sendStkPush() {
            log("STK Push " + ticket.getVehicle().getPhoneNumber());
            log("Amount KES " + (int) ticket.getAmountDue());
            log("Ref " + ticket.getTicketId());
            log("Waiting for customer PIN...");
            mpesaService.stkPush(ticket.getVehicle().getPhoneNumber(),
                    ticket.getAmountDue(), ticket.getTicketId());
        }

        private void retryStkPush() {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                setStatus("Enter a valid phone number.", new Color(180, 80, 0));
                return;
            }
            log("\nRetrying " + phone);
            mpesaService.stkPush(phone, ticket.getAmountDue(), ticket.getTicketId());
            setStatus("Resent to " + phone + " — waiting...", new Color(30, 100, 200));
        }

        private void confirmPayment() {
            String phone  = phoneField.getText().trim();
            double amount = ticket.getAmountDue();
            confirmButton.setEnabled(false);
            retryButton.setEnabled(false);

            PaymentStatus status = mpesaService.confirmPayment(phone, amount);

            if (status == PaymentStatus.CONFIRMED) {
                result = PaymentStatus.CONFIRMED;
                setStatus("CONFIRMED — KES " + (int) amount, new Color(0, 130, 0));
                log("\nCONFIRMED — KES " + (int) amount);
                log("From: " + phone);
                log(" Safe journey.");
                closeAfterDelay(2000);
            } else {
                result = PaymentStatus.FAILED;
                setStatus("FAILED — retry or collect cash manually.",
                        new Color(180, 0, 0));
                log("\nFAILED — customer did not respond.");
                confirmButton.setEnabled(true);
                retryButton.setEnabled(true);
            }
        }

        private void cancelPayment() {
            result = PaymentStatus.FAILED;
            log("\nCancelled by attendant.");
            dispose();
        }

        private void closeAfterDelay(int ms) {
            new Thread(() -> {
                try { Thread.sleep(ms); }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                dispose();
            }).start();
        }

        private int infoRow(Panel p, GridBagConstraints c,
                            int row, String lbl, String val) {
            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label l = new Label(lbl);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            p.add(l, c);
            c.gridx = 1;
            Label v = new Label(val);
            v.setFont(new Font("SansSerif", Font.PLAIN, 12));
            v.setForeground(new Color(20, 20, 120));
            p.add(v, c);
            return row + 1;
        }

        private Button mkBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        private void setStatus(String text, Color color) {
            statusLabel.setText("  " + text);
            statusLabel.setForeground(color);
        }

        private void log(String text) { receiptArea.append(text + "\n"); }
        public PaymentStatus getResult() { return result; }
    }

    public class ParkingApp extends Frame implements ActionListener {

        private ParkingService parkingService;
        private MPesaPaymentService mpesaService;
        private Choice  locationChoice;
        private TextField plateField;
        private TextField ownerField;
        private TextField phoneField;
        private Choice  vehicleTypeChoice;
        private Button parkButton;
        private Button exitButton;
        private Button searchButton;
        private Button refreshButton;
        private Panel capacityPanel;
        private TextArea activeTable;
        private TextArea outputArea;
        private Label statusLabel;

        public ParkingApp() {
            super("Smart Parking Kenya — Nairobi");
            parkingService = new ParkingService();
            mpesaService   = new MPesaPaymentService("247247", "Smart Parking Kenya");

            setupUI();
            refreshCapacityPanel();
            refreshActiveTable();

            setSize(1000, 700);
            setResizable(true);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
        }

        // ── UI SETUP
        private void setupUI() {
            setLayout(new BorderLayout(6, 6));
            setBackground(new Color(240, 244, 240));

            Panel header = new Panel(new BorderLayout());
            header.setBackground(new Color(0, 110, 45));
            header.setPreferredSize(new Dimension(0, 50));

            Label title = new Label("   SMART PARKING KENYA  —  NAIROBI",
                    Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setForeground(Color.WHITE);
            title.setBackground(new Color(0, 110, 45));
            header.add(title, BorderLayout.CENTER);

            Label clock = new Label(getCurrentTime() + "   ", Label.RIGHT);
            clock.setFont(new Font("SansSerif", Font.PLAIN, 13));
            clock.setForeground(new Color(180, 255, 180));
            clock.setBackground(new Color(0, 110, 45));
            header.add(clock, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            Panel left = new Panel(new GridBagLayout());
            left.setBackground(new Color(248, 252, 248));
            left.setPreferredSize(new Dimension(240, 0));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 10, 5, 10);
            c.fill   = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;

            int row = 0;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label inputHead = new Label("VEHICLE ENTRY", Label.LEFT);
            inputHead.setFont(new Font("SansSerif", Font.BOLD, 13));
            inputHead.setForeground(new Color(0, 100, 40));
            left.add(inputHead, c);

            c.gridy = row++; left.add(boldLbl("Location:"), c);
            c.gridy = row++;
            locationChoice = new Choice();
            populateLocationDropdown();
            locationChoice.addItemListener(e -> updateLocationStatus());
            left.add(locationChoice, c);

            c.gridy = row++;
            statusLabel = new Label("", Label.CENTER);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            left.add(statusLabel, c);
            updateLocationStatus();

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            left.add(boldLbl("Reg Plate:"), c);
            c.gridx = 1;
            plateField = new TextField();
            left.add(plateField, c);
            row++;

            c.gridy = row; c.gridx = 0;
            left.add(boldLbl("Owner Name:"), c);
            c.gridx = 1;
            ownerField = new TextField();
            left.add(ownerField, c);
            row++;

            c.gridy = row; c.gridx = 0;
            left.add(boldLbl("Phone (MPesa):"), c);
            c.gridx = 1;
            phoneField = new TextField();
            left.add(phoneField, c);
            row++;

            c.gridy = row; c.gridx = 0;
            left.add(boldLbl("Vehicle Type:"), c);
            c.gridx = 1;
            vehicleTypeChoice = new Choice();
            for (VehicleType vt : VehicleType.values())
                vehicleTypeChoice.add(vt.name());
            left.add(vehicleTypeChoice, c);
            row++;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            left.add(new Label(" "), c);

            c.gridy = row++;
            parkButton = mkBtn("  PARK VEHICLE  ", new Color(0, 140, 60));
            parkButton.addActionListener(this);
            left.add(parkButton, c);

            c.gridy = row++;
            exitButton = mkBtn("  EXIT VEHICLE  ", new Color(180, 45, 45));
            exitButton.addActionListener(this);
            left.add(exitButton, c);

            c.gridy = row++;
            searchButton = mkBtn("  SEARCH PLATE  ", new Color(40, 90, 180));
            searchButton.addActionListener(this);
            left.add(searchButton, c);

            c.gridy = row++;
            refreshButton = mkBtn("  REFRESH VIEW  ", new Color(100, 100, 100));
            refreshButton.addActionListener(this);
            left.add(refreshButton, c);

            add(left, BorderLayout.WEST);

            Panel center = new Panel(new BorderLayout(6, 6));
            center.setBackground(new Color(240, 244, 240));

            Panel capWrapper = new Panel(new BorderLayout(4, 4));
            capWrapper.setBackground(new Color(240, 244, 240));

            Label capTitle = new Label("  NAIROBI LOCATIONS — LIVE CAPACITY",
                    Label.LEFT);
            capTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            capTitle.setForeground(new Color(0, 80, 30));
            capWrapper.add(capTitle, BorderLayout.NORTH);

            capacityPanel = new Panel(new GridLayout(0, 2, 6, 4));
            capacityPanel.setBackground(new Color(240, 244, 240));

            Panel capOuter = new Panel(new BorderLayout());
            capOuter.setBackground(new Color(240, 244, 240));
            capOuter.add(capacityPanel, BorderLayout.NORTH);
            capWrapper.add(capOuter, BorderLayout.CENTER);

            center.add(capWrapper, BorderLayout.NORTH);

            Panel activeWrapper = new Panel(new BorderLayout(4, 4));
            activeWrapper.setBackground(new Color(240, 244, 240));

            Label activeTitle = new Label(
                    "  CURRENTLY PARKED  —  click plate in search to highlight",
                    Label.LEFT);
            activeTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            activeTitle.setForeground(new Color(0, 80, 30));
            activeWrapper.add(activeTitle, BorderLayout.NORTH);

            activeTable = new TextArea("", 10, 60,
                    TextArea.SCROLLBARS_VERTICAL_ONLY);
            activeTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            activeTable.setEditable(false);
            activeTable.setBackground(new Color(245, 252, 245));
            activeWrapper.add(activeTable, BorderLayout.CENTER);
            center.add(activeWrapper, BorderLayout.CENTER);

            add(center, BorderLayout.CENTER);

            Panel bottomWrap = new Panel(new BorderLayout(4, 4));
            bottomWrap.setBackground(new Color(240, 244, 240));

            Label outTitle = new Label("  TICKET / RECEIPT", Label.LEFT);
            outTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            outTitle.setForeground(new Color(0, 80, 30));
            bottomWrap.add(outTitle, BorderLayout.NORTH);

            outputArea = new TextArea("", 8, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            outputArea.setEditable(false);
            outputArea.setBackground(new Color(240, 250, 240));
            bottomWrap.add(outputArea, BorderLayout.CENTER);

            add(bottomWrap, BorderLayout.SOUTH);
        }

        // ── CAPACITY PANEL
        private void refreshCapacityPanel() {
            capacityPanel.removeAll();

            for (ParkingLocation loc : parkingService.getLocations()) {
                Panel card = new Panel(new GridBagLayout());
                card.setBackground(Color.WHITE);

                int pct = loc.getOccupancyPercent();
                Color barColor;
                if      (pct >= 90) barColor = new Color(220, 60, 60);
                else if (pct >= 70) barColor = new Color(210, 140, 0);
                else                barColor = new Color(0, 150, 60);

                GridBagConstraints cc = new GridBagConstraints();
                cc.insets  = new Insets(3, 8, 2, 8);
                cc.fill    = GridBagConstraints.HORIZONTAL;
                cc.anchor  = GridBagConstraints.WEST;
                cc.weightx = 1.0;

                // Location name
                cc.gridy = 0; cc.gridx = 0;
                Label nameLbl = new Label(loc.getName());
                nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                nameLbl.setForeground(new Color(30, 30, 80));
                card.add(nameLbl, cc);

                cc.gridy = 1;
                Label areaLbl = new Label(loc.getArea());
                areaLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
                areaLbl.setForeground(new Color(100, 100, 100));
                card.add(areaLbl, cc);

                cc.gridy = 2;
                String spaceText = loc.getAvailable() + " free  /  "
                        + loc.getCapacity() + " total";
                Label spaceLbl = new Label(spaceText);
                spaceLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                spaceLbl.setForeground(barColor);
                card.add(spaceLbl, cc);

                cc.gridy = 3;
                Label bar = new Label(buildBar(pct) + "  " + pct + "%");
                bar.setFont(new Font("Monospaced", Font.PLAIN, 11));
                bar.setForeground(barColor);
                card.add(bar, cc);

                capacityPanel.add(card);
            }

            capacityPanel.revalidate();
            capacityPanel.repaint();
            validate();
        }

        private String buildBar(int percent) {
            int filled = (int)(percent / 10.0);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < 10; i++)
                sb.append(i < filled ? "==" : "--");
            sb.append("]");
            return sb.toString();
        }

        // ── ACTIVE TABLE
        private void refreshActiveTable() {
            ArrayList<ParkingTicket> tickets = parkingService.getActiveTickets();

            if (tickets.isEmpty()) {
                activeTable.setText("  No vehicles currently parked.\n");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String fmt = "%-10s %-12s %-18s %-20s %-10s\n";
            sb.append(String.format(fmt,
                    "TICKET", "PLATE", "OWNER", "LOCATION", "DURATION"));
            sb.append("─".repeat(72)).append("\n");

            for (ParkingTicket t : tickets) {
                sb.append(String.format(fmt,
                        t.getTicketId(),
                        t.getVehicle().getRegPlate(),
                        truncate(t.getVehicle().getOwnerName(), 17),
                        truncate(t.getLocation().getName(), 19),
                        t.getDuration()));
            }

            sb.append("─".repeat(72)).append("\n");
            sb.append("  Total parked: ").append(tickets.size()).append(" vehicle(s)\n");
            activeTable.setText(sb.toString());
        }

        // Truncates long strings so the table stays aligned
        private String truncate(String s, int max) {
            return s.length() > max ? s.substring(0, max - 1) + "…" : s;
        }

        // ── LOCATION DROPDOWN

        private void populateLocationDropdown() {
            locationChoice.removeAll();
            for (ParkingLocation loc : parkingService.getLocations()) {
                locationChoice.add(loc.getLocationId() + " | " + loc.getName()
                        + " (" + loc.getArea() + ")");
            }
        }

        // Updates the status label below the dropdown
        private void updateLocationStatus() {
            ParkingLocation loc = getSelectedLocation();
            if (loc == null) return;

            int pct = loc.getOccupancyPercent();
            String text;
            Color  color;

            if (loc.isFull()) {
                text  = "FULL — " + loc.getName();
                color = new Color(200, 0, 0);
            } else if (pct >= 70) {
                text  = loc.getAvailable() + " spaces — filling up!";
                color = new Color(180, 100, 0);
            } else {
                text  = loc.getAvailable() + " spaces available";
                color = new Color(0, 130, 0);
            }

            statusLabel.setText(text);
            statusLabel.setForeground(color);
        }

        private ParkingLocation getSelectedLocation() {
            String s = locationChoice.getSelectedItem();
            if (s == null) return null;
            String id = s.split(" \\| ")[0].trim();
            return parkingService.getLocationById(id);
        }

        // ── ACTION HANDLER
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().trim()) {
                case "PARK VEHICLE": doPark();    break;
                case "EXIT VEHICLE": doExit();    break;
                case "SEARCH PLATE": doSearch();  break;
                case "REFRESH VIEW": doRefresh(); break;
            }
        }

        private void doPark() {
            String plate = plateField.getText().trim().toUpperCase();
            String owner = ownerField.getText().trim();
            String phone = phoneField.getText().trim();

            if (plate.isEmpty() || owner.isEmpty() || phone.isEmpty()) {
                output("  Fill in plate, owner name and phone number.");
                return;
            }

            ParkingLocation loc = getSelectedLocation();
            if (loc == null) { output("  Select a location."); return; }

            if (loc.isFull()) {
                output("  " + loc.getName() + " is FULL.\n"
                        + "  Try another location.");
                return;
            }

            VehicleType   type    = VehicleType.valueOf(
                    vehicleTypeChoice.getSelectedItem());
            Vehicle       vehicle = new Vehicle(plate, owner, type, phone);
            ParkingTicket ticket  = parkingService.parkVehicle(vehicle,
                    loc.getLocationId());

            if (ticket == null) {
                output("  Parking failed. Location may be full.");
            } else {
                output(ticket.getSummary());
                plateField.setText("");
                ownerField.setText("");
                phoneField.setText("");
                doRefresh();
            }
        }

        private void doExit() {
            String plate = plateField.getText().trim().toUpperCase();
            if (plate.isEmpty()) {
                output("  Enter the registration plate to exit.");
                return;
            }

            ParkingTicket ticket = parkingService.exitVehicle(plate);

            if (ticket == null) {
                output("  Plate " + plate + " is not currently parked.\n"
                        + "  Check the plate and try again.");
                return;
            }

            MPesaDialog dialog = new MPesaDialog(this, ticket, mpesaService);
            dialog.setVisible(true);
            PaymentStatus result = dialog.getResult();

            if (result == PaymentStatus.CONFIRMED) {
                output(ticket.getSummary()
                        + "\n\n  PAYMENT CONFIRMED — KES " + (int) ticket.getAmountDue()
                        + "\n Safe journey.");
            } else {
                output(ticket.getSummary()
                        + "\n\n  PAYMENT UNRESOLVED — KES " + (int) ticket.getAmountDue()
                        + "\n  Collect cash or resolve with supervisor.");
            }

            plateField.setText("");
            doRefresh();
        }

        private void doSearch() {
            String plate = plateField.getText().trim().toUpperCase();
            if (plate.isEmpty()) {
                output("  Enter a plate number to search.");
                return;
            }

            ParkingTicket ticket = parkingService.searchVehicle(plate);

            if (ticket == null) {
                output("  SEARCH: " + plate + "\n"
                        + "  Not currently parked at any location.");
            } else {
                Vehicle  v   = ticket.getVehicle();
                ParkingLocation loc = ticket.getLocation();
                output("  FOUND: " + plate + "\n"
                        + "\n"
                        + "  Owner : " + v.getOwnerName() + "\n"
                        + "  Type : " + v.getVehicleType() + "\n"
                        + "  Location : " + loc.getName() + "\n"
                        + "  Area : " + loc.getArea() + "\n"
                        + "  Ticket : " + ticket.getTicketId() + "\n"
                        + "  Parked : " + ticket.getDuration() + " ago");
            }
        }

        private void doRefresh() {
            refreshCapacityPanel();
            refreshActiveTable();
            populateLocationDropdown();
            updateLocationStatus();
        }

        // ── HELPERS
        private void output(String text) { outputArea.setText(text); }
        private Label boldLbl(String text) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(200, 34));
            return b;
        }

        private String getCurrentTime() {
            return LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("EEE dd MMM  HH:mm"));
        }

    }
}