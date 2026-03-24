package manu.oop.objectsandclasses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;


enum EnrollmentStatus  {
        ENROLLED, DROPPED, WAITLISTED
    }

    enum CourseStatus {
        OPEN, FULL,CLOSED
    }

    enum Department {
        COMPUTING, BUSINESS,
        ENGINEERING, LAW,
        MEDICINE, EDUCATION, OTHER
    }
public class StudentCourseEnrollmentSystem {
    public static void main(String[] args) {
        new StudentCourseEnrollmentSystem().new EnrollmentApp();
    }

    public class Student {
        private static int idCounter = 0;
        private String studentId;
        private String fullName;
        private String email;
        private String phone;
        private String programme;
        private int yearOfStudy;
        private LocalDate registeredDate;

        public Student(String fullName, String email, String phone, String programme, int yearOfStudy) {
            idCounter++;
            this.studentId = String.format("STU-%04d", idCounter);
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.programme = programme;
            this.yearOfStudy = yearOfStudy;
            this.registeredDate = LocalDate.now();
        }

        public String getStudentId() {
            return studentId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public int getYearOfStudy() {
            return yearOfStudy;
        }

        public LocalDate getRegisteredDate() {
            return registeredDate;
        }

        public String getProgramme() {
            return programme;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setYearOfStudy(int yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
        }

        public void setProgramme(String programme) {
            this.programme = programme;
        }

        public boolean canEnrollMore(int currentEnrollmentCount) {
            int maxUnits = 6;
            if (currentEnrollmentCount < maxUnits) return true;
            return false;
        }

        @Override
        public String toString() {
            return studentId + " — " + fullName + programme;
        }
    }

    class Course {
        private static int idCounter = 0;
        private String courseId;
        private String courseCode;
        private String courseName;
        private String lecturer;
        private Department department;
        private int creditUnits;
        private int capacity;
        private int enrolledCount;
        private String semester;
        private CourseStatus status;

        public Course(String courseCode, String courseName, String lecturer,
                      Department department, int creditUnits, int capacity, String semester) {
            idCounter++;
            this.courseId = String.format("CRSID-%04d", idCounter);
            this.enrolledCount = 0;
            this.status = CourseStatus.OPEN;
            this.courseCode = courseCode;
            this.semester = semester;
            this.lecturer = lecturer;
            this.capacity = capacity;
            this.courseName = courseName;
            this.department = department;
            this.creditUnits = creditUnits;
        }

        //getters
        public String getCourseId() {
            return courseId;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getLecturer() {
            return lecturer;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getCreditUnits() {
            return creditUnits;
        }

        public CourseStatus getStatus() {
            return status;
        }

        public Department getDepartment() {
            return department;
        }

        public String getSemester() {
            return semester;
        }

        public int getEnrolledCount() {
            return enrolledCount;
        }

        //setters
        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public void setDepartment(Department department) {
            this.department = department;
        }

        public void setCreditUnits(int creditUnits) {
            this.creditUnits = creditUnits;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public void setLecturer(String lecturer) {
            this.lecturer = lecturer;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        //helpers
        public boolean isFull() {
            if (enrolledCount >= capacity) return true;
            return false;
        }

        public int getSpaceLeft() {
            int spaceLeft = capacity - enrolledCount;
            return spaceLeft;
        }

        public String getCapacityDisplay() {
            return enrolledCount + " / " + capacity;
        }

        public boolean incrementEnrolled() {
            if (isFull()) {
                return false;
            }
            enrolledCount++;
            if (isFull()) {
                status = CourseStatus.FULL;
            }
            return true;
        }

        public void decrementEnrolled() {
            if (enrolledCount > 0) {
                enrolledCount--;
                status = CourseStatus.OPEN;
            }
        }

        @Override
        public String toString() {
            return capacity + " - " +
                    courseId + " - " +
                    courseCode + " - " +
                    courseName + " - " +
                    lecturer + " - " +
                    department + " - " +
                    semester
                    ;
        }
    }

    class Enrollment {
        private static int idCounter = 0;
        private String enrollmentId;
        private Course course;
        private Student student;
        private EnrollmentStatus status;
        private LocalDate enrollmentDate;
        private LocalDate droppedDate;

        public Enrollment(Course course, Student student) {
            idCounter++;
            this.enrollmentId = String.format("ENRID-%04d", idCounter);
            this.enrollmentDate = LocalDate.now();
            this.droppedDate = null;
            this.status = EnrollmentStatus.ENROLLED;
            this.course = course;
            this.student = student;
        }
// getters

        public Course getCourse() {
            return course;
        }
        public String getEnrollmentId() {
            return enrollmentId;
        }
        public Student getStudent() {
            return student;
        }
        public EnrollmentStatus getEnrollmentStatus() {
            return status;
        }
        public LocalDate getEnrollmentDate() {
            return enrollmentDate;
        }
        public LocalDate getDroppedDate() {
            return droppedDate;
        }

        public void drop() {
            status = EnrollmentStatus.DROPPED;
            droppedDate = LocalDate.now();
            course.decrementEnrolled();
        }

        @Override
        public String toString() {
            return enrollmentId + " - " + course + " - " + student + " - " + enrollmentDate;
        }
    }

    class WaitList {
        private int position;
        private static int idCounter = 0;
        private String waitListId;
        private Student student;
        private Course course;
        private LocalDate joinedDate;

        public WaitList(Student student, Course course, int position) {
            idCounter++;
            this.waitListId = String.format("WLID-%04d", idCounter);
            this.joinedDate = LocalDate.now();
            this.student = student;
            this.course = course;
            this.position = position;
        }

        //            getters
        public Course getCourse() {
            return course;
        }
        public LocalDate getJoinedDate() {
            return joinedDate;
        }
        public int getPosition() {
            return position;
        }
        public Student getStudent() {
            return student;
        }
        public String getWaitListId() {
            return waitListId;
        }

        //     setter
        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return
                    course + " - " +
                            position +
                            waitListId +
                            student +
                            joinedDate;
        }
    }

    class EnrollmentService {
        private ArrayList<Student> students;
        private ArrayList<Course> courses;
        private ArrayList<Enrollment> enrollments;
        private ArrayList<WaitList> waitlists;

        public EnrollmentService() {
            this.students = new ArrayList<>();
            this.courses = new ArrayList<>();
            this.enrollments = new ArrayList<>();
            this.waitlists = new ArrayList<>();
        }

        public ArrayList<Student> getAllStudents() { return students; }
        public ArrayList<Course> getAllCourses() { return courses; }
        public ArrayList<Enrollment> getAllEnrollments() { return enrollments; }
        public ArrayList<WaitList> getAllWaitlists() { return waitlists; }

        public int getFullCoursesCount() {
            int count = 0;
            for (int i = 0; i < courses.size(); i++)
                if (courses.get(i).isFull()) count++;
            return count;
        }

        public int getTotalWaitlistCount() { return waitlists.size(); }

        //   1 . student methods
        public Student addStudent(String fullName, String email,
                                  String phone, String programme, int yearOfStudy) {
            Student student = new Student(fullName, email, phone, programme, yearOfStudy);
            students.add(student);
            return student;
        }

        public Student getStudentById(String studentId) {
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(studentId)) {
                    return students.get(i);
                }
            }
            return null;
        }

        public ArrayList<Student> searchStudents(String query) {
            ArrayList<Student> results = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase();
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                if (student.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                        student.getStudentId().toLowerCase().contains(lowerCaseQuery)) {
                    results.add(student);
                }
            }
            return results;
        }

        //   2 . Course Operations
        public Course addCourse(String courseCode, String courseName,
                                String lecturer, Department department,
                                int creditUnits, int capacity,
                                String semester) {
            Course course = new Course(courseCode, courseName, lecturer, department, creditUnits, capacity, semester);
            courses.add(course);
            return course;
        }

        public Course getCourseById(String courseId) {
            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getCourseId().equals(courseId)) {
                    return courses.get(i);
                }
            }
            return null;
        }

