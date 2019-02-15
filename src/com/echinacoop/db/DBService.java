package com.echinacoop.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.h2.jdbcx.JdbcConnectionPool;

import com.echinacoop.controller.Config;
import com.yinsin.other.LogHelper;
import com.yinsin.utils.CommonUtils;

public class DBService {
	private static final LogHelper logger = LogHelper.getLogger(DBService.class);
	public static String DNNAME = Config.APP_DIR + "gxbchat";
	private static JdbcConnectionPool jdbcPool = null;

	public static void initDB(String userId) {
		DNNAME = Config.APP_DIR + userId + "-gxbchat";
		jdbcPool = JdbcConnectionPool.create("jdbc:h2:" + DNNAME, "sa", "gxb");
		jdbcPool.setMaxConnections(60);
	}

	public static void main(String[] args) {
		initDB("");
		List<Map<String, Object>> result = DBService.query("SELECT * FROM INFORMATION_SCHEMA.TABLES", null);
		System.out.println(result);
		
	}

	public static Connection getCconnection() throws SQLException {
		return jdbcPool.getConnection();
	}

	public static boolean execute(String sql, List<Object> params) {
		PreparedStatement ps = null;
		Connection conn = null;
		boolean result = false;
		try {
			conn = getCconnection();
			ps = conn.prepareStatement(sql);
			if (null != params) {
				for (int i = 0, k = params.size(); i < k; i++) {
					ps.setObject(i + 1, params.get(i));
				}
			}
			int count = ps.executeUpdate();
			if(count > 0){
				result = true;
				conn.commit();
			}
		} catch (Exception e) {
			logger.error("执行预编译SQL异常：" + e.getMessage(), e);
		} finally {
			DBHelper.close(null, ps, conn);
		}
		return result;
	}
	
	public static boolean execute(String sql) {
		boolean result = false;
		Connection conn = null;
		Statement ps = null;
		try {
			conn = getCconnection();
			ps = conn.createStatement();
			int count = ps.executeUpdate(sql);
			if(count > 0){
				result = true;
				conn.commit();
			}
		} catch (Exception e) {
			logger.error("执行SQL异常" + e.getMessage(), e);
		} finally {
			DBHelper.close(null, ps, conn);
		}
		return result;
	}

	public static List<Map<String, Object>> query(String sql, List<Object> params) {
		List<Map<String, Object>> result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getCconnection();
			stmt = conn.prepareStatement(sql);
			if (null != params) {
				for (int i = 0, k = params.size(); i < k; i++) {
					stmt.setObject(i + 1, params.get(i));
				}
			}
			rs = stmt.executeQuery();
			if (rs != null) {
				Map<String, Object> data = null;
				result = new ArrayList<Map<String, Object>>();
				Vector<Object> column = null;
				int size = 0;
				String key = null;
				while (rs.next()) {
					try {
						if (column == null) {
							column = resultSetMetaDataToVector(rs.getMetaData());
							size = column.size();
						}
						data = new HashMap<String, Object>();
						for (int i = 0; i < size; i++) {
							key = CommonUtils.objectToString(column.get(i));
							data.put(key, rs.getObject(key));
						}
						result.add(data);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			logger.error("查询数据异常：" + e.getMessage(), e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
		return result;
	}

	public static ResultSet executeQuery(String sql, List<Object> params) {
		ResultSet result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getCconnection();
			stmt = conn.prepareStatement(sql);
			if (null != params) {
				for (int i = 0, k = params.size(); i < k; i++) {
					stmt.setObject(i + 1, params.get(i));
				}
			}
			result = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("查询数据异常：" + e.getMessage(), e);
		} finally {
			DBHelper.close(null, stmt, conn);
		}
		return result;
	}
	
	
	private static Vector<Object> resultSetMetaDataToVector(ResultSetMetaData rsData) {
		Vector<Object> vector = null;
		try {
			int size = rsData.getColumnCount();
			vector = new Vector<Object>();
			for (int i = 0; i < size; i++) {
				vector.add(rsData.getColumnName(i + 1));
			}
		} catch (SQLException e) {
			vector = new Vector<Object>();
		}
		return vector;
	}

}
