package manu.oop.objectsandclasses;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class TaskManagerPro {

    void main(String[] args) {
        new TaskApp();
    }

    enum Status {
        TODO, IN_PROGRESS, DONE
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    enum Category {
        SCHOOL, WORK, PERSONAL, BUSINESS, HEALTH, OTHER
    }

    class Task {

        private static int idCounter = 0;

        private int  taskId;
        private String title;
        private String description;
        private Priority priority;
        private Status status;
        private Category category;
        private LocalDate  deadline;
        private LocalDateTime createdAt;

        public Task(String title, String description,
                    Priority priority, Category category,
                    LocalDate deadline) {
            idCounter++;
            this.taskId = idCounter;
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.status = Status.TODO;
            this.category = category;
            this.deadline = deadline;
            this.createdAt = LocalDateTime.now();
        }

        public boolean isOverdue() {
            if (deadline == null)      return false;
            if (status == Status.DONE) return false;
            return LocalDate.now().isAfter(deadline);
        }

        public boolean isDueToday() {
            if (deadline == null) return false;
            if (status == Status.DONE) return false;
            return LocalDate.now().equals(deadline);
        }

        public boolean isDueSoon() {
            if (deadline == null) return false;
            if (status == Status.DONE) return false;
            if (isOverdue()) return false;
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            return daysLeft <= 2;
        }

        public long getDaysLeft() {
            if (deadline == null) return Long.MAX_VALUE;
            return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        }

        public Color getStatusColor() {
            if (status == Status.DONE) return new Color(0, 160, 60);
            if (isOverdue()) return new Color(200, 40, 40);  // red
            if (isDueToday() || isDueSoon()) return new Color(200, 130, 0);
            if (status == Status.IN_PROGRESS) return new Color(30, 100, 200);
            return new Color(120, 120, 120);
        }

        public Color getPriorityColor() {
            switch (priority) {
                case HIGH: return new Color(220, 50, 50);
                case MEDIUM: return new Color(220, 140, 0);
                case LOW: return new Color(80, 160, 80);
                default: return Color.GRAY;
            }
        }

        public String getDeadlineDisplay() {
            if (deadline == null) return "No deadline";
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            long days = getDaysLeft();
            if (status == Status.DONE)  return deadline.format(fmt);
            if (days < 0) return deadline.format(fmt) + " OVERDUE";
            if (days == 0) return "TODAY";
            if (days == 1) return "TOMORROW";
            if (days <= 7) return days + " days left";
            return deadline.format(fmt);
        }

        public String toListLine() {
            return String.format("%-35s %-10s %-14s %s",
                    title.length() > 34 ? title.substring(0, 31) + "..." : title,
                    "[" + category + "]",
                    getDeadlineDisplay(),
                    priority.name());
        }

        public int  getTaskId() { return taskId; }
        public String getTitle() { return title; }
        public String getDescription(){ return description; }
        public Priority getPriority() { return priority; }
        public Status getStatus() { return status; }
        public Category getCategory() { return category; }
        public LocalDate getDeadline() { return deadline; }
        public LocalDateTime getCreatedAt() { return createdAt;}

        public void setTitle(String title) { this.title = title; }
        public void setDescription(String desc) { this.description = desc; }
        public void setPriority(Priority priority) { this.priority = priority; }
        public void setStatus(Status status) { this.status = status; }
        public void setCategory(Category category) { this.category = category; }
        public void setDeadline(LocalDate deadline)  { this.deadline = deadline; }

        @Override
        public String toString() {
            return "[" + priority + "] " + title + " — " + status;
        }
    }

    class TaskService {

        private ArrayList<Task> tasks;
        public TaskService() {
            this.tasks = new ArrayList<>();
            loadSampleTasks();
        }

        private void loadSampleTasks() {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate nextWeek = today.plusDays(7);
            LocalDate yesterday= today.minusDays(1);
            LocalDate lastWeek = today.minusDays(5);

            createTask("Submit Java OOP Project",
                    "Complete ManuEats food ordering system and submit to lecturer",
                    Priority.HIGH, Category.SCHOOL, tomorrow);

            createTask("Pay House Rent",
                    "MPESA to landlord — KES 18,000",
                    Priority.HIGH, Category.PERSONAL, today);

            createTask("Prepare Client Proposal",
                    "Website redesign proposal for Wanjiru Designs — include mockups",
                    Priority.HIGH, Category.WORK, nextWeek);

            createTask("Buy Groceries",
                    "Supermarket run — ugali, sukuma, tomatoes, rice",
                    Priority.MEDIUM, Category.PERSONAL, yesterday);

            createTask("Morning Run",
                    "5km around Karura Forest — consistency is key",
                    Priority.LOW, Category.HEALTH, null);

            createTask("Update LinkedIn Profile",
                    "Add new Java skills, update portfolio projects",
                    Priority.LOW, Category.WORK, null);

            createTask("Hospital Checkup",
                    "Annual medical checkup — Aga Khan Hospital",
                    Priority.MEDIUM, Category.HEALTH, nextWeek.plusDays(3));

            createTask("Read Clean Code Book",
                    "Robert C. Martin — chapter 4 onwards",
                    Priority.LOW, Category.SCHOOL, null);

            createTask("Business Registration",
                    "Register ManuEats as a business — Huduma Centre",
                    Priority.MEDIUM, Category.BUSINESS, lastWeek);

            createTask("Team Meeting Notes",
                    "Write and share minutes from Monday's project meeting",
                    Priority.MEDIUM, Category.WORK, today);

            tasks.get(2).setStatus(Status.DONE);
            tasks.get(5).setStatus(Status.IN_PROGRESS);
        }

        public Task createTask(String title, String description,
                               Priority priority, Category category,
                               LocalDate deadline) {
            Task task = new Task(title, description, priority, category, deadline);
            tasks.add(task);
            return task;
        }

        public boolean updateTask(int taskId, String title, String description,
                                  Priority priority, Status status,
                                  Category category, LocalDate deadline) {
            Task task = getTaskById(taskId);
            if (task == null) return false;

            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            task.setStatus(status);
            task.setCategory(category);
            task.setDeadline(deadline);
            return true;
        }

        public boolean deleteTask(int taskId) {
            Task toRemove = getTaskById(taskId);
            if (toRemove == null) return false;
            tasks.remove(toRemove);
            return true;
        }

        public boolean markDone(int taskId) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            task.setStatus(Status.DONE);
            return true;
        }

        public boolean markInProgress(int taskId) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            task.setStatus(Status.IN_PROGRESS);
            return true;
        }

        public Task getTaskById(int taskId) {
            for (Task task : tasks) {
                if (task.getTaskId() == taskId) return task;
            }
            return null;
        }

        public ArrayList<Task> getSortedBy(String sortOption) {
            ArrayList<Task> sorted = new ArrayList<>(tasks);

            switch (sortOption) {
                case "Priority":
                    Collections.sort(sorted, new Comparator<Task>() {
                        public int compare(Task a, Task b) {
                            return a.getPriority().compareTo(b.getPriority());
                        }
                    });
                    break;

                case "Deadline":
                    Collections.sort(sorted, new Comparator<Task>() {
                        public int compare(Task a, Task b) {
                            if (a.getDeadline() == null && b.getDeadline() == null) return 0;
                            if (a.getDeadline() == null) return 1;
                            if (b.getDeadline() == null) return -1;
                            return a.getDeadline().compareTo(b.getDeadline());
                        }
                    });
                    break;

                case "Category":
                    Collections.sort(sorted, new Comparator<Task>() {
                        public int compare(Task a, Task b) {
                            return a.getCategory().compareTo(b.getCategory());
                        }
                    });
                    break;

                case "Status":
                    Collections.sort(sorted, new Comparator<Task>() {
                        public int compare(Task a, Task b) {
                            return a.getStatus().compareTo(b.getStatus());
                        }
                    });
                    break;

                case "Created":
                default:
                    Collections.sort(sorted, new Comparator<Task>() {
                        public int compare(Task a, Task b) {
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        }
                    });
                    break;
            }

            return sorted;
        }

        public ArrayList<Task> getFiltered(ArrayList<Task> source, String filter) {
            if (filter.equals("All")) return source;
            ArrayList<Task> filtered = new ArrayList<>();
            for (Task task : source) {
                boolean matches = false;

                switch (filter) {
                    case "Overdue": matches = task.isOverdue(); break;
                    case "Due Today": matches = task.isDueToday(); break;
                    case "Due Soon": matches = task.isDueSoon() || task.isDueToday(); break;
                    case "TODO": matches = task.getStatus() == Status.TODO;  break;
                    case "In Progress": matches = task.getStatus() == Status.IN_PROGRESS; break;
                    case "Done": matches = task.getStatus() == Status.DONE; break;
                    case "HIGH": matches = task.getPriority() == Priority.HIGH; break;
                    case "MEDIUM": matches = task.getPriority() == Priority.MEDIUM; break;
                    case "LOW": matches = task.getPriority() == Priority.LOW; break;
                    case "SCHOOL": matches = task.getCategory() == Category.SCHOOL; break;
                    case "WORK":  matches = task.getCategory() == Category.WORK; break;
                    case "PERSONAL":  matches = task.getCategory() == Category.PERSONAL; break;
                    case "BUSINESS": matches = task.getCategory() == Category.BUSINESS; break;
                    case "HEALTH": matches = task.getCategory() == Category.HEALTH; break;
                    case "OTHER": matches = task.getCategory() == Category.OTHER; break;
                }

                if (matches) filtered.add(task);
            }

            return filtered;
        }

        public int getTotalCount() { return tasks.size(); }
        public int getDoneCount() {
            int count = 0;
            for (Task t : tasks) if (t.getStatus() == Status.DONE) count++;
            return count;
        }

        public int getOverdueCount() {
            int count = 0;
            for (Task t : tasks) if (t.isOverdue()) count++;
            return count;
        }

        public int getDueTodayCount() {
            int count = 0;
            for (Task t : tasks) if (t.isDueToday()) count++;
            return count;
        }

        public int getInProgressCount() {
            int count = 0;
            for (Task t : tasks) if (t.getStatus() == Status.IN_PROGRESS) count++;
            return count;
        }

        public ArrayList<Task> getAllTasks() { return tasks; }
    }

    class TaskFormDialog extends Dialog implements ActionListener {

        private TaskService taskService;
        private Task  taskToEdit;
        private boolean  saved = false;

        private TextField titleField;
        private TextArea descField;
        private Choice priorityChoice;
        private Choice statusChoice;
        private Choice categoryChoice;
        private TextField deadlineField;

        private Button saveBtn, cancelBtn;
        private Label errorLabel;

        private static final DateTimeFormatter DATE_FMT =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        public TaskFormDialog(Frame parent, Task taskToEdit, TaskService service) {
            super(parent, taskToEdit == null ? "New Task" : "Edit Task", true);
            this.taskToEdit = taskToEdit;
            this.taskService = service;

            buildUI();
            prefillFields();
            setSize(480, 480);
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
            hdr.setBackground(new Color(40, 40, 40));
            Label htl = new Label(
                    taskToEdit == null ? "  Create New Task" : "  Edit Task",
                    Label.LEFT);
            htl.setFont(new Font("SansSerif", Font.BOLD, 15));
            htl.setForeground(Color.WHITE);
            htl.setBackground(new Color(40, 40, 40));
            hdr.add(htl);
            add(hdr, BorderLayout.NORTH);

            Panel form = new Panel(new GridBagLayout());
            form.setBackground(new Color(252, 252, 252));
            GridBagConstraints c = new GridBagConstraints();
            c.insets  = new Insets(7, 14, 7, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            int row = 0;

            row = addFormRow(form, c, row, "Title *", titleField = new TextField());
            titleField.setFont(new Font("SansSerif", Font.PLAIN, 13));

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label dl = boldLabel("Description");
            form.add(dl, c);
            c.gridx = 1; c.gridwidth = 1;
            descField = new TextArea("", 3, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
            descField.setFont(new Font("SansSerif", Font.PLAIN, 12));
            form.add(descField, c);
            row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            form.add(boldLabel("Priority"), c);
            c.gridx = 1;
            priorityChoice = new Choice();
            for (Priority p : Priority.values()) priorityChoice.add(p.name());
            form.add(priorityChoice, c);
            row++;

            if (taskToEdit != null) {
                c.gridy = row; c.gridx = 0; c.gridwidth = 1;
                form.add(boldLabel("Status"), c);
                c.gridx = 1;
                statusChoice = new Choice();
                for (Status s : Status.values()) statusChoice.add(s.name());
                form.add(statusChoice, c);
                row++;
            }

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            form.add(boldLabel("Category"), c);
            c.gridx = 1;
            categoryChoice = new Choice();
            for (Category cat : Category.values()) categoryChoice.add(cat.name());
            form.add(categoryChoice, c);
            row++;

            row = addFormRow(form, c, row, "Deadline (dd/MM/yyyy)",
                    deadlineField = new TextField("optional"));
            deadlineField.setFont(new Font("SansSerif", Font.PLAIN, 13));
            deadlineField.setForeground(new Color(140, 140, 140));
            deadlineField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (deadlineField.getText().equals("optional")) {
                        deadlineField.setText("");
                        deadlineField.setForeground(Color.BLACK);
                    }
                }
            });

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            errorLabel = new Label("", Label.CENTER);
            errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            errorLabel.setForeground(new Color(200, 40, 40));
            form.add(errorLabel, c);

            add(form, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 14, 10));
            btns.setBackground(new Color(252, 252, 252));

            saveBtn = mkBtn(
                    taskToEdit == null ? "CREATE TASK" : "SAVE CHANGES",
                    new Color(0, 140, 60));
            cancelBtn = mkBtn("CANCEL", new Color(180, 50, 50));

            saveBtn.addActionListener(this);
            cancelBtn.addActionListener(this);

            btns.add(saveBtn);
            btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
        }

        private void prefillFields() {
            if (taskToEdit == null) return;

            titleField.setText(taskToEdit.getTitle());
            descField.setText(taskToEdit.getDescription());

            priorityChoice.select(taskToEdit.getPriority().name());
            if (statusChoice != null) statusChoice.select(taskToEdit.getStatus().name());
            categoryChoice.select(taskToEdit.getCategory().name());

            if (taskToEdit.getDeadline() != null) {
                deadlineField.setText(taskToEdit.getDeadline().format(DATE_FMT));
                deadlineField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("CANCEL")) {
                dispose(); return;
            }
            saveTask();
        }

        private void saveTask() {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                errorLabel.setText("Title is required.");
                return;
            }

            LocalDate deadline = null;
            String deadlineText = deadlineField.getText().trim();
            if (!deadlineText.isEmpty() && !deadlineText.equals("optional")) {
                try {
                    deadline = LocalDate.parse(deadlineText, DATE_FMT);
                } catch (DateTimeParseException ex) {
                    errorLabel.setText("Invalid date. Use dd/MM/yyyy e.g. 25/03/2026");
                    return;
                }
            }

            String   desc     = descField.getText().trim();
            Priority priority = Priority.valueOf(priorityChoice.getSelectedItem());
            Category category = Category.valueOf(categoryChoice.getSelectedItem());
            Status   status   = (statusChoice != null)
                    ? Status.valueOf(statusChoice.getSelectedItem())
                    : Status.TODO;

            if (taskToEdit == null) {
                taskService.createTask(title, desc, priority, category, deadline);
            } else {
                taskService.updateTask(taskToEdit.getTaskId(), title, desc,
                        priority, status, category, deadline);
            }
            saved = true;
            dispose();
        }

        private int addFormRow(Panel p, GridBagConstraints c,
                               int row, String labelText, TextField field) {
            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            p.add(boldLabel(labelText), c);
            c.gridx = 1;
            p.add(field, c);
            return row + 1;
        }

        private Label boldLabel(String text) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(160, 34));
            return b;
        }
        public boolean wasSaved() { return saved; }
    }

    public class TaskApp extends Frame implements ActionListener {

        private TaskService taskService;
        private Choice sortChoice;
        private Choice filterChoice;
        private Button newTaskBtn;
        private Button editBtn;
        private Button inProgressBtn;
        private Button doneBtn;
        private Button deleteBtn;

        private java.awt.List taskList;

        private Label totalLabel;
        private Label doneLabel;
        private Label overdueLabel;
        private Label todayLabel;
        private Label progressLabel;

        private Label detailTitle;
        private Label detailStatus;
        private Label detailPriority;
        private Label detailCategory;
        private Label detailDeadline;
        private TextArea detailDesc;

        private ArrayList<Task> displayedTasks = new ArrayList<>();

        private static final Color BRAND = new Color(40, 40, 40);
        private static final Color RED = new Color(200, 40, 40);
        private static final Color AMBER = new Color(200, 130, 0);
        private static final Color GREEN = new Color(0, 150, 60);
        private static final Color BLUE = new Color(30, 100, 200);

        public TaskApp() {
            super("Task Manager Pro+");
            taskService = new TaskService();

            buildUI();
            refreshAll();

            setSize(900, 680);
            setResizable(true);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(0, 0));
            setBackground(new Color(245, 245, 243));
            add(buildHeader(),  BorderLayout.NORTH);
            add(buildCenter(),  BorderLayout.CENTER);
            add(buildBottom(),  BorderLayout.SOUTH);
        }

        private Panel buildHeader() {
            Panel header = new Panel(new BorderLayout());
            header.setBackground(BRAND);
            header.setPreferredSize(new Dimension(0, 54));

            Label title = new Label("TASK MANAGER", Label.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setForeground(Color.WHITE);
            title.setBackground(BRAND);
            header.add(title, BorderLayout.CENTER);

            newTaskBtn = new Button("  + NEW TASK  ");
            newTaskBtn.setBackground(new Color(0, 150, 60));
            newTaskBtn.setForeground(Color.WHITE);
            newTaskBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            newTaskBtn.addActionListener(this);

            Panel right = new Panel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
            right.setBackground(BRAND);
            right.add(newTaskBtn);
            header.add(right, BorderLayout.EAST);

            return header;
        }

        private Panel buildCenter() {
            Panel center = new Panel(new BorderLayout(0, 4));
            center.setBackground(new Color(245, 245, 243));
            center.add(buildSummaryBar(), BorderLayout.NORTH);
            center.add(buildControlBar(), BorderLayout.CENTER);
            return center;
        }

        private Panel buildSummaryBar() {
            Panel bar = new Panel(new FlowLayout(FlowLayout.LEFT, 16, 8));
            bar.setBackground(new Color(235, 235, 232));

            totalLabel = summaryLabel("0 tasks", new Color(60, 60, 60));
            doneLabel = summaryLabel("0 done", GREEN);
            overdueLabel = summaryLabel("0 overdue", RED);
            todayLabel = summaryLabel("0 due today", AMBER);
            progressLabel = summaryLabel("0 in progress",BLUE);

            bar.add(totalLabel);
            bar.add(new Label("|"));
            bar.add(doneLabel);
            bar.add(new Label("|"));
            bar.add(overdueLabel);
            bar.add(new Label("|"));
            bar.add(todayLabel);
            bar.add(new Label("|"));
            bar.add(progressLabel);
            return bar;
        }

        private Panel buildControlBar() {
            Panel wrapper = new Panel(new BorderLayout(4, 4));
            wrapper.setBackground(new Color(245, 245, 243));
            Panel controls = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            controls.setBackground(new Color(245, 245, 243));
            Label sortLbl = new Label("Sort by:");
            sortLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            controls.add(sortLbl);

            sortChoice = new Choice();
            sortChoice.add("Created"); sortChoice.add("Priority");
            sortChoice.add("Deadline"); sortChoice.add("Category");
            sortChoice.add("Status");
            sortChoice.addItemListener(e -> refreshAll());
            controls.add(sortChoice);

            Label filterLbl = new Label("  Filter:");
            filterLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            controls.add(filterLbl);

            filterChoice = new Choice();
            filterChoice.add("All");
            filterChoice.add("Overdue"); filterChoice.add("Due Today");
            filterChoice.add("Due Soon");
            filterChoice.add("TODO"); filterChoice.add("In Progress");
            filterChoice.add("Done");
            filterChoice.add("HIGH"); filterChoice.add("MEDIUM"); filterChoice.add("LOW");
            filterChoice.add("SCHOOL"); filterChoice.add("WORK");
            filterChoice.add("PERSONAL"); filterChoice.add("BUSINESS");
            filterChoice.add("HEALTH"); filterChoice.add("OTHER");
            filterChoice.addItemListener(e -> refreshAll());
            controls.add(filterChoice);

            wrapper.add(controls, BorderLayout.NORTH);

            Panel main = new Panel(new BorderLayout(6, 0));
            main.setBackground(new Color(245, 245, 243));
            Panel listWrap = new Panel(new BorderLayout(0, 4));
            listWrap.setBackground(new Color(245, 245, 243));

            Panel colHeader = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 2));
            colHeader.setBackground(new Color(220, 220, 218));
            Label colLbl = new Label(String.format("  %-4s %-36s %-12s %-16s %s",
                    "P", "TITLE", "CATEGORY", "DEADLINE", "STATUS"));
            colLbl.setFont(new Font("Monospaced", Font.BOLD, 11));
            colLbl.setForeground(new Color(60, 60, 60));
            colHeader.add(colLbl);
            listWrap.add(colHeader, BorderLayout.NORTH);

            taskList = new java.awt.List(16, false);
            taskList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            taskList.setBackground(new Color(255, 255, 254));
            taskList.addItemListener(e -> onTaskSelected());
            taskList.addActionListener(e -> onTaskDoubleClick());
            listWrap.add(taskList, BorderLayout.CENTER);
            main.add(listWrap, BorderLayout.CENTER);

            main.add(buildDetailPanel(), BorderLayout.EAST);
            wrapper.add(main, BorderLayout.CENTER);
            return wrapper;
        }

        private Panel buildDetailPanel() {
            Panel detail = new Panel(new GridBagLayout());
            detail.setBackground(new Color(252, 252, 250));
            detail.setPreferredSize(new Dimension(230, 0));

            GridBagConstraints c = new GridBagConstraints();
            c.insets  = new Insets(5, 10, 5, 10);
            c.fill    = GridBagConstraints.HORIZONTAL;
            c.anchor  = GridBagConstraints.NORTHWEST;
            int row   = 0;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label hdr = new Label("TASK DETAILS");
            hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
            hdr.setForeground(new Color(80, 80, 80));
            detail.add(hdr, c);

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            detailTitle = new Label("(select a task)");
            detailTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            detailTitle.setForeground(new Color(40, 40, 40));
            detail.add(detailTitle, c); row++;

            row = detailRow(detail, c, row, "Status:",   detailStatus   = new Label("—"));
            row = detailRow(detail, c, row, "Priority:", detailPriority = new Label("—"));
            row = detailRow(detail, c, row, "Category:", detailCategory = new Label("—"));
            row = detailRow(detail, c, row, "Deadline:", detailDeadline = new Label("—"));

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label descLbl = new Label("Description:");
            descLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            detail.add(descLbl, c);

            c.gridy = row; c.gridx = 0; c.gridwidth = 2; c.weighty = 1.0;
            detailDesc = new TextArea("", 5, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
            detailDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
            detailDesc.setEditable(false);
            detailDesc.setBackground(new Color(248, 248, 248));
            detail.add(detailDesc, c);

            return detail;
        }

        private Panel buildBottom() {
            Panel bot = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            bot.setBackground(new Color(235, 235, 232));

            editBtn = actionBtn("EDIT", new Color(40, 90, 180));
            inProgressBtn = actionBtn("MARK IN PROGRESS",new Color(30, 130, 180));
            doneBtn = actionBtn("MARK DONE", GREEN);
            deleteBtn = actionBtn("DELETE", RED);

            for (Button b : new Button[]{editBtn, inProgressBtn, doneBtn, deleteBtn}) {
                b.addActionListener(this);
                bot.add(b);
            }
            return bot;
        }
        private void refreshAll() {
            String sort = sortChoice.getSelectedItem();
            String filter = filterChoice.getSelectedItem();
            ArrayList<Task> sorted = taskService.getSortedBy(sort);
            displayedTasks  = taskService.getFiltered(sorted, filter);

            taskList.removeAll();
            if (displayedTasks.isEmpty()) {
                taskList.add("  No tasks match this filter.");
                return;
            }

            for (Task task : displayedTasks) {
                String priorityDot = task.getPriority() == Priority.HIGH   ? "HIGH  "
                        : task.getPriority() == Priority.MEDIUM ? "MED   "
                        :  "LOW ";

                String statusTag = task.getStatus() == Status.DONE ? "[DONE] "
                        : task.getStatus() == Status.IN_PROGRESS ? "[IN PROG] "
                        : "[TODO] ";

                String line = String.format("  %s %-34s %-12s %-16s %s",
                        priorityDot,
                        task.getTitle().length() > 33
                                ? task.getTitle().substring(0, 30) + "..."
                                : task.getTitle(),
                        "[" + task.getCategory() + "]",
                        task.getDeadlineDisplay(),
                        statusTag);

                taskList.add(line);
            }

            // Update summary bar
            totalLabel.setText(taskService.getTotalCount() + " tasks");
            doneLabel.setText(taskService.getDoneCount()  + " done");
            overdueLabel.setText(taskService.getOverdueCount() + " overdue");
            todayLabel.setText(taskService.getDueTodayCount()  + " due today");
            progressLabel.setText(taskService.getInProgressCount() + " in progress");
        }

        private void onTaskSelected() {
            Task task = getSelectedTask();
            if (task == null) return;
            updateDetailPanel(task);
        }

        private void onTaskDoubleClick() {
            Task task = getSelectedTask();
            if (task != null) openEditDialog(task);
        }

        private Task getSelectedTask() {
            int idx = taskList.getSelectedIndex();
            if (idx < 0 || idx >= displayedTasks.size()) return null;
            return displayedTasks.get(idx);
        }

        private void updateDetailPanel(Task task) {
            detailTitle.setText(task.getTitle());
            detailTitle.setForeground(task.getStatusColor());

            detailStatus.setText(task.getStatus().name());
            detailStatus.setForeground(task.getStatusColor());

            detailPriority.setText(task.getPriority().name());
            detailPriority.setForeground(task.getPriorityColor());

            detailCategory.setText(task.getCategory().name());
            detailCategory.setForeground(new Color(60, 60, 60));

            detailDeadline.setText(task.getDeadlineDisplay());
            detailDeadline.setForeground(task.isOverdue() ? RED
                    : task.isDueToday() ? AMBER : new Color(60, 60, 60));

            detailDesc.setText(task.getDescription().isEmpty()
                    ? "(no description)" : task.getDescription());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand().trim();
            switch (cmd) {
                case "+ NEW TASK": openCreateDialog(); break;
                case "EDIT": openEditForSelected();  break;
                case "MARK IN PROGRESS":  markSelectedInProgress();  break;
                case "MARK DONE": markSelectedDone(); break;
                case "DELETE": deleteSelected(); break;
            }
        }

        private void openCreateDialog() {
            TaskFormDialog dialog = new TaskFormDialog(this, null, taskService);
            dialog.setVisible(true);
            if (dialog.wasSaved()) refreshAll();
        }

        private void openEditForSelected() {
            Task task = getSelectedTask();
            if (task == null) { showMsg("Select a task to edit."); return; }
            openEditDialog(task);
        }

        private void openEditDialog(Task task) {
            TaskFormDialog dialog = new TaskFormDialog(this, task, taskService);
            dialog.setVisible(true);
            if (dialog.wasSaved()) {
                refreshAll();
                updateDetailPanel(task);
            }
        }

        private void markSelectedInProgress() {
            Task task = getSelectedTask();
            if (task == null) { showMsg("Select a task first."); return; }
            if (task.getStatus() == Status.DONE) {
                showMsg("This task is already done."); return;
            }
            taskService.markInProgress(task.getTaskId());
            refreshAll();
            updateDetailPanel(task);
        }

        private void markSelectedDone() {
            Task task = getSelectedTask();
            if (task == null) { showMsg("Select a task first."); return; }
            taskService.markDone(task.getTaskId());
            refreshAll();
            updateDetailPanel(task);
        }

        private void deleteSelected() {
            Task task = getSelectedTask();
            if (task == null) { showMsg("Select a task to delete."); return; }

            Dialog confirm = new Dialog(this, "Confirm Delete", true);
            confirm.setLayout(new BorderLayout(10, 10));
            confirm.setBackground(Color.WHITE);
            confirm.setSize(360, 150);
            confirm.setLocationRelativeTo(this);

            Label msg = new Label("  Delete \"" + task.getTitle() + "\"?", Label.LEFT);
            msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
            confirm.add(msg, BorderLayout.CENTER);

            Panel btns = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            Button yes = new Button("YES, DELETE");
            yes.setBackground(RED); yes.setForeground(Color.WHITE);
            yes.setFont(new Font("SansSerif", Font.BOLD, 12));
            Button no  = new Button("CANCEL");
            no.setFont(new Font("SansSerif", Font.BOLD, 12));

            yes.addActionListener(e -> {
                taskService.deleteTask(task.getTaskId());
                refreshAll();
                clearDetailPanel();
                confirm.dispose();
            });
            no.addActionListener(e -> confirm.dispose());

            confirm.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { confirm.dispose(); }
            });

            btns.add(yes); btns.add(no);
            confirm.add(btns, BorderLayout.SOUTH);
            confirm.setVisible(true);
        }

        private void clearDetailPanel() {
            detailTitle.setText("(select a task)");
            detailTitle.setForeground(new Color(40, 40, 40));
            detailStatus.setText("—"); detailPriority.setText("—");
            detailCategory.setText("—"); detailDeadline.setText("—");
            detailDesc.setText("");
        }

        private void showMsg(String msg) {
            Dialog d = new Dialog(this, "Info", true);
            d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            d.setBackground(Color.WHITE);
            d.setSize(320, 120);
            d.setLocationRelativeTo(this);
            Label l = new Label(msg); l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            Button ok = new Button("OK"); ok.addActionListener(e -> d.dispose());
            d.add(l); d.add(ok);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            d.setVisible(true);
        }

        private Label summaryLabel(String text, Color color) {
            Label l = new Label(text);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            l.setForeground(color);
            return l;
        }

        private Button actionBtn(String label, Color bg) {
            Button b = new Button(label);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            b.setPreferredSize(new Dimension(160, 32));
            return b;
        }

        private int detailRow(Panel p, GridBagConstraints c,
                              int row, String lbl, Label val) {
            c.gridy = row; c.gridx = 0; c.gridwidth = 1; c.weighty = 0;
            Label l = new Label(lbl); l.setFont(new Font("SansSerif", Font.BOLD, 11));
            l.setForeground(new Color(100, 100, 100));
            p.add(l, c);
            c.gridx = 1;
            val.setFont(new Font("SansSerif", Font.BOLD, 12));
            p.add(val, c);
            return row + 1;
        }
    }
}
