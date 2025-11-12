package dbconnect;

import java.sql.*;
import java.util.Scanner;

public class DatabaseOperations {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // Change this to your MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Database Operations Menu ===");
            System.out.println("1. Add person");
            System.out.println("2. Delete person by ID");
            System.out.println("3. Update person by ID");
            System.out.println("4. Fetch all persons");
            System.out.println("5. Fetch person by ID");
            System.out.println("6. Interactive navigation (next/prev/first/last)");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice = Integer.parseInt(sc.nextLine().trim());

            try {
                switch (choice) {
                    case 1 -> addPerson(sc);
                    case 2 -> deletePerson(sc);
                    case 3 -> updatePerson(sc);
                    case 4 -> fetchAll();
                    case 5 -> fetchById(sc);
                    case 6 -> interactiveNavigation(sc);
                    case 0 -> {
                        System.out.println("Goodbye!");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid option!");
                }
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void addPerson(Scanner sc) throws SQLException {
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter age: ");
        int age = Integer.parseInt(sc.nextLine().trim());

        String sql = "INSERT INTO persons (name, email, age) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, age);
            int rows = ps.executeUpdate();
            System.out.println(rows + " row(s) inserted.");
        }
    }

    private static void deletePerson(Scanner sc) throws SQLException {
        System.out.print("Enter ID to delete: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        String sql = "DELETE FROM persons WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " row(s) deleted.");
        }
    }

    private static void updatePerson(Scanner sc) throws SQLException {
        System.out.print("Enter ID to update: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter new email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter new age: ");
        int age = Integer.parseInt(sc.nextLine().trim());

        String sql = "UPDATE persons SET name = ?, email = ?, age = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, age);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " row(s) updated.");
        }
    }

    private static void fetchAll() throws SQLException {
        String sql = "SELECT * FROM persons";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-5s %-20s %-25s %-5s%n", "ID", "Name", "Email", "Age");
            System.out.println("----------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s %-5d%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age"));
            }
        }
    }

    private static void fetchById(Scanner sc) throws SQLException {
        System.out.print("Enter ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        String sql = "SELECT * FROM persons WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Age: " + rs.getInt("age"));
                } else {
                    System.out.println("No record found for ID: " + id);
                }
            }
        }
    }

    private static void interactiveNavigation(Scanner sc) throws SQLException {
        String sql = "SELECT * FROM persons ORDER BY id";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = st.executeQuery(sql)) {

            if (!rs.next()) {
                System.out.println("No records found.");
                return;
            }
            rs.beforeFirst();

            boolean running = true;
            while (running) {
                System.out.print("\nCommand [next|prev|first|last|exit]: ");
                String cmd = sc.nextLine().trim().toLowerCase();

                switch (cmd) {
                    case "next" -> {
                        if (rs.next()) showRecord(rs);
                        else System.out.println("Already at last record.");
                    }
                    case "prev" -> {
                        if (rs.previous()) showRecord(rs);
                        else System.out.println("Already at first record.");
                    }
                    case "first" -> {
                        rs.first();
                        showRecord(rs);
                    }
                    case "last" -> {
                        rs.last();
                        showRecord(rs);
                    }
                    case "exit" -> running = false;
                    default -> System.out.println("Invalid command.");
                }
            }
        }
    }

    private static void showRecord(ResultSet rs) throws SQLException {
        System.out.println("\nCurrent Record:");
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("Name: " + rs.getString("name"));
        System.out.println("Email: " + rs.getString("email"));
        System.out.println("Age: " + rs.getInt("age"));
    }
}

/*javac dbconnect\DatabaseOperations.java
java dbconnect.DatabaseOperations
 */