//EASY
import java.sql.*;

public class MySQLConnectionExample {
    public static void main(String[] args) {
        // Database credentials and connection details
        String url = "jdbc:mysql://localhost:3306/your_database";
        String user = "your_username";
        String password = "your_password";
        
        // SQL Query
        String query = "SELECT EmpID, Name, Salary FROM Employee";
        
        // Establishing the connection
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Displaying the results
            System.out.println("EmpID | Name | Salary");
            System.out.println("----------------------");
            
            while (rs.next()) {
                int empID = rs.getInt("EmpID");
                String name = rs.getString("Name");
                double salary = rs.getDouble("Salary");
                System.out.printf("%d | %s | %.2f\n", empID, name, salary);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//MEDIUM
import java.sql.*;
import java.util.Scanner;

public class ProductCRUD {
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1. Create 2. Read 3. Update 4. Delete 5. Exit");
                System.out.print("Choose: ");
                int choice = scanner.nextInt();
                if (choice == 5) break;
                handleChoice(choice, scanner);
            }
        }
    }

    private static void handleChoice(int choice, Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            if (choice == 1) createProduct(conn, scanner);
            else if (choice == 2) readProducts(conn);
            else if (choice == 3) updateProduct(conn, scanner);
            else if (choice == 4) deleteProduct(conn, scanner);
            conn.commit();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void createProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Name: "); String name = scanner.next();
        System.out.print("Price: "); double price = scanner.nextDouble();
        System.out.print("Quantity: "); int quantity = scanner.nextInt();
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Product (ProductName, Price, Quantity) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name); pstmt.setDouble(2, price); pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        }
    }

    private static void readProducts(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Product")) {
            while (rs.next()) System.out.printf("%d | %s | %.2f | %d\n", rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4));
        }
    }

    private static void updateProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("ProductID: "); int id = scanner.nextInt();
        System.out.print("New Price: "); double price = scanner.nextDouble();
        System.out.print("New Quantity: "); int quantity = scanner.nextInt();
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Product SET Price = ?, Quantity = ? WHERE ProductID = ?")) {
            pstmt.setDouble(1, price); pstmt.setInt(2, quantity); pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    private static void deleteProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("ProductID: "); int id = scanner.nextInt();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Product WHERE ProductID = ?")) {
            pstmt.setInt(1, id); pstmt.executeUpdate();
        }
    }
}

//HARD
import java.sql.*;
import java.util.Scanner;

// Model
class Student {
    int id; String name, dept; double marks;
    public Student(int id, String name, String dept, double marks) {
        this.id = id; this.name = name; this.dept = dept; this.marks = marks;
    }
}

// Controller
class StudentController {
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    
    public void executeUpdate(String query, Object... params) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void displayStudents() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Students")) {
            while (rs.next()) System.out.printf("%d | %s | %s | %.2f\n", rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

// View
public class StudentApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentController ctrl = new StudentController();
        
        while (true) {
            System.out.println("1. Add 2. View 3. Update 4. Delete 5. Exit");
            switch (sc.nextInt()) {
                case 1 -> { System.out.print("ID Name Dept Marks: ");
                    ctrl.executeUpdate("INSERT INTO Students VALUES (?, ?, ?, ?)", sc.nextInt(), sc.next(), sc.next(), sc.nextDouble()); }
                case 2 -> ctrl.displayStudents();
                case 3 -> { System.out.print("ID New Marks: ");
                    ctrl.executeUpdate("UPDATE Students SET Marks = ? WHERE StudentID = ?", sc.nextDouble(), sc.nextInt()); }
                case 4 -> { System.out.print("ID: ");
                    ctrl.executeUpdate("DELETE FROM Students WHERE StudentID = ?", sc.nextInt()); }
                case 5 -> { sc.close(); return; }
            }
        }
    }
}

  