        public ArrayList<Course> searchCourses(String query) {
            ArrayList<Course> results = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase();
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                if (course.getCourseName().toLowerCase().contains(lowerCaseQuery) ||
                        course.getCourseId().toLowerCase().contains(lowerCaseQuery)) {
                    results.add(course);
                }
            }
            return results;
        }

        //  3 . enrollment Operations
        public int countActiveEnrollments(Student student) {
            int count = 0;
            for (int i = 0; i < enrollments.size(); i++) {
                Enrollment enrollment = enrollments.get(i);
                if (enrollment.getStudent().getStudentId().equals(student.getStudentId()) &&
                        enrollment.getEnrollmentStatus() == EnrollmentStatus.ENROLLED) {
                    count++;
                }
            }
            return count;
        }

        public String enrollStudent(Student student, Course course) {
            for (int i = 0; i < enrollments.size(); i++) {
                Enrollment enrollment = enrollments.get(i);
                if (enrollment.getStudent().getStudentId().equals(student.getStudentId()) &&
                        enrollment.getCourse().getCourseId().equals(course.getCourseId()) &&
                        enrollment.getEnrollmentStatus() == EnrollmentStatus.ENROLLED
                ) {
                    return "DUPLICATE";
                }
            }
                if (!student.canEnrollMore(countActiveEnrollments(student))) return "MAX_UNITS";
                if (course.getStatus() == CourseStatus.CLOSED) return "CLOSED";
                if (course.isFull()) return "FULL";

                Enrollment enrollment1 = new Enrollment(course, student);
                enrollments.add(enrollment1);
                course.incrementEnrolled();
            return "ENROLLED";
        }

        public String addToWaitList(Student student, Course course) {
            for (int i = 0; i < waitlists.size(); i++) {
                WaitList waitlist = waitlists.get(i);
                if (waitlist.getStudent().getStudentId().equals(student.getStudentId()) &&
                        waitlist.getCourse().getCourseId().equals(course.getCourseId())) {
                    return "ALREADY_WAITLISTED";
                }
            }

            for (int i = 0; i < enrollments.size(); i++) {
                Enrollment enrollment = enrollments.get(i);
                if (enrollment.getStudent().getStudentId().equals(student.getStudentId()) &&
                        enrollment.getCourse().getCourseId().equals(course.getCourseId())) {
                    return "ALREADY_ENROLLED";
                }
            }

            int position = 0;
            for (int i = 0; i < waitlists.size(); i++) {
                if (waitlists.get(i).getCourse().getCourseId().equals(course.getCourseId())) {
                    position++;
                }
            }
            position++;
            WaitList entry = new WaitList(student, course, position);
            waitlists.add(entry);
            return "WAITLISTED";
        }

        public String dropEnrollment(Enrollment enrollment) {
            enrollment.drop();
            return checkWaitlistPromotion(enrollment.getCourse());
        }

        private String checkWaitlistPromotion(Course course) {
            ArrayList<WaitList> courseWaitList = new ArrayList<>();
            for (int i = 0; i < waitlists.size(); i++) {
                if (waitlists.get(i).getCourse().getCourseId().equals(course.getCourseId())) {
                    courseWaitList.add(waitlists.get(i));
                }
            }
            if (courseWaitList.isEmpty()) {
                return "No waitlist for this course.";
            }
            WaitList first = null;
            for (int i = 0; i < courseWaitList.size(); i++) {
                if (courseWaitList.get(i).getPosition() == 1) {
                    first = courseWaitList.get(i);
                    break;
                }
            }
            if (first == null) return "No waitlist for this course.";
            Enrollment promoted = new Enrollment(course, first.getStudent());
            enrollments.add(promoted);
            course.incrementEnrolled();

            waitlists.remove(first);
            for (int i = 0; i < waitlists.size(); i++) {
                WaitList waitlist = waitlists.get(i);
                if (waitlist.getCourse().getCourseId().equals(course.getCourseId())) {
                    waitlist.setPosition(waitlist.getPosition() - 1);
                }
            }
            return first.getStudent().getFullName()
                    + " promoted from waitlist and is enrolled in "
                    + course.getCourseName() + course.getCourseCode();
        }

        //  4 . Query methods
        public ArrayList<Enrollment> getEnrolledStudents(String courseId) {
            ArrayList<Enrollment> results = new ArrayList<>();
            for (int i = 0; i < enrollments.size(); i++) {
                Enrollment enrollment = enrollments.get(i);
                if (enrollment.getCourse().getCourseId().equals(courseId)
                        && enrollment.getEnrollmentStatus() == EnrollmentStatus.ENROLLED) {
                    results.add(enrollment);
                }
            }
            return results;
        }

        public ArrayList<WaitList> getWaitlistForCourse(String courseId) {
            ArrayList<WaitList> results = new ArrayList<>();
            for (int i = 0; i < waitlists.size(); i++) {
                WaitList waitlist = waitlists.get(i);
                if (waitlist.getCourse().getCourseId().equals(courseId)) {
                    results.add(waitlist);
                }
            }
            return results;
        }

