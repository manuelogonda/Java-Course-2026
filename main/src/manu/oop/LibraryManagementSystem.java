package manu.oop;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
public class LibraryManagementSystem {
    void main(String[] args) { new LibraryApp(); }

    enum BookStatus {
        AVAILABLE,
        BORROWED,
        LOST
    }

    enum BorrowStatus {
        ACTIVE,
        RETURNED,
        OVERDUE
    }

    enum BookCategory {
        FICTION,
        TEXTBOOK,
        REFERENCE,
        NOVEL,
        SCIENCE,
        HISTORY,
        TECHNOLOGY,
        RELIGIOUS,
        OTHER
    }

    enum MemberType {
        STUDENT, STAFF, PUBLIC
    }


    class Book {

        private static int idCounter = 0;
        private int bookId;
        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private int yearPublished;
        private BookCategory category;
        private int totalCopies;
        private int availableCopies;

        public Book(String isbn, String title, String author,
                    String publisher, int yearPublished,
                    BookCategory category, int totalCopies) {
            idCounter++;
            this.bookId = idCounter;
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.yearPublished = yearPublished;
            this.category = category;
            this.totalCopies = totalCopies;
            this.availableCopies = totalCopies;
        }

        public boolean checkOut() {
            if (availableCopies <= 0) return false;
            availableCopies--;
            return true;
        }

        public void checkIn() {
            if (availableCopies < totalCopies) {
                availableCopies++;
            }
        }

        public boolean canBorrow() {
            return availableCopies > 0
                    && category != BookCategory.REFERENCE;
        }

        public boolean isAvailable() { return availableCopies > 0; }
        public String getCopiesDisplay() {
            return availableCopies + " / " + totalCopies;
        }

        public int getBookId() { return bookId; }
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getPublisher() { return publisher; }
        public int getYearPublished() { return yearPublished;}
        public BookCategory getCategory() { return category; }
        public int getTotalCopies() { return totalCopies; }
        public int getAvailableCopies() { return availableCopies; }

        public void setIsbn(String isbn) { this.isbn = isbn; }
        public void setTitle(String title) { this.title = title; }
        public void setAuthor(String author) { this.author = author;}
        public void setPublisher(String publisher) { this.publisher = publisher;}
        public void setYearPublished(int year){ this.yearPublished = year; }
        public void setCategory(BookCategory category)     { this.category      = category;      }
        public void setTotalCopies(int total) {
            int borrowed = totalCopies - availableCopies;
            this.totalCopies     = total;
            this.availableCopies = Math.max(0, total - borrowed);
        }

        @Override
        public String toString() {
            return title + " — " + author;
        }
    }


    class Borrower {

        private static int idCounter = 0;
        private String borrowerId;
        private String fullName;
        private String phone;
        private String email;
        private MemberType memberType;
        private LocalDate  registeredDate;

        public Borrower(String fullName, String phone,
                        String email, MemberType memberType) {
            idCounter++;
            this.borrowerId     = String.format("MBR-%04d", idCounter);
            this.fullName       = fullName;
            this.phone          = phone;
            this.email          = email;
            this.memberType     = memberType;
            this.registeredDate = LocalDate.now();
        }

        public String getBorrowerId(){ return borrowerId; }
        public String getFullName() { return fullName;}
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public MemberType getMemberType() { return memberType; }
        public LocalDate  getRegisteredDate(){ return registeredDate; }

        public void setFullName(String name) { this.fullName = name;}
        public void setPhone(String phone) { this.phone = phone;  }
        public void setEmail(String email) { this.email  = email;  }
        public void setMemberType(MemberType type) { this.memberType = type; }

        @Override
        public String toString() {
            return borrowerId + " — " + fullName + " (" + memberType + ")";
        }
    }

    class BorrowRecord {

        private static int recordCounter = 0;
        private static final int PENALTY_PER_DAY = 20;
        private static final int MAX_PENALTY = 500;
        private static final int BORROW_DAYS = 14;

        private String  recordId;
        private Book book;
        private Borrower borrower;
        private LocalDate borrowDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private BorrowStatus status;
        private double penaltyAmount;
        private boolean penaltyPaid;

        public BorrowRecord(Book book, Borrower borrower) {
            recordCounter++;
            this.recordId = String.format("REC-%04d", recordCounter);
            this.book = book;
            this.borrower = borrower;
            this.borrowDate = LocalDate.now();
            this.dueDate = LocalDate.now().plusDays(BORROW_DAYS);
            this.returnDate = null;
            this.status = BorrowStatus.ACTIVE;
            this.penaltyAmount = 0.0;
            this.penaltyPaid  = false;
        }

        public boolean isOverdue() {
            if (status == BorrowStatus.RETURNED) return false;
            return LocalDate.now().isAfter(dueDate);
        }

        public long getDaysOverdue() {
            if (!isOverdue()) return 0;
            return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }

        public double calculatePenalty() {
            long days = getDaysOverdue();
            if (days <= 0) return 0;
            double penalty = days * PENALTY_PER_DAY;
            return Math.min(penalty, MAX_PENALTY);
        }

        public double processReturn() {
            this.returnDate = LocalDate.now();
            this.penaltyAmount = calculatePenalty();
            this.status = BorrowStatus.RETURNED;
            this.book.checkIn();
            return penaltyAmount;
        }

        public void updateStatus() {
            if (status == BorrowStatus.ACTIVE && isOverdue()) {
                status = BorrowStatus.OVERDUE;
            }
        }

        public String getDueDateDisplay() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String base = dueDate.format(fmt);
            if (status == BorrowStatus.RETURNED) return base;
            long days = getDaysOverdue();
            if (days > 0) return base + " (" + days + " days late)";
            long remaining = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            if (remaining == 0) return "TODAY";
            if (remaining == 1) return "TOMORROW";
            if (remaining <= 3) return remaining + " days left";
            return base;
        }

        public String getRecordId() {
            return recordId;
        }

        public Book getBook() {
            return book;
        }

