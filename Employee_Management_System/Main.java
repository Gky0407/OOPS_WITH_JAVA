import java.io.*;
import java.util.*;

class InvalidSalaryException extends Exception {
    public InvalidSalaryException(String msg) {
        super(msg);
    }
}

class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(String msg) {
        super(msg);
    }
}

class Employee {
    int empId;
    String name;
    String department;
    double salary;

    public Employee(int empId, String name, String department, double salary) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String toFileString() {
        return empId + "," + name + "," + department + "," + salary;
    }
}

class EmployeeService {

    private static final String FILE_NAME = "employees.txt";

    public void addEmployee(Employee e) throws IOException, InvalidSalaryException {
        if (e.salary < 10000) {
            throw new InvalidSalaryException("Salary must be at least 10000");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));
        bw.write(e.toFileString());
        bw.newLine();
        bw.close();

        System.out.println("Employee added successfully.");
    }

    public void displayEmployees() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        String line;

        System.out.println("Employee List:");

        while ((line = br.readLine()) != null) {
            String data[] = line.split(",");
            System.out.println("ID: " + data[0] + " Name: " + data[1] + " Department: " + data[2] + " Salary: " + data[3]);
        }

        br.close();
    }

    public void searchEmployee(int empId) throws IOException, EmployeeNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {
            String data[] = line.split(",");
            if (Integer.parseInt(data[0]) == empId) {
                System.out.println("Employee Found:");
                System.out.println("ID: " + data[0] + " Name: " + data[1] + " Department: " + data[2] + " Salary: " + data[3]);
                found = true;
                break;
            }
        }

        br.close();

        if (!found) {
            throw new EmployeeNotFoundException("Employee not found!");
        }
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        EmployeeService service = new EmployeeService();

        try {

            System.out.print("Enter Employee ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Department: ");
            String dept = sc.nextLine();

            System.out.print("Enter Salary: ");
            double salary = sc.nextDouble();

            Employee emp = new Employee(id, name, dept, salary);

            service.addEmployee(emp);

            service.displayEmployees();

            System.out.print("Enter Employee ID to search: ");
            int searchId = sc.nextInt();

            service.searchEmployee(searchId);

        } 
        catch (InvalidSalaryException e) {
            System.out.println(e.getMessage());
        } 
        catch (EmployeeNotFoundException e) {
            System.out.println(e.getMessage());
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        sc.close();
    }
}