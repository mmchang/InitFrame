/**
 * 
 */
package com.lnpc.common;

import com.lnpc.common.rowset.RowSet;

/**
 * @author changjq
 * 
 */
public class TestInsert {
	public static void main(String[] args) throws Exception {
		RowSet rs1 = new RowSet();
		RowSet rs2 = rs1.clone();
		System.out.println(rs1==rs2);
		/*ResultSet rs = null;
		PreparedStatement pst = null;
		String sql = "insert into tbl_study(study_type) values('9')";
		Connection conn = null;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection("jdbc:oracle:thin:@58.154.128.129:1521:COLLEGE", "tiger", "tiger");
		pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int ii = pst.executeUpdate();
		rs = pst.getGeneratedKeys();
		if (rs.next()) {
			System.out.println("数据主键：" + rs.getString(1));
		}
		conn.close();*/
	}
}