        public Borrower getBorrower() {
            return borrower;
        }
        public LocalDate getBorrowDate() {
            return borrowDate;
        }
        public LocalDate getDueDate() {
            return dueDate;
        }
        public LocalDate getReturnDate() {
            return returnDate;
        }
        public BorrowStatus getStatus() {
            return status;
        }
        public double getPenaltyAmount() {
            return penaltyAmount;
        }
        public boolean isPenaltyPaid() {
            return penaltyPaid;
        }
        public void markPenaltyPaid() {
            penaltyPaid = true;
        }
        @Override
        public String toString() {
            return recordId + " | " + book.getTitle()
                    + " | " + borrower.getFullName()
                    + " | Due: " + dueDate;
        }
    }





    class LibraryService {

        private ArrayList<Book> books;
        private ArrayList<Borrower> borrowers;
        private ArrayList<BorrowRecord> borrowRecords;

        public LibraryService() {
            books = new ArrayList<>();
            borrowers = new ArrayList<>();
            borrowRecords = new ArrayList<>();
            loadSampleData();
        }

        private void loadSampleData() {

            // Books — mix of Kenyan and international titles
            addBook("978-0-435-90550-3", "Things Fall Apart",
                    "Chinua Achebe", "Heinemann", 1958,
                    BookCategory.FICTION, 3);
            addBook("978-9966-25-360-1", "Weep Not Child",
                    "Ngugi wa Thiong'o", "Heinemann", 1964,
                    BookCategory.NOVEL, 2);
            addBook("978-0-13-468599-1", "Introduction to Java Programming",
                    "Y. Daniel Liang", "Pearson", 2019,
                    BookCategory.TEXTBOOK, 4);
            addBook("978-0-321-12521-7", "The Pragmatic Programmer",
                    "Andrew Hunt", "Addison-Wesley", 1999,
                    BookCategory.TECHNOLOGY, 2);
            addBook("978-9966-47-010-8", "A Grain of Wheat",
                    "Ngugi wa Thiong'o", "Heinemann", 1967,
                    BookCategory.NOVEL, 2);
            addBook("978-0-19-852663-6", "Oxford English Dictionary",
                    "Oxford Press", "Oxford University", 2010,
                    BookCategory.REFERENCE, 1);
            addBook("978-0-07-340264-9", "Microeconomics",
                    "Paul Samuelson", "McGraw-Hill", 2015,
                    BookCategory.TEXTBOOK, 5);
            addBook("978-9966-03-154-2", "Petals of Blood",
                    "Ngugi wa Thiong'o", "Heinemann", 1977,
                    BookCategory.FICTION, 2);
            addBook("978-0-06-112008-4", "To Kill a Mockingbird",
                    "Harper Lee", "HarperCollins", 1960,
                    BookCategory.FICTION, 3);
            addBook("978-0-7432-7356-5", "The Da Vinci Code",
                    "Dan Brown", "Doubleday", 2003,
                    BookCategory.NOVEL, 3);

            // Borrowers — Kenyan names
            addBorrower("Amina Wanjiru", "0712345678", "amina@uon.ac.ke", MemberType.STUDENT);
            addBorrower("Brian Otieno", "0722111222", "brian@gmail.com", MemberType.STUDENT);
            addBorrower("Caroline Kamau", "0733456789", "ckamau@strathmore.edu", MemberType.STUDENT);
            addBorrower("David Mwangi", "0744567890", "", MemberType.STAFF);
            addBorrower("Eunice Akinyi", "0755678901", "eunice@gmail.com", MemberType.PUBLIC);
            addBorrower("Francis Njoroge", "0766789012", "", MemberType.STUDENT);

            BorrowRecord r1 = borrowBook(1, 1);
            BorrowRecord r2 = borrowBook(3, 2);
            BorrowRecord r3 = borrowBook(7, 3);
            BorrowRecord r4 = borrowBook(2, 4);

            if (r2 != null) {
                simulateOverdue(r2, 5);
            }
            if (r3 != null) {
                simulateOverdue(r3, 2);
            }
        }

        private void simulateOverdue(BorrowRecord record, int daysOverdue) {
            record.updateStatus();
        }


        public Book addBook(String isbn, String title, String author,
                            String publisher, int year,
                            BookCategory category, int copies) {
            Book book = new Book(isbn, title, author, publisher,
                    year, category, copies);
            books.add(book);
            return book;
        }

        public boolean updateBook(int bookId, String isbn, String title,
                                  String author, String publisher, int year,
                                  BookCategory category, int copies) {
            Book book = getBookById(bookId);
            if (book == null) return false;
            book.setIsbn(isbn);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setYearPublished(year);
            book.setCategory(category);
            book.setTotalCopies(copies);
            return true;
        }

        public boolean deleteBook(int bookId) {
            Book book = getBookById(bookId);
            if (book == null) return false;
            for (BorrowRecord r : borrowRecords) {
                if (r.getBook().getBookId() == bookId
                        && r.getStatus() == BorrowStatus.ACTIVE) {
                    return false;
                }
            }
            books.remove(book);
            return true;
        }

        public Book getBookById(int bookId) {
            for (Book b : books) {
                if (b.getBookId() == bookId) return b;
            }
            return null;
        }

        public ArrayList<Book> searchBooks(String query) {
            ArrayList<Book> results = new ArrayList<>();
            String lower = query.toLowerCase().trim();
            for (Book b : books) {
                if (b.getTitle().toLowerCase().contains(lower)
                        || b.getAuthor().toLowerCase().contains(lower)
                        || b.getIsbn().contains(lower)) {
                    results.add(b);
                }
            }
            return results;
        }

        public Borrower addBorrower(String name, String phone,
                                    String email, MemberType type) {
            Borrower borrower = new Borrower(name, phone, email, type);
            borrowers.add(borrower);
            return borrower;
        }

        public boolean updateBorrower(String borrowerId, String name,
                                      String phone, String email, MemberType type) {
            Borrower b = getBorrowerById(borrowerId);
            if (b == null) return false;
            b.setFullName(name); b.setPhone(phone);
            b.setEmail(email);   b.setMemberType(type);
            return true;
        }

        public Borrower getBorrowerById(String id) {
            for (Borrower b : borrowers) {
                if (b.getBorrowerId().equalsIgnoreCase(id)) return b;
            }
            return null;
        }

        public ArrayList<Borrower> searchBorrowers(String query) {
            ArrayList<Borrower> results = new ArrayList<>();
            String lower = query.toLowerCase().trim();
            for (Borrower b : borrowers) {
                if (b.getFullName().toLowerCase().contains(lower)
                        || b.getBorrowerId().toLowerCase().contains(lower)
                        || b.getPhone().contains(lower)) {
                    results.add(b);
                }
            }
            return results;
        }

        public BorrowRecord borrowBook(int bookId, int borrowerIndex) {
            if (bookId < 1 || bookId > books.size()) return null;
            Book book = getBookById(bookId);
            if (book == null || !book.canBorrow()) return null;

            if (borrowerIndex < 0 || borrowerIndex >= borrowers.size()) return null;
            Borrower borrower = borrowers.get(borrowerIndex);

            for (BorrowRecord r : borrowRecords) {
                if (r.getBook().getBookId() == bookId
                        && r.getBorrower().getBorrowerId().equals(borrower.getBorrowerId())
                        && r.getStatus() != BorrowStatus.RETURNED) {
                    return null;
                }
            }

            boolean checkedOut = book.checkOut();
            if (!checkedOut) return null;

            BorrowRecord record = new BorrowRecord(book, borrower);
            borrowRecords.add(record);
            return record;
        }


        public BorrowRecord borrowBook(Book book, Borrower borrower) {
            if (book == null || !book.canBorrow()) return null;
            if (borrower == null) return null;

            for (BorrowRecord r : borrowRecords) {
                if (r.getBook().getBookId() == book.getBookId()
                        && r.getBorrower().getBorrowerId()
                        .equals(borrower.getBorrowerId())
                        && r.getStatus() != BorrowStatus.RETURNED) {
                    return null;
                }
            }

            if (!book.checkOut()) return null;
            BorrowRecord record = new BorrowRecord(book, borrower);
            borrowRecords.add(record);
            return record;
        }


        public double returnBook(BorrowRecord record) {
            if (record == null) return 0;
            if (record.getStatus() == BorrowStatus.RETURNED) return 0;
            return record.processReturn();
        }

        public void refreshStatuses() {
            for (BorrowRecord r : borrowRecords) r.updateStatus();
        }

        public ArrayList<BorrowRecord> getActiveRecords() {
            refreshStatuses();
            ArrayList<BorrowRecord> active = new ArrayList<>();
            for (BorrowRecord r : borrowRecords) {
                if (r.getStatus() != BorrowStatus.RETURNED) active.add(r);
            }
            return active;
        }

        public ArrayList<BorrowRecord> getOverdueRecords() {
            refreshStatuses();
            ArrayList<BorrowRecord> overdue = new ArrayList<>();
            for (BorrowRecord r : borrowRecords) {
                if (r.isOverdue()) overdue.add(r);
            }
            return overdue;
        }

        public ArrayList<BorrowRecord> getBorrowerHistory(String borrowerId) {
            ArrayList<BorrowRecord> history = new ArrayList<>();
            for (BorrowRecord r : borrowRecords) {
                if (r.getBorrower().getBorrowerId().equals(borrowerId))
                    history.add(r);
            }
            return history;
        }

        public ArrayList<BorrowRecord> getActiveRecordsForBorrower(String borrowerId){
            ArrayList<BorrowRecord> active = new ArrayList<>();
            for (BorrowRecord r : borrowRecords) {
                if (r.getBorrower().getBorrowerId().equals(borrowerId)
                        && r.getStatus() != BorrowStatus.RETURNED)
                    active.add(r);
            }
            return active;
        }

        public int getTotalBooks() {
            return books.size();
        }

        public int getTotalBorrowers() {
            return borrowers.size();
        }

        public int getActiveCount() {
            return getActiveRecords().size();
        }

        public int getOverdueCount() {
            return getOverdueRecords().size();
        }

        public double getTotalPendingPenalties() {
            double total = 0;
            for (BorrowRecord r : getOverdueRecords()) {
                total += r.calculatePenalty();
            }
            return total;
        }

        public ArrayList<Book> getAllBooks() {
            return books;
        }

        public ArrayList<Borrower> getAllBorrowers() {
            return borrowers;
        }

        public ArrayList<BorrowRecord> getAllRecords() {
            return borrowRecords;
        }
    }

    class BookFormDialog extends Dialog implements ActionListener {

        private LibraryService service;
        private Book           bookToEdit;
        private boolean        saved = false;

        private TextField titleField, authorField, isbnField;
        private TextField publisherField, yearField, copiesField;
        private Choice categoryChoice;
        private Button saveBtn, cancelBtn;
        private Label errorLabel;

        public BookFormDialog(Frame parent, Book bookToEdit,
                              LibraryService service) {
            super(parent, bookToEdit == null ? "Add New Book" : "Edit Book", true);
            this.bookToEdit = bookToEdit;
            this.service = service;
            buildUI();
            if (bookToEdit != null) prefill();
            setSize(460, 460);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 252));

            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 70, 140));
            Label hl = new Label(
                    bookToEdit == null ? "  Add New Book" : "  Edit Book",
                    Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE);
            hl.setBackground(new Color(30, 70, 140));
            hdr.add(hl);
            add(hdr, BorderLayout.NORTH);

            Panel form = new Panel(new GridBagLayout());
            form.setBackground(new Color(252, 252, 252));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 14, 6, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            int row = 0;

            row = fRow(form, c, row, "Title *", titleField = new TextField());
            row = fRow(form, c, row, "Author *", authorField = new TextField());
            row = fRow(form, c, row, "ISBN", isbnField = new TextField());
            row = fRow(form, c, row, "Publisher", publisherField = new TextField());
            row = fRow(form, c, row, "Year Published", yearField = new TextField());
            row = fRow(form, c, row, "Total Copies *", copiesField = new TextField("1"));

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            form.add(bLbl("Category"), c); c.gridx = 1;
            categoryChoice = new Choice();
            for (BookCategory cat : BookCategory.values()) categoryChoice.add(cat.name());
            form.add(categoryChoice, c); row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            errorLabel = new Label("", Label.CENTER);
            errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            errorLabel.setForeground(new Color(200, 40, 40));
            form.add(errorLabel, c);
            add(form, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            btns.setBackground(new Color(252, 252, 252));
            saveBtn   = mkBtn(bookToEdit == null ? "ADD BOOK" : "SAVE",
                    new Color(30, 70, 140));
            cancelBtn = mkBtn("CANCEL", new Color(160, 160, 160));
            saveBtn.addActionListener(this); cancelBtn.addActionListener(this);
            btns.add(saveBtn); btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        private void prefill() {
            titleField.setText(bookToEdit.getTitle());
            authorField.setText(bookToEdit.getAuthor());
            isbnField.setText(bookToEdit.getIsbn());
            publisherField.setText(bookToEdit.getPublisher());
            yearField.setText(String.valueOf(bookToEdit.getYearPublished()));
            copiesField.setText(String.valueOf(bookToEdit.getTotalCopies()));
            categoryChoice.select(bookToEdit.getCategory().name());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) { dispose(); return; }
            save();
        }

        private void save() {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty()) {
                errorLabel.setText("Title is required.");
                return;
            }
            if (author.isEmpty()) {
                errorLabel.setText("Author is required.");
                return;
            }

            int copies = 1, year = 0;
            try {
                copies = Integer.parseInt(copiesField.getText().trim());
                if (copies < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Copies must be a positive number.");
                return;
            }
            try {
                String yr = yearField.getText().trim();
                if (!yr.isEmpty()) year = Integer.parseInt(yr);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Year must be a number.");
                return;
            }

            String isbn = isbnField.getText().trim();
            String publisher = publisherField.getText().trim();
            BookCategory category = BookCategory.valueOf(
                    categoryChoice.getSelectedItem());

            if (bookToEdit == null) {
                service.addBook(isbn, title, author, publisher,
                        year, category, copies);
            } else {
                service.updateBook(bookToEdit.getBookId(), isbn, title, author,
                        publisher, year, category, copies);
            }
            saved = true;
            dispose();
        }

        private int fRow(Panel p, GridBagConstraints c,
                         int r, String lbl, TextField tf) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            p.add(bLbl(lbl), c); c.gridx = 1; p.add(tf, c); return r + 1;
        }

        private Label  bLbl(String t) {
            Label l = new Label(t); l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        public boolean wasSaved() { return saved; }
    }

    class BorrowerFormDialog extends Dialog implements ActionListener {

        private LibraryService service;
        private boolean saved = false;

        private TextField nameField, phoneField, emailField;
        private Choice typeChoice;
        private Button saveBtn, cancelBtn;
        private Label errorLabel;

        public BorrowerFormDialog(Frame parent, LibraryService service) {
            super(parent, "Register New Member", true);
            this.service = service;
            buildUI();
            setSize(420, 340);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 252));

            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 100, 60));
            Label hl = new Label("  Register New Member", Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE);
            hl.setBackground(new Color(30, 100, 60));
            hdr.add(hl);
            add(hdr, BorderLayout.NORTH);

            Panel form = new Panel(new GridBagLayout());
            form.setBackground(new Color(252, 252, 252));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(7, 14, 7, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            int row = 0;

            row = fRow(form, c, row, "Full Name *", nameField = new TextField());
            row = fRow(form, c, row, "Phone *", phoneField = new TextField());
            row = fRow(form, c, row, "Email", emailField = new TextField("optional"));

            c.gridy = row;
            c.gridx = 0;
            c.gridwidth = 1;
            form.add(bLbl("Member Type"), c);
            c.gridx = 1;
            typeChoice = new Choice();
            for (MemberType mt : MemberType.values()) typeChoice.add(mt.name());
            form.add(typeChoice, c);
            row++;

            c.gridy = row;
            c.gridx = 0;
            c.gridwidth = 2;
            errorLabel = new Label("", Label.CENTER);
            errorLabel.setForeground(new Color(200, 40, 40));
            form.add(errorLabel, c);
            add(form, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            btns.setBackground(new Color(252, 252, 252));
            saveBtn = mkBtn("REGISTER", new Color(30, 100, 60));
            cancelBtn = mkBtn("CANCEL", new Color(160, 160, 160));
            saveBtn.addActionListener(this);
            cancelBtn.addActionListener(this);
            btns.add(saveBtn);
            btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) { dispose(); return; }
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty())  { errorLabel.setText("Name is required.");  return; }
            if (phone.isEmpty()) { errorLabel.setText("Phone is required."); return; }
            String email = emailField.getText().trim();
            if (email.equals("optional")) email = "";
            MemberType type = MemberType.valueOf(typeChoice.getSelectedItem());
            service.addBorrower(name, phone, email, type);
            saved = true; dispose();
        }

        private int fRow(Panel p, GridBagConstraints c,
                         int r, String lbl, TextField tf) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            p.add(bLbl(lbl), c); c.gridx = 1; p.add(tf, c); return r + 1;
        }

        private Label  bLbl(String t) {
            Label l = new Label(t); l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        public boolean wasSaved() { return saved; }
    }

    class BorrowDialog extends Dialog implements ActionListener {

        private LibraryService service;
        private Book book;
        private boolean borrowed = false;
        private BorrowRecord createdRecord = null;

        private java.awt.List borrowerList;
        private TextField searchField;
        private Label bookInfoLabel;
        private Label statusLabel;
        private Button borrowBtn, cancelBtn;

        private ArrayList<Borrower> displayedBorrowers;

        public BorrowDialog(Frame parent, Book book, LibraryService service) {
            super(parent, "Borrow Book", true);
            this.book    = book;
            this.service = service;
            buildUI();
            refreshBorrowers("");
            setSize(500, 420);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 252));

            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 70, 140));
            Label hl = new Label("  Borrow Book", Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE); hl.setBackground(new Color(30, 70, 140));
            hdr.add(hl); add(hdr, BorderLayout.NORTH);

            Panel center = new Panel(new BorderLayout(6, 6));
            center.setBackground(new Color(252, 252, 252));

            Panel bookInfo = new Panel(new GridLayout(2, 1));
            bookInfo.setBackground(new Color(240, 245, 255));
            bookInfoLabel = new Label("  Book: " + book.getTitle()
                    + " by " + book.getAuthor(), Label.LEFT);
            bookInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            Label copiesInfo = new Label("  Available copies: "
                    + book.getAvailableCopies() + " / " + book.getTotalCopies()
                    + "     Due date: " + LocalDate.now().plusDays(14)
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    Label.LEFT);
            copiesInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
            copiesInfo.setForeground(new Color(60, 60, 60));
            bookInfo.add(bookInfoLabel); bookInfo.add(copiesInfo);
            center.add(bookInfo, BorderLayout.NORTH);

            Panel searchRow = new Panel(new BorderLayout(4, 0));
            searchRow.setBackground(new Color(252, 252, 252));
            Label sLbl = new Label("Search member: ");
            sLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            searchField = new TextField();
            searchField.addTextListener(e ->
                    refreshBorrowers(searchField.getText().trim()));
            searchRow.add(sLbl, BorderLayout.WEST);
            searchRow.add(searchField, BorderLayout.CENTER);
            center.add(searchRow, BorderLayout.CENTER);

            borrowerList = new java.awt.List(8, false);
            borrowerList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            center.add(borrowerList, BorderLayout.SOUTH);
            add(center, BorderLayout.CENTER);

            Panel bot = new Panel(new BorderLayout(8, 8));
            bot.setBackground(new Color(252, 252, 252));
            statusLabel = new Label("  Select a member and click BORROW",
                    Label.LEFT);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            statusLabel.setForeground(new Color(80, 80, 80));
            bot.add(statusLabel, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            btns.setBackground(new Color(252, 252, 252));
            borrowBtn = mkBtn("BORROW",  new Color(30, 70, 140));
            cancelBtn = mkBtn("CANCEL",  new Color(160, 160, 160));
            borrowBtn.addActionListener(this); cancelBtn.addActionListener(this);
            btns.add(borrowBtn); btns.add(cancelBtn);
            bot.add(btns, BorderLayout.SOUTH);
            add(bot, BorderLayout.SOUTH);
        }

        private void refreshBorrowers(String query) {
            borrowerList.removeAll();
            displayedBorrowers = query.isEmpty()
                    ? service.getAllBorrowers()
                    : service.searchBorrowers(query);
            for (Borrower b : displayedBorrowers) {
                borrowerList.add(String.format("%-12s %-22s %-14s %s",
                        b.getBorrowerId(), b.getFullName(),
                        b.getPhone(), b.getMemberType()));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) { dispose(); return; }
            int idx = borrowerList.getSelectedIndex();
            if (idx < 0 || idx >= displayedBorrowers.size()) {
                setStatus("Select a member first.", new Color(180, 0, 0)); return;
            }
            Borrower borrower = displayedBorrowers.get(idx);
            createdRecord = service.borrowBook(book, borrower);
            if (createdRecord != null) {
                borrowed = true;
                setStatus("Borrowed! Due: "
                                + createdRecord.getDueDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        new Color(0, 130, 0));
                closeAfter(1500);
            } else {
                setStatus("Could not borrow — already borrowed or unavailable.",
                        new Color(180, 0, 0));
            }
        }

        private void closeAfter(int ms) {
            new Thread(() -> {
                try { Thread.sleep(ms); }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                dispose();
            }).start();
        }

        private void setStatus(String t, Color c) {
            statusLabel.setText("  " + t); statusLabel.setForeground(c);
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        public boolean       wasBorrowed()    { return borrowed;       }
        public BorrowRecord  getCreatedRecord(){ return createdRecord;  }
    }


    class ReturnDialog extends Dialog implements ActionListener {

        private LibraryService service;
        private BorrowRecord record;
        private boolean returned = false;

        private Label  penaltyLabel, statusLabel;
        private Button confirmBtn, cancelBtn;

        public ReturnDialog(Frame parent, BorrowRecord record,
                            LibraryService service) {
            super(parent, "Return Book", true);
            this.record  = record;
            this.service = service;
            buildUI();
            setSize(440, 360);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(Color.WHITE);

            boolean overdue = record.isOverdue();
            Color   hdrColor = overdue ? new Color(180, 50, 50) : new Color(30, 100, 60);

            Panel hdr = new Panel();
            hdr.setBackground(hdrColor);
            Label hl = new Label(overdue ? "  Return — OVERDUE" : "  Return Book",
                    Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE); hl.setBackground(hdrColor);
            hdr.add(hl); add(hdr, BorderLayout.NORTH);

            Panel details = new Panel(new GridBagLayout());
            details.setBackground(Color.WHITE);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(7, 18, 7, 18);
            c.fill   = GridBagConstraints.HORIZONTAL;
            int row  = 0;

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            row = dRow(details, c, row, "Book:",
                    record.getBook().getTitle());
            row = dRow(details, c, row, "Author:",
                    record.getBook().getAuthor());
            row = dRow(details, c, row, "Borrower:",
                    record.getBorrower().getFullName());
            row = dRow(details, c, row, "Borrowed on:",
                    record.getBorrowDate().format(fmt));
            row = dRow(details, c, row, "Due date:",
                    record.getDueDate().format(fmt));

            if (overdue) {
                long days = record.getDaysOverdue();
                double penalty = record.calculatePenalty();

                c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
                Label divider = new Label(
                        "-----");
                divider.setForeground(new Color(200, 200, 200));
                details.add(divider, c);

                c.gridy = row; c.gridx = 0; c.gridwidth = 1;
                Label dl = new Label("Days overdue:");
                dl.setFont(new Font("SansSerif", Font.BOLD, 13));
                dl.setForeground(new Color(180, 50, 50));
                details.add(dl, c);
                c.gridx = 1;
                Label dv = new Label(days + " days");
                dv.setFont(new Font("SansSerif", Font.BOLD, 13));
                dv.setForeground(new Color(180, 50, 50));
                details.add(dv, c); row++;

                c.gridy = row; c.gridx = 0; c.gridwidth = 1;
                Label pl = new Label("Penalty (KES 20/day):");
                pl.setFont(new Font("SansSerif", Font.BOLD, 14));
                pl.setForeground(new Color(180, 50, 50));
                details.add(pl, c);
                c.gridx = 1;
                penaltyLabel = new Label("KES " + (int) penalty);
                penaltyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                penaltyLabel.setForeground(new Color(180, 50, 50));
                details.add(penaltyLabel, c); row++;
            } else {
                c.gridy = row; c.gridx = 0; c.gridwidth = 2;
                Label ok = new Label(
                        "  Returned on time — no penalty. Asante sana!");
                ok.setFont(new Font("SansSerif", Font.ITALIC, 12));
                ok.setForeground(new Color(0, 130, 0));
                details.add(ok, c); row++;
            }

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            statusLabel = new Label("", Label.CENTER);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            details.add(statusLabel, c);
            add(details, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 14, 10));
            btns.setBackground(Color.WHITE);
            confirmBtn = mkBtn(overdue ? "CONFIRM RETURN + COLLECT PENALTY"
                    : "CONFIRM RETURN", new Color(30, 100, 60));
            cancelBtn  = mkBtn("CANCEL", new Color(160, 160, 160));
            confirmBtn.addActionListener(this); cancelBtn.addActionListener(this);
            btns.add(confirmBtn); btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) { dispose(); return; }
            double penalty = service.returnBook(record);
            returned = true;
            confirmBtn.setEnabled(false);
            if (penalty > 0) {
                statusLabel.setText("Returned. KES " + (int) penalty
                        + " penalty collected.");
                statusLabel.setForeground(new Color(30, 100, 60));
            } else {
                statusLabel.setText("Returned on time. Karibu tena!");
                statusLabel.setForeground(new Color(30, 100, 60));
            }
            closeAfter(1800);
        }

        private int dRow(Panel p, GridBagConstraints c,
                         int r, String lbl, String val) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            Label l = new Label(lbl); l.setFont(new Font("SansSerif", Font.BOLD, 12));
            l.setForeground(new Color(80, 80, 80)); p.add(l, c);
            c.gridx = 1;
            Label v = new Label(val); v.setFont(new Font("SansSerif", Font.PLAIN, 12));
            p.add(v, c); return r + 1;
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        private void closeAfter(int ms) {
            new Thread(() -> {
                try { Thread.sleep(ms); }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                dispose();
            }).start();
        }

        public boolean wasReturned() { return returned; }
    }



    public class LibraryApp extends Frame implements ActionListener {

        private LibraryService service;

        private Button booksTab, borrowersTab, borrowedTab, overdueTab;
        private Panel  booksPanel, borrowersPanel, borrowedPanel, overduePanel;
        private Panel  activePanel;
        private Panel  contentArea;
        private java.awt.List booksList;
        private TextField     bookSearchField;
        private ArrayList<Book> displayedBooks = new ArrayList<>();

        private java.awt.List borrowersList;
        private TextField     borrowerSearchField;
        private ArrayList<Borrower> displayedBorrowers = new ArrayList<>();

        private java.awt.List borrowedList;
        private ArrayList<BorrowRecord> displayedBorrowed = new ArrayList<>();

        private java.awt.List overdueList;
        private ArrayList<BorrowRecord> displayedOverdue = new ArrayList<>();

        private Label totalBooksLbl, totalBorrowersLbl;
        private Label activeLbl, overdueLbl, penaltyLbl;

        private static final Color BRAND = new Color(30, 70, 140);
        private static final Color RED = new Color(200, 40, 40);
        private static final Color GREEN = new Color(0, 130, 60);
        private static final Color AMBER = new Color(190, 120, 0);

        public LibraryApp() {
            super("Library Management System");
            service = new LibraryService();
            buildUI();
            showTab(booksTab, booksPanel);
            refreshAll();
            setSize(1000, 700);
            setResizable(true);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(0, 0));
            setBackground(new Color(240, 243, 250));

            add(buildHeader(), BorderLayout.NORTH);
            add(buildTabBar(), BorderLayout.CENTER);
        }

        private Panel buildHeader() {
            Panel hdr = new Panel(new BorderLayout());
            hdr.setBackground(BRAND);
            hdr.setPreferredSize(new Dimension(0, 56));

            Label title = new Label("LIBRARY MANAGEMENT SYSTEM",
                    Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setForeground(Color.WHITE);
            title.setBackground(BRAND);
            hdr.add(title, BorderLayout.CENTER);

            Label sub = new Label("some top books to read", Label.RIGHT);
            sub.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sub.setForeground(new Color(160, 190, 255));
            sub.setBackground(BRAND);
            hdr.add(sub, BorderLayout.EAST);
            return hdr;
        }

        private Panel buildTabBar() {
            Panel wrapper = new Panel(new BorderLayout(0, 0));
            wrapper.setBackground(new Color(240, 243, 250));

            Panel tabRow = new Panel(new BorderLayout());
            tabRow.setBackground(new Color(20, 20, 20));
            tabRow.setPreferredSize(new Dimension(0, 38));

            Panel tabs = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tabs.setBackground(new Color(20, 20, 20));
            booksTab = tabBtn("  BOOKS  ", true);
            borrowersTab = tabBtn("  BORROWERS  ", false);
            borrowedTab = tabBtn("  BORROWED  ", false);
            overdueTab = tabBtn("  OVERDUE  ", false);

            for (Button b : new Button[]{
                    booksTab, borrowersTab, borrowedTab, overdueTab}) {
                b.addActionListener(this);
                tabs.add(b);
            }
            tabRow.add(tabs, BorderLayout.WEST);

            Panel sumBar = new Panel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
            sumBar.setBackground(new Color(20, 20, 20));
            totalBooksLbl = sLbl("0 books", new Color(160, 200, 255));
            totalBorrowersLbl = sLbl("0 members", new Color(160, 200, 255));
            activeLbl = sLbl("0 borrowed", new Color(255, 210, 100));
            overdueLbl = sLbl("0 overdue", new Color(255, 120, 120));
            penaltyLbl = sLbl("KES 0", new Color(255, 160, 80));

            for (Label l : new Label[]{totalBooksLbl, totalBorrowersLbl,
                    activeLbl, overdueLbl, penaltyLbl}) {
                sumBar.add(l);
            }
            tabRow.add(sumBar, BorderLayout.EAST);
            wrapper.add(tabRow, BorderLayout.NORTH);

            // Content area — panels swap here
            contentArea = new Panel(new BorderLayout(0, 0));
            contentArea.setBackground(new Color(240, 243, 250));

            booksPanel     = buildBooksPanel();
            borrowersPanel = buildBorrowersPanel();
            borrowedPanel  = buildBorrowedPanel();
            overduePanel   = buildOverduePanel();

            wrapper.add(contentArea, BorderLayout.CENTER);
            return wrapper;
        }

        private Panel buildBooksPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 243, 250));

            Panel top = new Panel(new BorderLayout(6, 0));
            top.setBackground(new Color(240, 243, 250));

            Panel searchRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            searchRow.setBackground(new Color(240, 243, 250));
            Label sl = new Label("Search:");
            sl.setFont(new Font("SansSerif", Font.BOLD, 12));
            bookSearchField = new TextField(22);
            bookSearchField.addTextListener(e -> refreshBooksPanel());
            Button clrBtn = new Button("X");
            clrBtn.addActionListener(e -> {
                bookSearchField.setText(""); refreshBooksPanel();
            });
            searchRow.add(sl); searchRow.add(bookSearchField); searchRow.add(clrBtn);

            Panel btnRow = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
            btnRow.setBackground(new Color(240, 243, 250));
            Button addBook = aBtn("+ ADD BOOK", GREEN);
            addBook.addActionListener(e -> doAddBook());
            btnRow.add(addBook);

            top.add(searchRow, BorderLayout.WEST);
            top.add(btnRow, BorderLayout.EAST);
            p.add(top, BorderLayout.NORTH);

            Panel colHdr = colHeader(String.format(
                    "  %-5s %-36s %-22s %-12s %-8s %s",
                    "ID", "TITLE", "AUTHOR", "CATEGORY", "COPIES", "STATUS"));
            p.add(colHdr, BorderLayout.CENTER);

            Panel listArea = new Panel(new BorderLayout(0, 4));
            listArea.setBackground(new Color(240, 243, 250));

            booksList = new java.awt.List(14, false);
            booksList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            booksList.setBackground(new Color(255, 255, 254));
            listArea.add(booksList, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(232, 235, 245));
            Button borrowBtn = aBtn("BORROW",     BRAND);
            Button editBtn   = aBtn("EDIT BOOK",  new Color(80, 80, 80));
            Button delBtn    = aBtn("DELETE BOOK",RED);
            borrowBtn.addActionListener(e -> doBorrowSelected());
            editBtn.addActionListener(e -> doEditBook());
            delBtn.addActionListener(e -> doDeleteBook());
            btns.add(borrowBtn); btns.add(editBtn); btns.add(delBtn);
            listArea.add(btns, BorderLayout.SOUTH);

            Panel combined = new Panel(new BorderLayout(0,0));
            combined.setBackground(new Color(240, 243, 250));
            combined.add(colHdr, BorderLayout.NORTH);
            combined.add(listArea, BorderLayout.CENTER);
            p.remove(colHdr);
            p.add(combined, BorderLayout.CENTER);

            return p;
        }

        private Panel buildBorrowersPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 243, 250));

            Panel top = new Panel(new BorderLayout(6, 0));
            top.setBackground(new Color(240, 243, 250));

            Panel searchRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            searchRow.setBackground(new Color(240, 243, 250));
            Label sl = new Label("Search:");
            sl.setFont(new Font("SansSerif", Font.BOLD, 12));
            borrowerSearchField = new TextField(22);
            borrowerSearchField.addTextListener(e -> refreshBorrowersPanel());
            Button clr = new Button("X");
            clr.addActionListener(e -> {
                borrowerSearchField.setText(""); refreshBorrowersPanel();
            });
            searchRow.add(sl); searchRow.add(borrowerSearchField); searchRow.add(clr);

            Panel btnRow = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
            btnRow.setBackground(new Color(240, 243, 250));
            Button addMbr = aBtn("+ REGISTER MEMBER", GREEN);
            addMbr.addActionListener(e -> doAddBorrower());
            btnRow.add(addMbr);
            top.add(searchRow, BorderLayout.WEST);
            top.add(btnRow, BorderLayout.EAST);
            p.add(top, BorderLayout.NORTH);

            Panel colHdr = colHeader(String.format(
                    "  %-12s %-24s %-16s %-10s %s",
                    "ID", "FULL NAME", "PHONE", "TYPE", "BORROWED / OVERDUE"));
            borrowersList = new java.awt.List(14, false);
            borrowersList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            borrowersList.setBackground(new Color(255, 255, 254));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(232, 235, 245));
            Button histBtn   = aBtn("VIEW HISTORY",   BRAND);
            Button returnBtn = aBtn("RETURN BOOK",    new Color(30, 120, 60));
            histBtn.addActionListener(e -> doViewHistory());
            returnBtn.addActionListener(e -> doReturnFromBorrower());
            btns.add(histBtn); btns.add(returnBtn);

            Panel combined = new Panel(new BorderLayout(0,0));
            combined.setBackground(new Color(240, 243, 250));
            combined.add(colHdr, BorderLayout.NORTH);
            Panel listArea = new Panel(new BorderLayout(0,4));
            listArea.add(borrowersList, BorderLayout.CENTER);
            listArea.add(btns, BorderLayout.SOUTH);
            combined.add(listArea, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }

        private Panel buildBorrowedPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 243, 250));

            Label info = new Label(
                    "   All currently borrowed books (ACTIVE + OVERDUE)",
                    Label.LEFT);
            info.setFont(new Font("SansSerif", Font.BOLD, 12));
            info.setForeground(BRAND);
            p.add(info, BorderLayout.NORTH);

            Panel colHdr = colHeader(String.format(
                    "  %-10s %-30s %-20s %-14s %s",
                    "RECORD", "BOOK TITLE", "BORROWER", "DUE DATE", "STATUS"));
            borrowedList = new java.awt.List(14, false);
            borrowedList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            borrowedList.setBackground(new Color(255, 255, 254));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(232, 235, 245));
            Button retBtn = aBtn("RETURN SELECTED", GREEN);
            retBtn.addActionListener(e -> doReturnFromBorrowed());
            btns.add(retBtn);

            Panel combined = new Panel(new BorderLayout(0,0));
            combined.add(colHdr, BorderLayout.NORTH);
            Panel listArea = new Panel(new BorderLayout(0,4));
            listArea.add(borrowedList, BorderLayout.CENTER);
            listArea.add(btns, BorderLayout.SOUTH);
            combined.add(listArea, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }

        private Panel buildOverduePanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 243, 250));

            Label info = new Label(
                    "   Overdue books — penalty: KES 20 per day | Max KES 500",
                    Label.LEFT);
            info.setFont(new Font("SansSerif", Font.BOLD, 12));
            info.setForeground(RED);
            p.add(info, BorderLayout.NORTH);

            Panel colHdr = colHeader(String.format(
                    "  %-10s %-28s %-20s %-12s %-12s %s",
                    "RECORD", "BOOK TITLE", "BORROWER",
                    "DUE DATE", "DAYS LATE", "PENALTY"));
            overdueList = new java.awt.List(14, false);
            overdueList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            overdueList.setBackground(new Color(255, 248, 248));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(232, 235, 245));
            Button retBtn = aBtn("RETURN + COLLECT PENALTY", RED);
            retBtn.addActionListener(e -> doReturnOverdue());
            btns.add(retBtn);

            Panel combined = new Panel(new BorderLayout(0,0));
            combined.add(colHdr, BorderLayout.NORTH);
            Panel listArea = new Panel(new BorderLayout(0,4));
            listArea.add(overdueList, BorderLayout.CENTER);
            listArea.add(btns, BorderLayout.SOUTH);
            combined.add(listArea, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }


        private void showTab(Button tab, Panel panel) {
            contentArea.removeAll();
            contentArea.add(panel, BorderLayout.CENTER);
            activePanel = panel;
            highlightTab(tab);
            contentArea.validate();
            contentArea.repaint();
        }

        private void highlightTab(Button active) {
            for (Button b : new Button[]{booksTab, borrowersTab,
                    borrowedTab, overdueTab}) {
                boolean on = (b == active);
                b.setBackground(on ? BRAND : new Color(55, 55, 55));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("SansSerif",
                        on ? Font.BOLD : Font.PLAIN, 12));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand().trim();
            switch (cmd) {
                case "BOOKS":      showTab(booksTab,     booksPanel);
                    refreshBooksPanel(); break;
                case "BORROWERS":  showTab(borrowersTab, borrowersPanel);
                    refreshBorrowersPanel(); break;
                case "BORROWED":   showTab(borrowedTab,  borrowedPanel);
                    refreshBorrowedPanel(); break;
                case "OVERDUE":    showTab(overdueTab,   overduePanel);
                    refreshOverduePanel(); break;
            }
        }

        private void doAddBook() {
            BookFormDialog dlg = new BookFormDialog(this, null, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doEditBook() {
            Book book = getSelectedBook();
            if (book == null) { msg("Select a book to edit."); return; }
            BookFormDialog dlg = new BookFormDialog(this, book, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doDeleteBook() {
            Book book = getSelectedBook();
            if (book == null) { msg("Select a book to delete."); return; }

            Dialog confirm = confirmDialog("Delete \"" + book.getTitle() + "\"?");
            confirm.setVisible(true);
        }

        private void doBorrowSelected() {
            Book book = getSelectedBook();
            if (book == null)          { msg("Select a book first."); return; }
            if (!book.canBorrow()) {
                if (book.getCategory() == BookCategory.REFERENCE) {
                    msg("Reference books cannot be borrowed.");
                } else {
                    msg("No available copies. All copies are currently borrowed.");
                }
                return;
            }
            BorrowDialog dlg = new BorrowDialog(this, book, service);
            dlg.setVisible(true);
            if (dlg.wasBorrowed()) refreshAll();
        }

        private void doAddBorrower() {
            BorrowerFormDialog dlg = new BorrowerFormDialog(this, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doViewHistory() {
            Borrower borrower = getSelectedBorrower();
            if (borrower == null) { msg("Select a member first."); return; }

            ArrayList<BorrowRecord> history =
                    service.getBorrowerHistory(borrower.getBorrowerId());

            Dialog hist = new Dialog(this,
                    "Borrowing History — " + borrower.getFullName(), true);
            hist.setLayout(new BorderLayout(8, 8));
            hist.setBackground(Color.WHITE);
            hist.setSize(640, 380);
            hist.setLocationRelativeTo(this);

            java.awt.List histList = new java.awt.List(12, false);
            histList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (history.isEmpty()) {
                histList.add("  No borrowing history for this member.");
            }

            for (BorrowRecord r : history) {
                String status = r.getStatus() == BorrowStatus.RETURNED
                        ? "RETURNED " + (r.getReturnDate() != null
                        ? r.getReturnDate().format(fmt) : "")
                        : r.isOverdue() ? "OVERDUE (" + r.getDaysOverdue() + " days)"
                        : "ACTIVE";
                histList.add(String.format("  %-10s %-30s %-14s %s",
                        r.getRecordId(),
                        r.getBook().getTitle().length() > 28
                                ? r.getBook().getTitle().substring(0,25) + "..."
                                : r.getBook().getTitle(),
                        r.getDueDate().format(fmt),
                        status));
            }

            hist.add(histList, BorderLayout.CENTER);
            Button close = new Button("CLOSE");
            close.addActionListener(e -> hist.dispose());
            Panel bot = new Panel(new FlowLayout(FlowLayout.CENTER));
            bot.add(close); hist.add(bot, BorderLayout.SOUTH);
            hist.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { hist.dispose(); }
            });
            hist.setVisible(true);
        }

        private void doReturnFromBorrower() {
            Borrower borrower = getSelectedBorrower();
            if (borrower == null) { msg("Select a member first."); return; }

            ArrayList<BorrowRecord> active =
                    service.getActiveRecordsForBorrower(borrower.getBorrowerId());
            if (active.isEmpty()) { msg("This member has no active borrows."); return; }


            if (active.size() == 1) {
                ReturnDialog dlg = new ReturnDialog(this, active.get(0), service);
                dlg.setVisible(true);
                if (dlg.wasReturned()) refreshAll();
            } else {
                showSelectReturnDialog(active);
            }
        }

        private void showSelectReturnDialog(ArrayList<BorrowRecord> records) {
            Dialog selectDlg = new Dialog(this, "Select Book to Return", true);
            selectDlg.setLayout(new BorderLayout(8, 8));
            selectDlg.setBackground(Color.WHITE);
            selectDlg.setSize(520, 300);
            selectDlg.setLocationRelativeTo(this);

            java.awt.List sList = new java.awt.List(8, false);
            sList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (BorrowRecord r : records) {
                sList.add(String.format("  %-10s %-30s Due: %s",
                        r.getRecordId(), r.getBook().getTitle(),
                        r.getDueDate().format(fmt)));
            }
            selectDlg.add(sList, BorderLayout.CENTER);

            Button selBtn = new Button("SELECT");
            selBtn.setBackground(GREEN); selBtn.setForeground(Color.WHITE);
            selBtn.addActionListener(e -> {
                int idx = sList.getSelectedIndex();
                if (idx < 0) return;
                selectDlg.dispose();
                ReturnDialog dlg = new ReturnDialog(this, records.get(idx), service);
                dlg.setVisible(true);
                if (dlg.wasReturned()) refreshAll();
            });
            Panel bot = new Panel(new FlowLayout(FlowLayout.CENTER));
            bot.add(selBtn);
            selectDlg.add(bot, BorderLayout.SOUTH);
            selectDlg.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { selectDlg.dispose(); }
            });
            selectDlg.setVisible(true);
        }

        private void doReturnFromBorrowed() {
            int idx = borrowedList.getSelectedIndex();
            if (idx < 0 || idx >= displayedBorrowed.size()) {
                msg("Select a record first."); return;
            }
            BorrowRecord record = displayedBorrowed.get(idx);
            ReturnDialog dlg = new ReturnDialog(this, record, service);
            dlg.setVisible(true);
            if (dlg.wasReturned()) refreshAll();
        }

        private void doReturnOverdue() {
            int idx = overdueList.getSelectedIndex();
            if (idx < 0 || idx >= displayedOverdue.size()) {
                msg("Select an overdue record first."); return;
            }
            BorrowRecord record = displayedOverdue.get(idx);
            ReturnDialog dlg = new ReturnDialog(this, record, service);
            dlg.setVisible(true);
            if (dlg.wasReturned()) refreshAll();
        }

        private void refreshAll() {
            service.refreshStatuses();
            refreshBooksPanel();
            refreshBorrowersPanel();
            refreshBorrowedPanel();
            refreshOverduePanel();
            refreshSummary();
        }

        private void refreshBooksPanel() {
            booksList.removeAll();
            String q = bookSearchField.getText().trim();
            displayedBooks = q.isEmpty()
                    ? service.getAllBooks()
                    : service.searchBooks(q);

            for (Book b : displayedBooks) {
                String status = b.getCategory() == BookCategory.REFERENCE
                        ? "REFERENCE"
                        : b.isAvailable() ? "AVAILABLE" : "FULLY BORROWED";
                booksList.add(String.format("  %-5d %-36s %-22s %-12s %-8s %s",
                        b.getBookId(),
                        trunc(b.getTitle(), 35),
                        trunc(b.getAuthor(), 21),
                        b.getCategory(),
                        b.getCopiesDisplay(),
                        status));
            }
        }

        private void refreshBorrowersPanel() {
            borrowersList.removeAll();
            String q = borrowerSearchField.getText().trim();
            displayedBorrowers = q.isEmpty()
                    ? service.getAllBorrowers()
                    : service.searchBorrowers(q);

            for (Borrower b : displayedBorrowers) {
                ArrayList<BorrowRecord> active =
                        service.getActiveRecordsForBorrower(b.getBorrowerId());
                long overdue = active.stream().filter(BorrowRecord::isOverdue).count();
                borrowersList.add(String.format("  %-12s %-24s %-16s %-10s %d active / %d overdue",
                        b.getBorrowerId(),
                        trunc(b.getFullName(), 23),
                        b.getPhone(),
                        b.getMemberType(),
                        active.size(), overdue));
            }
        }

        private void refreshBorrowedPanel() {
            borrowedList.removeAll();
            displayedBorrowed = service.getActiveRecords();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (displayedBorrowed.isEmpty()) {
                borrowedList.add("  No books currently borrowed.");
                return;
            }

            for (BorrowRecord r : displayedBorrowed) {
                String statusStr = r.isOverdue()
                        ? "OVERDUE " + r.getDaysOverdue() + "d late"
                        : r.getStatus().name();
                borrowedList.add(String.format("  %-10s %-30s %-20s %-14s %s",
                        r.getRecordId(),
                        trunc(r.getBook().getTitle(), 29),
                        trunc(r.getBorrower().getFullName(), 19),
                        r.getDueDateDisplay(),
                        statusStr));
            }
        }

        private void refreshOverduePanel() {
            overdueList.removeAll();
            displayedOverdue = service.getOverdueRecords();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (displayedOverdue.isEmpty()) {
                overdueList.add("  No overdue books. All returned on time!");
                return;
            }

            for (BorrowRecord r : displayedOverdue) {
                overdueList.add(String.format("  %-10s %-28s %-20s %-12s %-12s KES %d",
                        r.getRecordId(),
                        trunc(r.getBook().getTitle(), 27),
                        trunc(r.getBorrower().getFullName(), 19),
                        r.getDueDate().format(fmt),
                        r.getDaysOverdue() + " days",
                        (int) r.calculatePenalty()));
            }
        }

        private void refreshSummary() {
            totalBooksLbl.setText(service.getTotalBooks() + " books");
            totalBorrowersLbl.setText(service.getTotalBorrowers() + " members");
            activeLbl.setText(service.getActiveCount() + " borrowed");
            overdueLbl.setText(service.getOverdueCount() + " overdue");
            penaltyLbl.setText("KES " + (int) service.getTotalPendingPenalties()
                    + " penalties");
        }

        private Book getSelectedBook() {
            int idx = booksList.getSelectedIndex();
            if (idx < 0 || idx >= displayedBooks.size()) return null;
            return displayedBooks.get(idx);
        }

        private Borrower getSelectedBorrower() {
            int idx = borrowersList.getSelectedIndex();
            if (idx < 0 || idx >= displayedBorrowers.size()) return null;
            return displayedBorrowers.get(idx);
        }

        private Panel colHeader(String text) {
            Panel hdr = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            hdr.setBackground(new Color(210, 215, 230));
            Label l = new Label(text);
            l.setFont(new Font("Monospaced", Font.BOLD, 11));
            l.setForeground(new Color(40, 40, 80));
            hdr.add(l);
            return hdr;
        }

        private Button tabBtn(String label, boolean active) {
            Button b = new Button(label);
            b.setBackground(active ? BRAND : new Color(55, 55, 55));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 12));
            b.setPreferredSize(new Dimension(130, 38));
            return b;
        }

        private Button aBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        private Label sLbl(String text, Color color) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 11));
            l.setForeground(color); l.setBackground(new Color(20, 20, 20));
            return l;
        }

        private String trunc(String s, int max) {
            return s.length() > max ? s.substring(0, max - 1) + "…" : s;
        }

        private void msg(String message) {
            Dialog d = new Dialog(this, "Info", true);
            d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 18));
            d.setBackground(Color.WHITE);
            d.setSize(340, 120);
            d.setLocationRelativeTo(this);
            Label l = new Label(message);
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            Button ok = new Button("OK");
            ok.addActionListener(e -> d.dispose());
            d.add(l); d.add(ok);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            d.setVisible(true);
        }

        private Dialog confirmDialog(String message) {
            Dialog d = new Dialog(this, "Confirm", true);
            d.setLayout(new BorderLayout(8, 8));
            d.setBackground(Color.WHITE);
            d.setSize(340, 130);
            d.setLocationRelativeTo(this);
            Label l = new Label("  " + message, Label.LEFT);
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            d.add(l, BorderLayout.CENTER);
            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            Button yes = new Button("YES, DELETE");
            yes.setBackground(RED); yes.setForeground(Color.WHITE);
            Button no  = new Button("CANCEL");
            yes.addActionListener(e -> {
                Book book = getSelectedBook();
                if (book != null) {
                    boolean deleted = service.deleteBook(book.getBookId());
                    if (!deleted) msg("Cannot delete — copies still borrowed.");
                    else refreshAll();
                }
                d.dispose();
            });
            no.addActionListener(e -> d.dispose());
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            btns.add(yes); btns.add(no);
            d.add(btns, BorderLayout.SOUTH);
            return d;
        }

    }
}
