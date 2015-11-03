import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by zach on 10/12/15.
 */
public class ToDo {
    static void printTodos(ArrayList<ToDoItem> todos) {
        for (ToDoItem todo : todos) {
            String checkBox = "[ ]";
            if (todo.isDone) {
                checkBox = "[x]";
            }
            String line = String.format("%d. %s %s", todo.id, checkBox, todo.text);
            System.out.println(line);
        }
    }

    static void insertToDo(Connection conn, String text) throws SQLException {//creates insertToDo method for the values we are putting in table
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, false)");//null uses default number of IDENTITY column
        stmt.setString(1, text);
        stmt.execute();
    }

    static ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException { //returns all of the todos from the database
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM todos");
        ArrayList<ToDoItem> todos = new ArrayList(); //creates new arraylist
        while (results.next()){//loops over the results of the todo list
            int id = results.getInt("id");
            String text = results.getString("text");
            Boolean isDone = results.getBoolean("is_done");
            ToDoItem item = new ToDoItem (id, text, isDone);
            todos.add(item);
        }
        return todos;
    }

    static void toggleToDo(Connection conn, int selectNum) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, selectNum);//injects specific row number we want to use
        stmt.execute();
    }


    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN)");//IDENTITY is auto-incramenting



        //ArrayList<ToDoItem> todos = new ArrayList(); removes this ad adds the arraylist to line 53, see line 53
        Scanner scanner = new Scanner(System.in);

        while (true) {
            ArrayList<ToDoItem> todos = selectToDos(conn);//queries the data base each time it is looped over!
            printTodos(todos);

            System.out.println("Options:");
            System.out.println("[1] Create todo");
            System.out.println("[2] Mark todo as done or not done");

            String option = scanner.nextLine();
            int optionNum = Integer.valueOf(option);

            if (optionNum == 1) {
                System.out.println("Type a todo and hit enter");
                String todo = scanner.nextLine();
                //ToDoItem item = new ToDoItem(todo);
                //todos.add(item);  We are removing this and replacing it with the insertToDo method
                insertToDo(conn, todo); //adds ToDo method passing conn and todo into the method as the arguement
            }
            else if (optionNum == 2) {
                System.out.println("Type the number of the todo you want to toggle");
                String select = scanner.nextLine();
                try {
                    int selectNum = Integer.valueOf(select);
                    //ToDoItem item = todos.get(selectNum - 1); removes these items for the BOOLEAN SQL method
                    //item.isDone = !item.isDone;
                    toggleToDo(conn, selectNum);
                } catch (Exception e) {
                    System.out.println("An error occurred.");
                }
            }
            else {
                System.out.println("Invalid number.");
            }
        }
    }
}
