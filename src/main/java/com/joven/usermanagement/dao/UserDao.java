package com.joven.usermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.joven.usermanagement.bean.User;

public class UserDao {
	
	private String URL = "jdbc:mysql://localhost:3306/stack_instance";
	private String USERNAME = "root";
	private String PASSWORD = "";
	private String DRIVER = "com.mysql.cj.jdbc.Driver";
	
	private static final String INSERT_USERS = "INSERT INTO users " + "(name, email, country) VALUES (?, ?, ?)";
	private static final String SELECT_USERS_BY_ID = "SELECT * FROM users WHERE id = ?";
	private static final String SELECT_USERS = "SELECT * FROM users";
	private static final String DELETE_USER_BY_ID = "DELETE FROM users where id = ?";
	private static final String UPDATE_USERS = "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?";
	
	public UserDao() {
		
	}
	
	/*
	 * Create Connection
	 */
	protected Connection getConnection() {
		Connection con = null;
		
		try {
			Class.forName(DRIVER);
			con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return con;
	}
	
	/*
	 * Create new User
	 */
	public void insertUser(User user) {
		System.out.println(INSERT_USERS);
		try (Connection con = getConnection();
			PreparedStatement preparedStatement = con.prepareStatement(INSERT_USERS)) {
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getCountry());
			
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	
	/*
	 * Select User by Id
	 */
	public User selectUserById(int id) {
		User user = null;
		
		try (Connection con = getConnection();
			PreparedStatement prep = con.prepareStatement(SELECT_USERS_BY_ID)) {
			
			prep.setInt(1, id);
			System.out.println(prep);
			
			ResultSet rs = prep.executeQuery();
			
			while (rs.next()) {
				user = new User(id, rs.getString("name"), rs.getString("email"), rs.getString("country"));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		
		return user;
	}
	
	/*Select all Users
	 * 
	 */
	public List<User> selectAllUsers() {
		List<User> users = new ArrayList<>();
		
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_USERS)) {
			
			System.out.println(ps);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				
				users.add(new User(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		
		return users;
	}
	
	/*
	 * Update User
	 */
	public boolean updateUser(User user) {
		boolean rowUpdated = false;
		
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_USERS)) {
			
			System.out.println("Updated User: " + ps);
			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getCountry());
			ps.setInt(4, user.getId());
			
			rowUpdated = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			printSQLException(e);
		}
		
		return rowUpdated;
	}
	
	/*
	 * Delete user by Id
	 */
	public boolean deleteUserById(int id) {
		boolean rowDeleted = false;
		
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_USER_BY_ID)) {
			
			ps.setInt(1, id);
			rowDeleted = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			printSQLException(e);
		}
		
		return rowDeleted;
	}
	
	/*
	 * Handles Exceptions
	 */
	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
}
