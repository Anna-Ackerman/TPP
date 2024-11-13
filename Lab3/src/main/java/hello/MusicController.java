package hello;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/execute")
    public String executeQuery(@RequestParam String query) {
        String result;
        try (Connection connection = dataSource.getConnection()) {
            result = parseAndExecuteCommand(connection, query);
        } catch (SQLException e) {
            result = "Database Error: " + e.getMessage();
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }
        return result;
    }

    private String parseAndExecuteCommand(Connection connection, String query) throws SQLException {
        Pattern insertPattern = Pattern.compile("insert (\\w+)\\((.*)\\)", Pattern.CASE_INSENSITIVE);
        Pattern deletePattern = Pattern.compile("delete (\\w+)\\((.*)\\)", Pattern.CASE_INSENSITIVE);
        Pattern updatePattern = Pattern.compile("update (\\w+)\\((.*)\\)", Pattern.CASE_INSENSITIVE);
        Pattern selectPattern = Pattern.compile("select (\\w+)", Pattern.CASE_INSENSITIVE);

        Matcher insertMatcher = insertPattern.matcher(query);
        Matcher deleteMatcher = deletePattern.matcher(query);
        Matcher updateMatcher = updatePattern.matcher(query);
        Matcher selectMatcher = selectPattern.matcher(query);

        if (insertMatcher.matches()) {
            return handleInsert(connection, insertMatcher.group(1), insertMatcher.group(2));
        } else if (deleteMatcher.matches()) {
            return handleDelete(connection, deleteMatcher.group(1), deleteMatcher.group(2));
        } else if (updateMatcher.matches()) {
            return handleUpdate(connection, updateMatcher.group(1), updateMatcher.group(2));
        } else if (selectMatcher.matches()) {
            return handleSelect(connection, selectMatcher.group(1));
        }
        return "Invalid command format!";
    }

    private String handleInsert(Connection connection, String tableName, String values) throws SQLException {
        String sql;
        PreparedStatement preparedStatement;

        switch (tableName.toLowerCase()) {
            case "band":
                sql = "INSERT INTO band (name, genre_id) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                preparedStatement.setInt(2, Integer.parseInt(extractValue(values, "genre_id")));
                break;

            case "genre":
                sql = "INSERT INTO genre (name) VALUES (?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                break;

            case "song":
                sql = "INSERT INTO song (name, band_id) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                preparedStatement.setInt(2, Integer.parseInt(extractValue(values, "band_id")));
                break;

            default:
                return "Invalid table name for insert!";
        }

        int rowsAffected = preparedStatement.executeUpdate();
        return "Inserted " + rowsAffected + " row(s) into " + tableName;
    }

    private String handleDelete(Connection connection, String tableName, String values) throws SQLException {
        String sql;
        PreparedStatement preparedStatement;

        switch (tableName.toLowerCase()) {
            case "band":
            case "genre":
            case "song":
                sql = "DELETE FROM " + tableName + " WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, Integer.parseInt(extractValue(values, "id")));
                break;

            default:
                return "Invalid table name for delete!";
        }
        int rowsAffected = preparedStatement.executeUpdate();
        return "Deleted " + rowsAffected + " row(s) from " + tableName;
    }

    private String handleUpdate(Connection connection, String tableName, String values) throws SQLException {
        String sql;
        PreparedStatement preparedStatement;

        switch (tableName.toLowerCase()) {
            case "band":
                sql = "UPDATE band SET name = ?, genre_id = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                preparedStatement.setInt(2, Integer.parseInt(extractValue(values, "genre_id")));
                preparedStatement.setInt(3, Integer.parseInt(extractValue(values, "id")));
                break;

            case "genre":
                sql = "UPDATE genre SET name = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                preparedStatement.setInt(2, Integer.parseInt(extractValue(values, "id")));
                break;

            case "song":
                sql = "UPDATE song SET name = ?, band_id = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, extractValue(values, "name"));
                preparedStatement.setInt(2, Integer.parseInt(extractValue(values, "band_id")));
                preparedStatement.setInt(3, Integer.parseInt(extractValue(values, "id")));
                break;

            default:
                return "Invalid table name for update!";
        }

        int rowsAffected = preparedStatement.executeUpdate();
        return "Updated " + rowsAffected + " row(s) in " + tableName;
    }

    private String handleSelect(Connection connection, String tableName) throws SQLException {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT * FROM " + tableName;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                result.append(metaData.getColumnName(i)).append("\t");
            }
            result.append("<br>");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(resultSet.getString(i)).append("\t");
                }
                result.append("<br>");
            }
        }

        return result.toString();
    }
    private String extractValue(String values, String fieldName) {
        Pattern fieldPattern = Pattern.compile(fieldName + "=\\'([^']+)\\'", Pattern.CASE_INSENSITIVE);
        Matcher fieldMatcher = fieldPattern.matcher(values);

        if (fieldMatcher.find()) {
            return fieldMatcher.group(1);
        }
        return "";
    }

    //    @PostMapping("/execute")
//    public String executeQuery(@RequestParam String query) {
//        StringBuilder result = new StringBuilder();
//        try (Connection connection = dataSource.getConnection();
//             Statement statement = connection.createStatement()) {
//
//            if (query.trim().toLowerCase().startsWith("select")) {
//                try (ResultSet resultSet = statement.executeQuery(query)) {
//                    int columnCount = resultSet.getMetaData().getColumnCount();
//                    while (resultSet.next()) {
//                        for (int i = 1; i <= columnCount; i++) {
//                            result.append(resultSet.getString(i)).append(" ");
//                        }
//                        result.append("<br>");
//                    }
//                }
//            } else {
//                int rowsAffected = statement.executeUpdate(query);
//                result.append("Rows affected: ").append(rowsAffected);
//            }
//        } catch (SQLException e) {
//            result.append("Error: ").append(e.getMessage());
//        }
//        return result.toString();


}