        public ArrayList<Enrollment> getStudentEnrollments(String studentId) {
            ArrayList<Enrollment> results = new ArrayList<>();
            for (int i = 0; i < enrollments.size(); i++) {
                Enrollment enrollment = enrollments.get(i);
                if (enrollment.getStudent().getStudentId().equals(studentId)) {
                    results.add(enrollment);
                }
            }
            return results;
        }

    }

    //        GUI
    class CourseFormDialog extends Dialog implements ActionListener {

        private EnrollmentService service;
        private Course            courseToEdit;
        private boolean           saved = false;

        private TextField courseCodeField;
        private TextField courseNameField;
        private TextField lecturerField;
        private TextField creditUnitsField;
        private TextField capacityField;
        private TextField semesterField;
        private Choice departmentChoice;
        private Label errorLabel;
        private Button saveBtn;
        private Button cancelBtn;

        public CourseFormDialog(Frame parent, Course courseToEdit,
                                EnrollmentService service) {
            super(parent,
                    courseToEdit == null ? "Add New Course" : "Edit Course",
                    true);
            this.courseToEdit = courseToEdit;
            this.service = service;
            buildUI();
            if (courseToEdit != null) prefill();
            setSize(460, 430);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 252));

            // Header
            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 90, 160));
            Label hl = new Label(
                    courseToEdit == null ? "  Add New Course" : "  Edit Course",
                    Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE);
            hl.setBackground(new Color(30, 90, 160));
            hdr.add(hl);
            add(hdr, BorderLayout.NORTH);

            // Form
            Panel form = new Panel(new GridBagLayout());
            form.setBackground(new Color(252, 252, 252));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 14, 6, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            int row   = 0;

            row = formRow(form, c, row, "Course Code *",
                    courseCodeField  = new TextField());
            row = formRow(form, c, row, "Course Name *",
                    courseNameField  = new TextField());
            row = formRow(form, c, row, "Lecturer",
                    lecturerField    = new TextField());
            row = formRow(form, c, row, "Credit Units",
                    creditUnitsField = new TextField("3"));
            row = formRow(form, c, row, "Capacity *",
                    capacityField    = new TextField("40"));
            row = formRow(form, c, row, "Semester",
                    semesterField    = new TextField("Semester 1 2026"));

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            form.add(boldLabel("Department"), c);
            c.gridx = 1;
            departmentChoice = new Choice();
            for (Department d : Department.values())
                departmentChoice.add(d.name());
            form.add(departmentChoice, c);
            row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            errorLabel = new Label("", Label.CENTER);
            errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            errorLabel.setForeground(new Color(200, 40, 40));
            form.add(errorLabel, c);
            add(form, BorderLayout.CENTER);

            // Buttons
            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            btns.setBackground(new Color(252, 252, 252));
            saveBtn   = makeBtn(
                    courseToEdit == null ? "ADD COURSE" : "SAVE CHANGES",
                    new Color(30, 90, 160));
            cancelBtn = makeBtn("CANCEL", new Color(160, 160, 160));
            saveBtn.addActionListener(this);
            cancelBtn.addActionListener(this);
            btns.add(saveBtn);
            btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        private void prefill() {
            courseCodeField.setText(courseToEdit.getCourseCode());
            courseNameField.setText(courseToEdit.getCourseName());
            lecturerField.setText(courseToEdit.getLecturer());
            creditUnitsField.setText(String.valueOf(courseToEdit.getCreditUnits()));
            capacityField.setText(String.valueOf(courseToEdit.getCapacity()));
            semesterField.setText(courseToEdit.getSemester());
            departmentChoice.select(courseToEdit.getDepartment().name());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) {
                dispose(); return;
            }
            save();
        }

        private void save() {
            String code = courseCodeField.getText().trim();
            String name = courseNameField.getText().trim();
            if (code.isEmpty()) {
                errorLabel.setText("Course code is required."); return;
            }
            if (name.isEmpty()) {
                errorLabel.setText("Course name is required."); return;
            }

            int units = 3, cap = 40;
            try {
                units = Integer.parseInt(creditUnitsField.getText().trim());
            } catch (NumberFormatException ex) {
                errorLabel.setText("Credit units must be a number."); return;
            }
            try {
                cap = Integer.parseInt(capacityField.getText().trim());
                if (cap < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Capacity must be a positive number."); return;
            }

            String  lec  = lecturerField.getText().trim();
            String sem  = semesterField.getText().trim();
            Department dept = Department.valueOf(
                    departmentChoice.getSelectedItem());

            if (courseToEdit == null) {
                service.addCourse(code, name, lec, dept, units, cap, sem);
            } else {
                // update fields directly since your service has no updateCourse
                courseToEdit.setCourseCode(code);
                courseToEdit.setCourseName(name);
                courseToEdit.setLecturer(lec);
                courseToEdit.setDepartment(dept);
                courseToEdit.setCreditUnits(units);
                courseToEdit.setCapacity(cap);
                courseToEdit.setSemester(sem);
            }
            saved = true;
            dispose();
        }

        // Helpers
        private int formRow(Panel p, GridBagConstraints c,
                            int row, String lbl, TextField tf) {
            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            p.add(boldLabel(lbl), c);
            c.gridx = 1;
            p.add(tf, c);
            return row + 1;
        }

        private Label boldLabel(String text) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button makeBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        public boolean wasSaved() { return saved; }
    }


    class StudentFormDialog extends Dialog implements ActionListener {

        private EnrollmentService service;
        private boolean saved = false;

        private TextField nameField;
        private TextField emailField;
        private TextField phoneField;
        private TextField programmeField;
        private Choice yearChoice;
        private Label errorLabel;
        private Button saveBtn;
        private Button cancelBtn;

        public StudentFormDialog(Frame parent, EnrollmentService service) {
            super(parent, "Register New Student", true);
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
            setBackground(new Color(252, 252, 252));

            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 120, 60));
            Label hl = new Label("  Register New Student", Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 15));
            hl.setForeground(Color.WHITE);
            hl.setBackground(new Color(30, 120, 60));
            hdr.add(hl);
            add(hdr, BorderLayout.NORTH);

            Panel form = new Panel(new GridBagLayout());
            form.setBackground(new Color(252, 252, 252));
            GridBagConstraints c = new GridBagConstraints();
            c.insets  = new Insets(7, 14, 7, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            int row   = 0;

            row = formRow(form, c, row, "Full Name *",  nameField = new TextField());
            row = formRow(form, c, row, "Email",emailField  = new TextField());
            row = formRow(form, c, row, "Phone *", phoneField = new TextField());
            row = formRow(form, c, row, "Programme *", programmeField = new TextField());

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            form.add(bLbl("Year of Study"), c);
            c.gridx = 1;
            yearChoice = new Choice();
            yearChoice.add("1"); yearChoice.add("2");
            yearChoice.add("3"); yearChoice.add("4");
            form.add(yearChoice, c);
            row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            errorLabel = new Label("", Label.CENTER);
            errorLabel.setForeground(new Color(200, 40, 40));
            form.add(errorLabel, c);
            add(form, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            btns.setBackground(new Color(252, 252, 252));
            saveBtn   = mkBtn("REGISTER", new Color(30, 120, 60));
            cancelBtn = mkBtn("CANCEL",   new Color(160, 160, 160));
            saveBtn.addActionListener(this);
            cancelBtn.addActionListener(this);
            btns.add(saveBtn);
            btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) {
                dispose(); return;
            }
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String prog  = programmeField.getText().trim();
            if (name.isEmpty())  { errorLabel.setText("Name required."); return; }
            if (phone.isEmpty()) { errorLabel.setText("Phone required."); return; }
            if (prog.isEmpty())  { errorLabel.setText("Programme required."); return; }

            String email = emailField.getText().trim();
            int year  = Integer.parseInt(yearChoice.getSelectedItem());
            service.addStudent(name, email, phone, prog, year);
            saved = true;
            dispose();
        }

        private int formRow(Panel p, GridBagConstraints c,
                            int row, String lbl, TextField tf) {
            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            p.add(bLbl(lbl), c); c.gridx = 1; p.add(tf, c);
            return row + 1;
        }

        private Label  bLbl(String t) {
            Label l = new Label(t);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        public boolean wasSaved() { return saved; }
    }


    class EnrollDialog extends Dialog implements ActionListener {

        private EnrollmentService service;
        private Student student;
        private boolean enrolled = false;

        private java.awt.List courseListDisplay;
        private Label statusLabel;
        private Button enrollBtn;
        private Button waitlistBtn;
        private Button closeBtn;
        private ArrayList<Course> displayedCourses;

        public EnrollDialog(Frame parent, Student student,
                            EnrollmentService service) {
            super(parent, "Enroll: " + student.getFullName(), true);
            this.student = student;
            this.service = service;
            buildUI();
            loadCourses();
            setSize(580, 440);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 252));

            // Header
            Panel hdr = new Panel();
            hdr.setBackground(new Color(30, 90, 160));
            int active = service.countActiveEnrollments(student);
            Label hl = new Label(
                    "  " + student.getFullName()
                            + "  |  " + active + "/6 units enrolled",
                    Label.LEFT);
            hl.setFont(new Font("SansSerif", Font.BOLD, 13));
            hl.setForeground(Color.WHITE);
            hl.setBackground(new Color(30, 90, 160));
            hdr.add(hl);
            add(hdr, BorderLayout.NORTH);

            // Column header for course list
            Panel colHdr = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            colHdr.setBackground(new Color(210, 218, 238));
            Label ch = new Label(String.format("  %-10s %-30s %-14s %-9s %s",
                    "CODE", "NAME", "DEPT", "CAP", "STATUS"));
            ch.setFont(new Font("Monospaced", Font.BOLD, 11));
            ch.setForeground(new Color(30, 50, 100));
            colHdr.add(ch);

            courseListDisplay = new java.awt.List(12, false);
            courseListDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
            courseListDisplay.setBackground(new Color(255, 255, 254));

            Panel centerPanel = new Panel(new BorderLayout(0, 0));
            centerPanel.add(colHdr, BorderLayout.NORTH);
            centerPanel.add(courseListDisplay, BorderLayout.CENTER);
            add(centerPanel, BorderLayout.CENTER);

            // Bottom: status + buttons
            Panel bot = new Panel(new BorderLayout(6, 4));
            bot.setBackground(new Color(252, 252, 252));

            statusLabel = new Label(
                    "  Select a course and click ENROLL",
                    Label.LEFT);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            statusLabel.setForeground(new Color(80, 80, 80));
            bot.add(statusLabel, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btns.setBackground(new Color(252, 252, 252));
            enrollBtn   = mkBtn("ENROLL", new Color(30,  90, 160));
            waitlistBtn = mkBtn("JOIN WAITLIST", new Color(180, 100,  0));
            closeBtn    = mkBtn("CLOSE",  new Color(160, 160, 160));
            waitlistBtn.setEnabled(false);
            enrollBtn.addActionListener(this);
            waitlistBtn.addActionListener(this);
            closeBtn.addActionListener(this);
            btns.add(enrollBtn);
            btns.add(waitlistBtn);
            btns.add(closeBtn);
            bot.add(btns, BorderLayout.SOUTH);
            add(bot, BorderLayout.SOUTH);
        }

        private void loadCourses() {
            courseListDisplay.removeAll();
            displayedCourses = service.getAllCourses();
            for (Course c : displayedCourses) {
                int wl = service.getWaitlistForCourse(c.getCourseId()).size();
                String statusStr = c.getStatus().name()
                        + (wl > 0 ? " (WL:" + wl + ")" : "");
                courseListDisplay.add(String.format(
                        "  %-10s %-30s %-14s %-9s %s",
                        c.getCourseCode(),
                        c.getCourseName().length() > 28
                                ? c.getCourseName().substring(0, 25) + "..."
                                : c.getCourseName(),
                        c.getDepartment(),
                        c.getCapacityDisplay(),
                        statusStr));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("CLOSE")) { dispose(); return; }

            int idx = courseListDisplay.getSelectedIndex();
            if (idx < 0 || idx >= displayedCourses.size()) {
                setStatus("Select a course first.", new Color(180, 0, 0));
                return;
            }
            Course course = displayedCourses.get(idx);

            if (cmd.equals("ENROLL")) {
                String result = service.enrollStudent(student, course);
                switch (result) {
                    case "ENROLLED":
                        setStatus("Enrolled in " + course.getCourseCode() + "!",
                                new Color(0, 130, 0));
                        enrolled = true;
                        waitlistBtn.setEnabled(false);
                        loadCourses();
                        break;
                    case "FULL":
                        setStatus("Course FULL — click JOIN WAITLIST to queue.",
                                new Color(180, 80, 0));
                        waitlistBtn.setEnabled(true);
                        break;
                    case "DUPLICATE":
                        setStatus("Already enrolled in " + course.getCourseCode(),
                                new Color(180, 0, 0));
                        break;
                    case "MAX_UNITS":
                        setStatus("Unit limit reached (6 units max).",
                                new Color(180, 0, 0));
                        break;
                    case "CLOSED":
                        setStatus("This course is closed for enrollment.",
                                new Color(180, 0, 0));
                        break;
                    default:
                        setStatus("Result: " + result, new Color(80, 80, 80));
                }

            } else if (cmd.equals("JOIN WAITLIST")) {
                String result = service.addToWaitList(student, course);
                switch (result) {
                    case "WAITLISTED":
                        int pos = service.getWaitlistForCourse(
                                course.getCourseId()).size();
                        setStatus("Added to waitlist. Position: #" + pos,
                                new Color(30, 90, 160));
                        enrolled = true;
                        waitlistBtn.setEnabled(false);
                        loadCourses();
                        break;
                    case "ALREADY_WAITLISTED":
                        setStatus("Already on waitlist for " + course.getCourseCode(),
                                new Color(180, 0, 0));
                        break;
                    case "ALREADY_ENROLLED":
                        setStatus("Already enrolled — no need to waitlist.",
                                new Color(180, 0, 0));
                        break;
                    default:
                        setStatus(result, new Color(80, 80, 80));
                }
            }
        }

        private void setStatus(String t, Color c) {
            statusLabel.setText("  " + t);
            statusLabel.setForeground(c);
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        public boolean wasEnrolled() { return enrolled; }
    }

    class EnrollmentApp extends Frame implements ActionListener {

        private EnrollmentService service = new EnrollmentService();

        // Tab buttons
        private Button coursesTab;
        private Button studentsTab;
        private Button enrollmentsTab;
        private Panel contentArea;

        // Panels — one per tab
        private Panel coursesPanel;
        private Panel studentsPanel;
        private Panel enrollmentsPanel;

        // Courses tab components
        private java.awt.List courseListComp;
        private TextField courseSearchField;
        private ArrayList<Course> shownCourses = new ArrayList<>();

        // Students tab components
        private java.awt.List studentListComp;
        private TextField studentSearchField;
        private ArrayList<Student> shownStudents = new ArrayList<>();

        // Enrollments tab components
        private java.awt.List enrollmentListComp;
        private Choice enrollFilterChoice;
        private ArrayList<Object> shownEnrollments = new ArrayList<>();

        // Summary bar labels
        private Label totalCoursesLbl;
        private Label totalStudentsLbl;
        private Label fullCoursesLbl;
        private Label waitlistLbl;

        // Colours
        private static final Color BRAND = new Color(30, 90, 160);
        private static final Color GREEN = new Color(0, 130, 60);
        private static final Color RED = new Color(200, 40, 40);
        private static final Color AMBER = new Color(190, 110, 0);

        public EnrollmentApp() {
            super("Student Course Enrollment System");
            buildUI();
            showTab(coursesTab, coursesPanel);
            refreshAll();
            setSize(1000, 680);
            setResizable(true);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(0, 0));
            setBackground(new Color(240, 244, 252));

            // Header
            Panel hdr = new Panel(new BorderLayout());
            hdr.setBackground(BRAND);
            hdr.setPreferredSize(new Dimension(0, 54));
            Label title = new Label(
                    "   STUDENT COURSE ENROLLMENT SYSTEM", Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setForeground(Color.WHITE);
            title.setBackground(BRAND);
            hdr.add(title, BorderLayout.CENTER);
            Label sub = new Label("University Registry   ", Label.RIGHT);
            sub.setFont(new Font("SansSerif", Font.ITALIC, 12));
            sub.setForeground(new Color(160, 190, 255));
            sub.setBackground(BRAND);
            hdr.add(sub, BorderLayout.EAST);
            add(hdr, BorderLayout.NORTH);

            // Tab bar wrapper
            Panel wrapper = new Panel(new BorderLayout(0, 0));
            wrapper.setBackground(new Color(240, 244, 252));

            // Tab row
            Panel tabRow = new Panel(new BorderLayout());
            tabRow.setBackground(new Color(20, 20, 20));
            tabRow.setPreferredSize(new Dimension(0, 38));

            Panel tabs = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tabs.setBackground(new Color(20, 20, 20));
            coursesTab = tabBtn("  COURSES  ", true);
            studentsTab = tabBtn("  STUDENTS  ", false);
            enrollmentsTab = tabBtn("  ENROLLMENTS  ", false);
            for (Button b : new Button[]{
                    coursesTab, studentsTab, enrollmentsTab}) {
                b.addActionListener(this);
                tabs.add(b);
            }
            tabRow.add(tabs, BorderLayout.WEST);

            // Summary bar (right side of tab row)
            Panel sumBar = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
            sumBar.setBackground(new Color(20, 20, 20));
            totalCoursesLbl = sLbl("0 courses", new Color(160, 200, 255));
            totalStudentsLbl = sLbl("0 students", new Color(160, 200, 255));
            fullCoursesLbl = sLbl("0 full", new Color(255, 120, 120));
            waitlistLbl = sLbl("0 waitlisted", new Color(255, 200, 100));
            for (Label l : new Label[]{
                    totalCoursesLbl, totalStudentsLbl,
                    fullCoursesLbl, waitlistLbl}) {
                sumBar.add(l);
            }
            tabRow.add(sumBar, BorderLayout.EAST);
            wrapper.add(tabRow, BorderLayout.NORTH);

            // Content area — panels swap here when tab clicked
            contentArea = new Panel(new BorderLayout(0, 0));
            contentArea.setBackground(new Color(240, 244, 252));

            coursesPanel = buildCoursesPanel();
            studentsPanel = buildStudentsPanel();
            enrollmentsPanel = buildEnrollmentsPanel();

            wrapper.add(contentArea, BorderLayout.CENTER);
            add(wrapper, BorderLayout.CENTER);
        }

        // ── BUILD COURSES TAB ─────────────────────────────────────────
        private Panel buildCoursesPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 244, 252));

            // Top bar
            Panel top = new Panel(new BorderLayout(6, 0));
            top.setBackground(new Color(240, 244, 252));

            Panel sr = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            sr.setBackground(new Color(240, 244, 252));
            Label sl = new Label("Search:");
            sl.setFont(new Font("SansSerif", Font.BOLD, 12));
            courseSearchField = new TextField(22);
            courseSearchField.addTextListener(e -> refreshCoursesPanel());
            Button clr = new Button("X");
            clr.addActionListener(e -> {
                courseSearchField.setText("");
                refreshCoursesPanel();
            });
            sr.add(sl);
            sr.add(courseSearchField);
            sr.add(clr);

            Panel br = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
            br.setBackground(new Color(240, 244, 252));
            Button addBtn = aBtn("+ ADD COURSE", GREEN);
            addBtn.addActionListener(e -> doAddCourse());
            br.add(addBtn);
            top.add(sr, BorderLayout.WEST);
            top.add(br, BorderLayout.EAST);
            p.add(top, BorderLayout.NORTH);

            // Column header + list + buttons
            Panel combined = new Panel(new BorderLayout(0, 0));
            Panel ch = colHdr(String.format("  %-10s %-10s %-30s %-14s %-9s %s",
                    "ID", "CODE", "NAME", "DEPT", "CAP", "STATUS"));
            courseListComp = new java.awt.List(14, false);
            courseListComp.setFont(new Font("Monospaced", Font.PLAIN, 12));
            courseListComp.setBackground(new Color(255, 255, 254));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(228, 233, 248));
            Button enrollBtn = aBtn("ENROLL STUDENT", BRAND);
            Button editBtn = aBtn("EDIT COURSE", new Color(80, 80, 80));
            Button delBtn = aBtn("DELETE COURSE", RED);
            enrollBtn.addActionListener(e -> doEnrollInSelectedCourse());
            editBtn.addActionListener(e -> doEditCourse());
            delBtn.addActionListener(e -> doDeleteCourse());
            btns.add(enrollBtn);
            btns.add(editBtn);
            btns.add(delBtn);

            Panel la = new Panel(new BorderLayout(0, 4));
            la.add(courseListComp, BorderLayout.CENTER);
            la.add(btns, BorderLayout.SOUTH);
            combined.add(ch, BorderLayout.NORTH);
            combined.add(la, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }

        private Panel buildStudentsPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 244, 252));

            Panel top = new Panel(new BorderLayout(6, 0));
            top.setBackground(new Color(240, 244, 252));

            Panel sr = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            sr.setBackground(new Color(240, 244, 252));
            Label sl = new Label("Search:");
            sl.setFont(new Font("SansSerif", Font.BOLD, 12));
            studentSearchField = new TextField(22);
            studentSearchField.addTextListener(e -> refreshStudentsPanel());
            Button clr = new Button("X");
            clr.addActionListener(e -> {
                studentSearchField.setText("");
                refreshStudentsPanel();
            });
            sr.add(sl);
            sr.add(studentSearchField);
            sr.add(clr);

            Panel br = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
            br.setBackground(new Color(240, 244, 252));
            Button addBtn = aBtn("+ REGISTER STUDENT", GREEN);
            addBtn.addActionListener(e -> doRegisterStudent());
            br.add(addBtn);
            top.add(sr, BorderLayout.WEST);
            top.add(br, BorderLayout.EAST);
            p.add(top, BorderLayout.NORTH);

            Panel combined = new Panel(new BorderLayout(0, 0));
            Panel ch = colHdr(String.format("  %-12s %-24s %-26s %-6s %s",
                    "ID", "FULL NAME", "PROGRAMME", "YEAR", "UNITS"));
            studentListComp = new java.awt.List(14, false);
            studentListComp.setFont(new Font("Monospaced", Font.PLAIN, 12));
            studentListComp.setBackground(new Color(255, 255, 254));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(228, 233, 248));
            Button enrollBtn = aBtn("ENROLL IN COURSE", BRAND);
            Button histBtn = aBtn("VIEW ENROLLMENTS", new Color(80, 80, 80));
            Button dropBtn = aBtn("DROP A COURSE", RED);
            enrollBtn.addActionListener(e -> doEnrollSelectedStudent());
            histBtn.addActionListener(e -> doViewStudentHistory());
            dropBtn.addActionListener(e -> doDropCourseForStudent());
            btns.add(enrollBtn);
            btns.add(histBtn);
            btns.add(dropBtn);

            Panel la = new Panel(new BorderLayout(0, 4));
            la.add(studentListComp, BorderLayout.CENTER);
            la.add(btns, BorderLayout.SOUTH);
            combined.add(ch, BorderLayout.NORTH);
            combined.add(la, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }

        private Panel buildEnrollmentsPanel() {
            Panel p = new Panel(new BorderLayout(4, 4));
            p.setBackground(new Color(240, 244, 252));

            Panel top = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            top.setBackground(new Color(240, 244, 252));
            Label fl = new Label("Filter:");
            fl.setFont(new Font("SansSerif", Font.BOLD, 12));
            enrollFilterChoice = new Choice();
            enrollFilterChoice.add("All");
            enrollFilterChoice.add("Active Only");
            enrollFilterChoice.add("Waitlist Only");
            enrollFilterChoice.add("Dropped Only");
            enrollFilterChoice.addItemListener(e -> refreshEnrollmentsPanel());
            top.add(fl);
            top.add(enrollFilterChoice);
            p.add(top, BorderLayout.NORTH);

            Panel combined = new Panel(new BorderLayout(0, 0));
            Panel ch = colHdr(String.format("  %-10s %-22s %-10s %-30s %s",
                    "ID", "STUDENT", "CODE", "COURSE NAME", "STATUS"));
            enrollmentListComp = new java.awt.List(14, false);
            enrollmentListComp.setFont(new Font("Monospaced", Font.PLAIN, 12));
            enrollmentListComp.setBackground(new Color(255, 255, 254));

            Panel btns = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            btns.setBackground(new Color(228, 233, 248));
            Button dropBtn = aBtn("DROP SELECTED", RED);
            Button promoteBtn = aBtn("PROMOTE FROM WAITLIST", AMBER);
            dropBtn.addActionListener(e -> doDropFromEnrollmentList());
            promoteBtn.addActionListener(e -> doPromoteFromWaitlist());
            btns.add(dropBtn);
            btns.add(promoteBtn);

            Panel la = new Panel(new BorderLayout(0, 4));
            la.add(enrollmentListComp, BorderLayout.CENTER);
            la.add(btns, BorderLayout.SOUTH);
            combined.add(ch, BorderLayout.NORTH);
            combined.add(la, BorderLayout.CENTER);
            p.add(combined, BorderLayout.CENTER);
            return p;
        }

        private void showTab(Button tab, Panel panel) {
            contentArea.removeAll();
            contentArea.add(panel, BorderLayout.CENTER);
            highlightTab(tab);
            contentArea.validate();
            contentArea.repaint();
        }

        private void highlightTab(Button active) {
            for (Button b : new Button[]{
                    coursesTab, studentsTab, enrollmentsTab}) {
                boolean on = (b == active);
                b.setBackground(on ? BRAND : new Color(55, 55, 55));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("SansSerif",
                        on ? Font.BOLD : Font.PLAIN, 12));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().trim()) {
                case "COURSES":
                    showTab(coursesTab, coursesPanel);
                    refreshCoursesPanel();
                    break;
                case "STUDENTS":
                    showTab(studentsTab, studentsPanel);
                    refreshStudentsPanel();
                    break;
                case "ENROLLMENTS":
                    showTab(enrollmentsTab, enrollmentsPanel);
                    refreshEnrollmentsPanel();
                    break;
            }
        }

        private void doAddCourse() {
            CourseFormDialog dlg = new CourseFormDialog(this, null, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doEditCourse() {
            Course c = getSelectedCourse();
            if (c == null) {
                showMsg("Select a course to edit.");
                return;
            }
            CourseFormDialog dlg = new CourseFormDialog(this, c, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doDeleteCourse() {
            Course c = getSelectedCourse();
            if (c == null) {
                showMsg("Select a course to delete.");
                return;
            }
            // Check no active enrollments
            ArrayList<Enrollment> active =
                    service.getEnrolledStudents(c.getCourseId());
            if (!active.isEmpty()) {
                showMsg("Cannot delete — " + active.size()
                        + " student(s) still enrolled.");
                return;
            }
            service.getAllCourses().remove(c);
            refreshAll();
        }

        private void doEnrollInSelectedCourse() {
            Course c = getSelectedCourse();
            if (c == null) {
                showMsg("Select a course first.");
                return;
            }
            showStudentPickerFor(c);
        }

        private void showStudentPickerFor(Course course) {
            Dialog picker = new Dialog(this,
                    "Select Student for " + course.getCourseCode(), true);
            picker.setLayout(new BorderLayout(8, 8));
            picker.setBackground(Color.WHITE);
            picker.setSize(480, 320);
            picker.setLocationRelativeTo(this);

            java.awt.List sList = new java.awt.List(10, false);
            sList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            ArrayList<Student> all = service.getAllStudents();
            for (Student s : all) {
                int active = service.countActiveEnrollments(s);
                sList.add(String.format("  %-12s %-24s %d/6 units",
                        s.getStudentId(), s.getFullName(), active));
            }
            picker.add(sList, BorderLayout.CENTER);

            Button selBtn = aBtn("ENROLL SELECTED", BRAND);
            selBtn.addActionListener(ev -> {
                int idx = sList.getSelectedIndex();
                if (idx < 0) return;
                Student s = all.get(idx);
                picker.dispose();
                String result = service.enrollStudent(s, course);
                if (result.equals("FULL")) {
                    String r2 = service.addToWaitList(s, course);
                    showMsg(r2.equals("WAITLISTED")
                            ? s.getFullName() + " added to waitlist for "
                            + course.getCourseCode()
                            : "Could not waitlist: " + r2);
                } else {
                    showMsg(result.equals("ENROLLED")
                            ? s.getFullName() + " enrolled in "
                            + course.getCourseCode()
                            : "Could not enroll: " + result);
                }
                refreshAll();
            });

            Button cancelBtn = new Button("CANCEL");
            cancelBtn.addActionListener(ev -> picker.dispose());
            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btns.add(selBtn);
            btns.add(cancelBtn);
            picker.add(btns, BorderLayout.SOUTH);
            picker.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    picker.dispose();
                }
            });
            picker.setVisible(true);
        }

        private void doRegisterStudent() {
            StudentFormDialog dlg = new StudentFormDialog(this, service);
            dlg.setVisible(true);
            if (dlg.wasSaved()) refreshAll();
        }

        private void doEnrollSelectedStudent() {
            Student s = getSelectedStudent();
            if (s == null) {
                showMsg("Select a student first.");
                return;
            }
            EnrollDialog dlg = new EnrollDialog(this, s, service);
            dlg.setVisible(true);
            if (dlg.wasEnrolled()) refreshAll();
        }

        private void doViewStudentHistory() {
            Student s = getSelectedStudent();
            if (s == null) {
                showMsg("Select a student first.");
                return;
            }

            ArrayList<Enrollment> history =
                    service.getStudentEnrollments(s.getStudentId());
            ArrayList<WaitList> wl =
                    service.getWaitlistForCourse(""); // reuse — filter below
            // get this student's waitlist entries
            ArrayList<WaitList> studentWL = new ArrayList<>();
            for (WaitList w : service.getAllWaitlists()) {
                if (w.getStudent().getStudentId().equals(s.getStudentId()))
                    studentWL.add(w);
            }

            Dialog hist = new Dialog(this,
                    "History: " + s.getFullName(), true);
            hist.setLayout(new BorderLayout(8, 8));
            hist.setBackground(Color.WHITE);
            hist.setSize(640, 360);
            hist.setLocationRelativeTo(this);

            java.awt.List hList = new java.awt.List(12, false);
            hList.setFont(new Font("Monospaced", Font.PLAIN, 12));

            if (history.isEmpty() && studentWL.isEmpty()) {
                hList.add("  No enrollments found for " + s.getFullName());
            }
            for (Enrollment en : history) {
                hList.add(String.format("  %-10s %-10s %-28s %s",
                        en.getEnrollmentId(),
                        en.getCourse().getCourseCode(),
                        en.getCourse().getCourseName().length() > 26
                                ? en.getCourse().getCourseName().substring(0, 23) + "..."
                                : en.getCourse().getCourseName(),
                        en.getEnrollmentStatus()));
            }
            for (WaitList w : studentWL) {
                hList.add(String.format("  %-10s %-10s %-28s WAITLIST #%d",
                        w.getWaitListId(),
                        w.getCourse().getCourseCode(),
                        w.getCourse().getCourseName().length() > 26
                                ? w.getCourse().getCourseName().substring(0, 23) + "..."
                                : w.getCourse().getCourseName(),
                        w.getPosition()));
            }
            hist.add(hList, BorderLayout.CENTER);

            Button close = new Button("CLOSE");
            close.addActionListener(e -> hist.dispose());
            Panel bot = new Panel(new FlowLayout(FlowLayout.CENTER));
            bot.add(close);
            hist.add(bot, BorderLayout.SOUTH);
            hist.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    hist.dispose();
                }
            });
            hist.setVisible(true);
        }

        private void doDropCourseForStudent() {
            Student s = getSelectedStudent();
            if (s == null) {
                showMsg("Select a student first.");
                return;
            }

            ArrayList<Enrollment> active = new ArrayList<>();
            for (Enrollment en : service.getStudentEnrollments(s.getStudentId())) {
                if (en.getEnrollmentStatus() == EnrollmentStatus.ENROLLED)
                    active.add(en);
            }
            if (active.isEmpty()) {
                showMsg(s.getFullName() + " has no active enrollments.");
                return;
            }

            Dialog picker = new Dialog(this,
                    "Drop Course for " + s.getFullName(), true);
            picker.setLayout(new BorderLayout(8, 8));
            picker.setBackground(Color.WHITE);
            picker.setSize(500, 280);
            picker.setLocationRelativeTo(this);

            java.awt.List eList = new java.awt.List(8, false);
            eList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            for (Enrollment en : active) {
                eList.add(String.format("  %-10s %-10s %s",
                        en.getEnrollmentId(),
                        en.getCourse().getCourseCode(),
                        en.getCourse().getCourseName()));
            }
            picker.add(eList, BorderLayout.CENTER);

            Button dropBtn = aBtn("DROP SELECTED", RED);
            dropBtn.addActionListener(ev -> {
                int idx = eList.getSelectedIndex();
                if (idx < 0) return;
                String notification = service.dropEnrollment(active.get(idx));
                picker.dispose();
                showMsg("Dropped. " + notification);
                refreshAll();
            });
            Button cancelBtn = new Button("CANCEL");
            cancelBtn.addActionListener(ev -> picker.dispose());
            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btns.add(dropBtn);
            btns.add(cancelBtn);
            picker.add(btns, BorderLayout.SOUTH);
            picker.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    picker.dispose();
                }
            });
            picker.setVisible(true);
        }

        private void doDropFromEnrollmentList() {
            int idx = enrollmentListComp.getSelectedIndex();
            if (idx < 0 || idx >= shownEnrollments.size()) {
                showMsg("Select an enrollment record to drop.");
                return;
            }
            Object item = shownEnrollments.get(idx);
            if (!(item instanceof Enrollment)) {
                showMsg("Select an ENROLLED record to drop.");
                return;
            }
            Enrollment en = (Enrollment) item;
            if (en.getEnrollmentStatus() != EnrollmentStatus.ENROLLED) {
                showMsg("This record is already dropped.");
                return;
            }
            String note = service.dropEnrollment(en);
            showMsg("Dropped. " + note);
            refreshAll();
        }

        private void doPromoteFromWaitlist() {
            int idx = enrollmentListComp.getSelectedIndex();
            if (idx < 0 || idx >= shownEnrollments.size()) {
                showMsg("Select a waitlist entry to promote.");
                return;
            }
            Object item = shownEnrollments.get(idx);
            if (!(item instanceof WaitList)) {
                showMsg("Select a WAITLIST entry to promote.");
                return;
            }
            WaitList w = (WaitList) item;
            if (w.getPosition() != 1) {
                showMsg("Only position #1 can be manually promoted.\n"
                        + "Drop an enrolled student to auto-promote.");
                return;
            }
            String result = service.enrollStudent(w.getStudent(), w.getCourse());
            if (result.equals("ENROLLED")) {
                service.getAllWaitlists().remove(w);
                showMsg(w.getStudent().getFullName()
                        + " promoted and enrolled in "
                        + w.getCourse().getCourseCode());
                refreshAll();
            } else {
                showMsg("Could not promote: " + result);
            }
        }

        private void refreshAll() {
            refreshCoursesPanel();
            refreshStudentsPanel();
            refreshEnrollmentsPanel();
            refreshSummary();
        }

        private void refreshCoursesPanel() {
            courseListComp.removeAll();
            String q = courseSearchField.getText().trim();
            shownCourses = q.isEmpty()
                    ? service.getAllCourses()
                    : service.searchCourses(q);

            for (Course c : shownCourses) {
                int wlCount =
                        service.getWaitlistForCourse(c.getCourseId()).size();
                String statusStr = c.getStatus().name()
                        + (wlCount > 0 ? " WL:" + wlCount : "");
                courseListComp.add(String.format(
                        "  %-10s %-10s %-30s %-14s %-9s %s",
                        c.getCourseId(),
                        c.getCourseCode(),
                        c.getCourseName().length() > 28
                                ? c.getCourseName().substring(0, 25) + "..."
                                : c.getCourseName(),
                        c.getDepartment(),
                        c.getCapacityDisplay(),
                        statusStr));
            }
        }

        private void refreshStudentsPanel() {
            studentListComp.removeAll();
            String q = studentSearchField.getText().trim();
            shownStudents = q.isEmpty()
                    ? service.getAllStudents()
                    : service.searchStudents(q);

            for (Student s : shownStudents) {
                int active = service.countActiveEnrollments(s);
                studentListComp.add(String.format(
                        "  %-12s %-24s %-26s %-6s %d/6",
                        s.getStudentId(),
                        s.getFullName().length() > 22
                                ? s.getFullName().substring(0, 19) + "..."
                                : s.getFullName(),
                        s.getProgramme().length() > 24
                                ? s.getProgramme().substring(0, 21) + "..."
                                : s.getProgramme(),
                        "Y" + s.getYearOfStudy(),
                        active));
            }
        }

        private void refreshEnrollmentsPanel() {
            enrollmentListComp.removeAll();
            shownEnrollments.clear();
            String filter = enrollFilterChoice.getSelectedItem();

            // Enrollments
            if (!filter.equals("Waitlist Only")) {
                for (Enrollment en : service.getAllEnrollments()) {
                    if (filter.equals("Active Only")
                            && en.getEnrollmentStatus()
                            != EnrollmentStatus.ENROLLED) continue;
                    if (filter.equals("Dropped Only")
                            && en.getEnrollmentStatus()
                            != EnrollmentStatus.DROPPED) continue;
                    shownEnrollments.add(en);
                    enrollmentListComp.add(String.format(
                            "  %-10s %-22s %-10s %-30s %s",
                            en.getEnrollmentId(),
                            en.getStudent().getFullName().length() > 20
                                    ? en.getStudent().getFullName().substring(0, 17) + "..."
                                    : en.getStudent().getFullName(),
                            en.getCourse().getCourseCode(),
                            en.getCourse().getCourseName().length() > 28
                                    ? en.getCourse().getCourseName().substring(0, 25) + "..."
                                    : en.getCourse().getCourseName(),
                            en.getEnrollmentStatus()));
                }
            }

            // Waitlist entries
            if (!filter.equals("Active Only")
                    && !filter.equals("Dropped Only")) {
                for (WaitList w : service.getAllWaitlists()) {
                    shownEnrollments.add(w);
                    enrollmentListComp.add(String.format(
                            "  %-10s %-22s %-10s %-30s %s",
                            w.getWaitListId(),
                            w.getStudent().getFullName().length() > 20
                                    ? w.getStudent().getFullName().substring(0, 17) + "..."
                                    : w.getStudent().getFullName(),
                            w.getCourse().getCourseCode(),
                            w.getCourse().getCourseName().length() > 28
                                    ? w.getCourse().getCourseName().substring(0, 25) + "..."
                                    : w.getCourse().getCourseName(),
                            "WAITLIST #" + w.getPosition()));
                }
            }

            if (shownEnrollments.isEmpty())
                enrollmentListComp.add("  No records to display.");
        }

        private void refreshSummary() {
            totalCoursesLbl.setText(
                    service.getAllCourses().size() + " courses");
            totalStudentsLbl.setText(
                    service.getAllStudents().size() + " students");
            fullCoursesLbl.setText(
                    service.getFullCoursesCount() + " full");
            waitlistLbl.setText(
                    service.getTotalWaitlistCount() + " waitlisted");
        }

        private Course getSelectedCourse() {
            int idx = courseListComp.getSelectedIndex();
            if (idx < 0 || idx >= shownCourses.size()) return null;
            return shownCourses.get(idx);
        }

        private Student getSelectedStudent() {
            int idx = studentListComp.getSelectedIndex();
            if (idx < 0 || idx >= shownStudents.size()) return null;
            return shownStudents.get(idx);
        }

        private Panel colHdr(String text) {
            Panel hdr = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            hdr.setBackground(new Color(210, 218, 238));
            Label l = new Label(text);
            l.setFont(new Font("Monospaced", Font.BOLD, 11));
            l.setForeground(new Color(30, 50, 100));
            hdr.add(l);
            return hdr;
        }

        private Button tabBtn(String label, boolean active) {
            Button b = new Button(label);
            b.setBackground(active ? BRAND : new Color(55, 55, 55));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif",
                    active ? Font.BOLD : Font.PLAIN, 12));
            b.setPreferredSize(new Dimension(150, 38));
            return b;
        }

        private Button aBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        private Label sLbl(String text, Color color) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 11));
            l.setForeground(color);
            l.setBackground(new Color(20, 20, 20));
            return l;
        }

        private void showMsg(String message) {
            Dialog d = new Dialog(this, "Info", true);
            d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 18));
            d.setBackground(Color.WHITE);
            d.setSize(380, 120);
            d.setLocationRelativeTo(this);
            Label l = new Label(message);
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            Button ok = new Button("OK");
            ok.addActionListener(e -> d.dispose());
            d.add(l);
            d.add(ok);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    d.dispose();
                }
            });
            d.setVisible(true);
        }
    }
}
