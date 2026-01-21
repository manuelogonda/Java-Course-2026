void main() {
    double salary = 1000;
    double bonus = 200;
    int quota = 10;

    IO.println("How many sales did the employee make this week?");
    Scanner scanner = new Scanner(System.in);
    int sales = scanner.nextInt();

    if (sales > quota) {
        salary = salary + bonus;
    }
    IO.println("Salary : " + salary);
}
