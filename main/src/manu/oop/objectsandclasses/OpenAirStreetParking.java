package manu.oop.objectsandclasses;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class OpenAirStreetParking {
    void main(String[] args) { new ParkingApp(); }
//     SMART PARKING KENYA — Pure Ticketing System
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
        private String phoneNumber;

        public Vehicle(String regPlate, String ownerName,
                       VehicleType vehicleType, String phoneNumber) {
            this.regPlate  = regPlate;
            this.ownerName = ownerName;
            this.vehicleType = vehicleType;
            this.phoneNumber = phoneNumber;
        }

        public String getRegPlate()  { return regPlate;  }
        public String  getOwnerName() { return ownerName; }
        public VehicleType getVehicleType() { return vehicleType; }
        public String getPhoneNumber() { return phoneNumber; }

        @Override
        public String toString() {
            return  vehicleType + regPlate + " — " + ownerName;
        }
    }

    class ParkingTicket {
        private String  ticketId;
        private Vehicle vehicle;
        private String location;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private double amountDue;

        private static int ticketCounter = 0;

        private static final double RATE_CAR = 100.0;
        private static final double RATE_MOTORCYCLE =  50.0;
        private static final double RATE_TRUCK = 200.0;
        private static final double RATE_MATATU = 150.0;

        public ParkingTicket(Vehicle vehicle, String location) {
            ticketCounter++;
            this.ticketId  = String.format("TKT-%04d", ticketCounter);
            this.vehicle = vehicle;
            this.location = location;
            this.entryTime = LocalDateTime.now();
            this.exitTime  = null;
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
                default: rate = RATE_CAR; break;
            }
            this.amountDue = Math.round(hours * rate);
            return amountDue;
        }

        public String getDuration() {
            LocalDateTime end  = exitTime != null ? exitTime : LocalDateTime.now();
            long mins = Duration.between(entryTime, end).toMinutes();
            return mins < 60 ? mins + " min" : (mins / 60) + "h " + (mins % 60) + "m";
        }

        public String getSummary() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return  "\n"
                    + " SMART PARKING KENYA — TICKET   \n"
                    + "\n"
                    + "  Ticket : " + ticketId + "\n"
                    + "  Location : " + location + "\n"
                    + "  Plate : " + vehicle.getRegPlate() + "\n"
                    + "  Owner : " + vehicle.getOwnerName() + "\n"
                    + "  Type : " + vehicle.getVehicleType() + "\n"
                    + "  Phone : " + vehicle.getPhoneNumber() + "\n"
                    + "  Entry : " + entryTime.format(fmt) + "\n"
                    + (exitTime != null
                    ? "  Exit : " + exitTime.format(fmt) + "\n"
                    + "  Duration : " + getDuration()          + "\n"
                    + "  Amount : KES " + (int) amountDue    + "\n"
                    : "  Status   : PARKED — " + getDuration() + "\n")
                    + "";
        }

        public String  getTicketId()  { return ticketId;  }
        public Vehicle getVehicle()   { return vehicle;   }
        public String  getLocation()  { return location;  }
        public LocalDateTime getEntryTime() { return entryTime; }
        public LocalDateTime getExitTime()  { return exitTime;  }
        public double getAmountDue() { return amountDue; }
    }

    class ParkingService {
        private ArrayList<ParkingTicket> activeTickets;
        public ParkingService() {
            this.activeTickets = new ArrayList<>();
        }

        public ParkingTicket parkVehicle(Vehicle vehicle, String location) {
            ParkingTicket ticket = new ParkingTicket(vehicle, location);
            activeTickets.add(ticket);
            return ticket;
        }

        public ParkingTicket exitVehicle(String regPlate) {
            ParkingTicket ticket = findActiveTicket(regPlate);
            if (ticket == null) return null;
            ticket.checkout();
            activeTickets.remove(ticket);
            return ticket;
        }

        public ParkingTicket searchVehicle(String regPlate) {
            return findActiveTicket(regPlate);
        }

        private ParkingTicket findActiveTicket(String regPlate) {
            for (ParkingTicket t : activeTickets) {
                if (t.getVehicle().getRegPlate().equalsIgnoreCase(regPlate))
                    return t;
            }
            return null;
        }
        public ArrayList<ParkingTicket> getActiveTickets() { return activeTickets; }
    }

    class MPesaPaymentService {
        private String shortCode;
        private String accountName;

        public MPesaPaymentService(String shortCode, String accountName) {
            this.shortCode   = shortCode;
            this.accountName = accountName;
        }

        public PaymentStatus stkPush(String phone, double amount, String ref) {
            System.out.println(" STK Push" + phone
                    + " | KES " + (int)amount + " | Ref: " + ref);
            return PaymentStatus.PENDING;
        }

        public PaymentStatus confirmPayment(String phone, double amount) {
            if (Math.random() < 0.90) {
                System.out.println("CONFIRMED — " + genCode());
                return PaymentStatus.CONFIRMED;
            }
            System.out.println("FAILED");
            return PaymentStatus.FAILED;
        }

        private String genCode() {
            String c = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++)
                sb.append(c.charAt((int)(Math.random() * c.length())));
            return sb.toString();
        }
    }

    class MPesaDialog extends Dialog implements ActionListener {

        private ParkingTicket ticket;
        private MPesaPaymentService mpesaService;
        private TextField phoneField;
        private Button confirmBtn, retryBtn, cancelBtn;
        private Label statusLabel;
        private TextArea logArea;
        private PaymentStatus result = PaymentStatus.FAILED;

        public MPesaDialog(Frame parent, ParkingTicket ticket,
                           MPesaPaymentService mpesaService) {
            super(parent, "MPesa Payment — Smart Parking Kenya", true);
            this.ticket = ticket;
            this.mpesaService = mpesaService;
            buildUI();
            sendPush();
            setSize(420, 450);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { cancel(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(245, 250, 245));

            Panel header = new Panel();
            header.setBackground(new Color(0, 130, 60));
            Label t = new Label("  MPesa STK Push Payment", Label.LEFT);
            t.setFont(new Font("SansSerif", Font.BOLD, 15));
            t.setForeground(Color.WHITE);
            t.setBackground(new Color(0, 130, 60));
            header.add(t);
            add(header, BorderLayout.NORTH);

            Panel center = new Panel(new GridBagLayout());
            center.setBackground(new Color(245, 250, 245));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 14, 5, 14);
            c.fill   = GridBagConstraints.HORIZONTAL;
            int row  = 0;

            row = row(center, c, row, "Plate:",    ticket.getVehicle().getRegPlate());
            row = row(center, c, row, "Owner:",    ticket.getVehicle().getOwnerName());
            row = row(center, c, row, "Location:", ticket.getLocation());
            row = row(center, c, row, "Duration:", ticket.getDuration());
            row = row(center, c, row, "Ticket:",   ticket.getTicketId());
            row = row(center, c, row, "Amount:",   "KES " + (int)ticket.getAmountDue());

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label div = new Label("─");
            div.setForeground(new Color(200, 200, 200));
            center.add(div, c);

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label pl = new Label("MPesa Phone:");
            pl.setFont(new Font("SansSerif", Font.BOLD, 12));
            center.add(pl, c);
            c.gridx = 1;
            phoneField = new TextField(ticket.getVehicle().getPhoneNumber());
            phoneField.setBackground(new Color(255, 255, 240));
            center.add(phoneField, c);
            row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            statusLabel = new Label("  Waiting for customer PIN...", Label.LEFT);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            statusLabel.setForeground(new Color(30, 100, 200));
            center.add(statusLabel, c);
            add(center, BorderLayout.CENTER);

            Panel bottom = new Panel(new BorderLayout(4, 4));
            bottom.setBackground(new Color(245, 250, 245));
            logArea = new TextArea("", 4, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
            logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            logArea.setEditable(false);
            logArea.setBackground(new Color(240, 248, 240));
            bottom.add(logArea, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            btns.setBackground(new Color(245, 250, 245));
            confirmBtn = mkBtn("CONFIRM PAYMENT", new Color(0, 140, 60));
            retryBtn   = mkBtn("RETRY",           new Color(140, 100, 0));
            cancelBtn  = mkBtn("CANCEL",          new Color(180, 50, 50));
            confirmBtn.addActionListener(this);
            retryBtn.addActionListener(this);
            cancelBtn.addActionListener(this);
            btns.add(confirmBtn); btns.add(retryBtn); btns.add(cancelBtn);
            bottom.add(btns, BorderLayout.SOUTH);
            add(bottom, BorderLayout.SOUTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "CONFIRM PAYMENT": confirm(); break;
                case "RETRY": retry();   break;
                case "CANCEL":  cancel();  break;
            }
        }

        private void sendPush() {
            log("STK Push " + ticket.getVehicle().getPhoneNumber());
            log("Amount KES " + (int)ticket.getAmountDue());
            log("Ref " + ticket.getTicketId());
            mpesaService.stkPush(ticket.getVehicle().getPhoneNumber(),
                    ticket.getAmountDue(), ticket.getTicketId());
        }

        private void retry() {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) { setStatus("Enter phone number.", Color.ORANGE); return; }
            mpesaService.stkPush(phone, ticket.getAmountDue(), ticket.getTicketId());
            log("\nRetry " + phone);
            setStatus("Resent — waiting...", new Color(30, 100, 200));
        }

        private void confirm() {
            confirmBtn.setEnabled(false); retryBtn.setEnabled(false);
            PaymentStatus s = mpesaService.confirmPayment(
                    phoneField.getText().trim(), ticket.getAmountDue());
            if (s == PaymentStatus.CONFIRMED) {
                result = PaymentStatus.CONFIRMED;
                setStatus("CONFIRMED — KES " + (int)ticket.getAmountDue(),
                        new Color(0, 130, 0));
                log("\nCONFIRMED!.");
                closeAfter(2000);
            } else {
                result = PaymentStatus.FAILED;
                setStatus("FAILED — retry or collect cash", new Color(180, 0, 0));
                log("\nFAILED.");
                confirmBtn.setEnabled(true); retryBtn.setEnabled(true);
            }
        }

        private void cancel() { log("\nCancelled."); dispose(); }

        private void closeAfter(int ms) {
            new Thread(() -> {
                try { Thread.sleep(ms); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                dispose();
            }).start();
        }

        private int row(Panel p, GridBagConstraints c,
                        int r, String lbl, String val) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            Label l = new Label(lbl);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            p.add(l, c);
            c.gridx = 1;
            Label v = new Label(val);
            v.setFont(new Font("SansSerif", Font.PLAIN, 12));
            v.setForeground(new Color(20, 20, 120));
            p.add(v, c);
            return r + 1;
        }

        private Button mkBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        private void setStatus(String text, Color color) {
            statusLabel.setText("  " + text);
            statusLabel.setForeground(color);
        }

        private void log(String text) { logArea.append(text + "\n"); }

        public PaymentStatus getResult() { return result; }
    }



    public class ParkingApp extends Frame implements ActionListener {
        private ParkingService  service;
        private MPesaPaymentService mpesa;

        private TextField plateField;
        private TextField ownerField;
        private TextField phoneField;
        private TextField locationField;
        private Choice typeChoice;

        private Button parkBtn, exitBtn, searchBtn, clearBtn;
        private TextArea activeTable;
        private TextArea outputArea;
        private Label countLabel;

        public ParkingApp() {
            super("Smart Parking Kenya : Pure Ticketing");
            service = new ParkingService();
            mpesa   = new MPesaPaymentService("247247", "Smart Parking Kenya");
            setupUI();
            refreshTable();
            setSize(950, 680);
            setResizable(true);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
        }

        private void setupUI() {
            setLayout(new BorderLayout(6, 6));
            setBackground(new Color(240, 244, 240));

            // HEADER
            Panel header = new Panel(new BorderLayout());
            header.setBackground(new Color(20, 80, 160));
            header.setPreferredSize(new Dimension(0, 50));
            Label title = new Label("   SMART PARKING KENYA  —  PURE TICKETING", Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setForeground(Color.WHITE);
            title.setBackground(new Color(20, 80, 160));
            header.add(title, BorderLayout.CENTER);
            Label sub = new Label("Attendant records, system bills   ", Label.RIGHT);
            sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
            sub.setForeground(new Color(180, 200, 255));
            sub.setBackground(new Color(20, 80, 160));
            header.add(sub, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // LEFT — input form
            Panel left = new Panel(new GridBagLayout());
            left.setBackground(new Color(248, 250, 255));
            left.setPreferredSize(new Dimension(230, 0));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 10, 5, 10);
            c.fill   = GridBagConstraints.HORIZONTAL;

            int r = 0;

            c.gridy = r++; c.gridx = 0; c.gridwidth = 2;
            Label sec = new Label("VEHICLE ENTRY", Label.LEFT);
            sec.setFont(new Font("SansSerif", Font.BOLD, 13));
            sec.setForeground(new Color(20, 80, 160));
            left.add(sec, c);

            // Fields
            r = addField(left, c, r, "Reg Plate:", plateField = new TextField());
            r = addField(left, c, r, "Owner Name:", ownerField = new TextField());
            r = addField(left, c, r, "Phone (MPesa):", phoneField = new TextField());

            // Location — the key field: completely free text
            c.gridy = r++; c.gridx = 0; c.gridwidth = 2;
            Label locLbl = new Label("Parked At (type anything):");
            locLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            left.add(locLbl, c);
            c.gridy = r++;
            locationField = new TextField("e.g. Quickmart Westlands or street names");
            locationField.setBackground(new Color(255, 255, 220));
            locationField.setFont(new Font("SansSerif", Font.PLAIN, 12));
            left.add(locationField, c);

            // Vehicle type
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            Label typeLbl = new Label("Vehicle Type:");
            typeLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            left.add(typeLbl, c);
            c.gridx = 1;
            typeChoice = new Choice();
            for (VehicleType vt : VehicleType.values()) typeChoice.add(vt.name());
            left.add(typeChoice, c);
            r++;

            // Spacer
            c.gridy = r++; c.gridx = 0; c.gridwidth = 2;
            left.add(new Label(" "), c);

            // Buttons
            parkBtn = mkBtn("PARK VEHICLE",   new Color(0,   130,  60));
            exitBtn = mkBtn("EXIT VEHICLE",   new Color(180,  45,  45));
            searchBtn = mkBtn("SEARCH PLATE",   new Color( 40,  90, 180));
            clearBtn = mkBtn("CLEAR FIELDS",   new Color(120, 120, 120));

            for (Button b : new Button[]{parkBtn, exitBtn, searchBtn, clearBtn}) {
                b.addActionListener(this);
                c.gridy = r++;
                left.add(b, c);
            }

            // Count badge
            c.gridy = r;
            countLabel = new Label("0 vehicles parked", Label.CENTER);
            countLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            countLabel.setForeground(new Color(20, 80, 160));
            left.add(countLabel, c);

            add(left, BorderLayout.WEST);

            // CENTER — live table
            Panel centerWrap = new Panel(new BorderLayout(4, 4));
            centerWrap.setBackground(new Color(240, 244, 240));
            Label tblTitle = new Label("  CURRENTLY PARKED VEHICLES", Label.LEFT);
            tblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            tblTitle.setForeground(new Color(20, 80, 160));
            centerWrap.add(tblTitle, BorderLayout.NORTH);

            activeTable = new TextArea("", 20, 70, TextArea.SCROLLBARS_VERTICAL_ONLY);
            activeTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            activeTable.setEditable(false);
            activeTable.setBackground(new Color(245, 248, 255));
            centerWrap.add(activeTable, BorderLayout.CENTER);
            add(centerWrap, BorderLayout.CENTER);

            // BOTTOM — output
            Panel bottomWrap = new Panel(new BorderLayout(4, 4));
            bottomWrap.setBackground(new Color(240, 244, 240));
            Label outTitle = new Label("  TICKET / RECEIPT", Label.LEFT);
            outTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            outTitle.setForeground(new Color(20, 80, 160));
            bottomWrap.add(outTitle, BorderLayout.NORTH);
            outputArea = new TextArea("", 8, 70, TextArea.SCROLLBARS_VERTICAL_ONLY);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            outputArea.setEditable(false);
            outputArea.setBackground(new Color(245, 248, 255));
            bottomWrap.add(outputArea, BorderLayout.CENTER);
            add(bottomWrap, BorderLayout.SOUTH);
        }

        // ── ACTIONS
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().trim()) {
                case "PARK VEHICLE": doPark();   break;
                case "EXIT VEHICLE": doExit();   break;
                case "SEARCH PLATE": doSearch(); break;
                case "CLEAR FIELDS": doClear();  break;
            }
        }

        private void doPark() {
            String plate = plateField.getText().trim().toUpperCase();
            String owner = ownerField.getText().trim();
            String phone = phoneField.getText().trim();
            String location = locationField.getText().trim();

            if (plate.isEmpty() || owner.isEmpty()
                    || phone.isEmpty() || location.isEmpty()) {
                output("  Fill in all fields including the location.");
                return;
            }

            VehicleType type    = VehicleType.valueOf(typeChoice.getSelectedItem());
            Vehicle vehicle = new Vehicle(plate, owner, type, phone);
            ParkingTicket ticket  = service.parkVehicle(vehicle, location);

            output(ticket.getSummary());
            clearInputFields();
            refreshTable();
        }

        private void doExit() {
            String plate = plateField.getText().trim().toUpperCase();
            if (plate.isEmpty()) { output("  Enter plate number to exit."); return; }

            ParkingTicket ticket = service.exitVehicle(plate);
            if (ticket == null) {
                output("  " + plate + " is not currently parked.");
                return;
            }

            MPesaDialog dialog = new MPesaDialog(this, ticket, mpesa);
            dialog.setVisible(true);
            PaymentStatus result = dialog.getResult();

            output(ticket.getSummary()
                    + (result == PaymentStatus.CONFIRMED
                    ? "\n\n  PAYMENT CONFIRMED — KES " + (int)ticket.getAmountDue()
                    + "\n Safe journey."
                    : "\n\n  PAYMENT UNRESOLVED — collect manually."));

            clearInputFields();
            refreshTable();
        }

        private void doSearch() {
            String plate = plateField.getText().trim().toUpperCase();
            if (plate.isEmpty()) { output("  Enter plate to search."); return; }

            ParkingTicket t = service.searchVehicle(plate);
            if (t == null) {
                output("  " + plate + " is not currently parked.");
            } else {
                output("  FOUND: " + plate + "\n"
                        + "  \n"
                        + " Owner : " + t.getVehicle().getOwnerName()  + "\n"
                        + " Type : " + t.getVehicle().getVehicleType() + "\n"
                        + " Location : " + t.getLocation() + "\n"
                        + " Ticket : " + t.getTicketId() + "\n"
                        + " Parked : " + t.getDuration() + " ago");
            }
        }

        private void doClear() { clearInputFields(); }
        // ── TABLE
        private void refreshTable() {
            ArrayList<ParkingTicket> tickets = service.getActiveTickets();
            countLabel.setText(tickets.size() + " vehicle(s) parked");

            if (tickets.isEmpty()) {
                activeTable.setText("  No vehicles currently parked.\n");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-10s %-12s %-16s %-24s %-10s\n",
                    "TICKET", "PLATE", "OWNER", "LOCATION", "DURATION"));
            sb.append("─".repeat(74)).append("\n");

            for (ParkingTicket t : tickets) {
                sb.append(String.format("%-10s %-12s %-16s %-24s %-10s\n",
                        t.getTicketId(),
                        t.getVehicle().getRegPlate(),
                        trunc(t.getVehicle().getOwnerName(), 15),
                        trunc(t.getLocation(), 23),
                        t.getDuration()));
            }
            sb.append("─".repeat(74)).append("\n");
            activeTable.setText(sb.toString());
        }

        // ── HELPERS
        private int addField(Panel p, GridBagConstraints c,
                             int r, String lbl, TextField tf) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            Label l = new Label(lbl);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            p.add(l, c);
            c.gridx = 1;
            p.add(tf, c);
            return r + 1;
        }

        private void clearInputFields() {
            plateField.setText("");
            ownerField.setText("");
            phoneField.setText("");
            locationField.setText("e.g. Quickmart Westlands");
        }

        private void output(String text) { outputArea.setText(text); }
        private String trunc(String s, int max)  {
            return s.length() > max ? s.substring(0, max - 1) + "…" : s;
        }

        private Button mkBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(200, 34));
            return b;
        }
    }
}
