
/*
 * @author: Nainika Aleti
 */

import java.util.*;
import java.sql.*;
import java.text.DecimalFormat;

public class Apriori {
	private static Scanner user_ip;

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Unable to load Driver");
			e.printStackTrace();
		}
		String url = "sql2.njit.edu";
		String ucid = "na368"; // your ucid
		String dbpassword = "winston64"; // your MySQL password
		Connection conn = DriverManager
				.getConnection("jdbc:mysql://" + url + "/" + ucid + "?user=" + ucid + "&password=" + dbpassword);
		Statement stmt1 = conn.createStatement();
		System.out.println("\t\tASSOCIATION RULES GENERATION USING APRIORI ALGORITHM!!!");
		System.out.println("\t=======================================================================");
		System.out.println("\n\nENTER THE MINIMUM SUPPORT VALUE IN %: ");
		float support_value, min_support, min_confidence;
		DecimalFormat df = new DecimalFormat("##.###");
		user_ip = new Scanner(System.in);
		min_support = user_ip.nextInt();
		System.out.println("ENTER THE MINIMUM CONFIDENCE VALUE IN %: ");
		min_confidence = user_ip.nextInt();
		System.out.println("\tENTER A TABLE NAME FROM THE GIVEN LIST");
		System.out.println("========================================================");
		System.out.println("\t\t Amazon");
		System.out.println("\t\t KMART");
		System.out.println("\t\t Walmart");
		System.out.println("\t\t Target");
		System.out.println("\t\t Costco");
		String table_name = user_ip.next();
		if (table_name.equalsIgnoreCase("Amazon") || table_name.equalsIgnoreCase("KMART")
				|| table_name.equalsIgnoreCase("Walmart") || table_name.equalsIgnoreCase("Target")
				|| table_name.equalsIgnoreCase("Costco")) {
			System.out.println("TABLE FOUND!!!");
			String query1 = "CREATE TABLE SCAN1 " + "(ITEMID INTEGER not NULL, " + " ITEM_NAME1 VARCHAR(15), "
					+ " SUPPORT_VAL INTEGER, " + " PRIMARY KEY ( ITEMID ))";
			stmt1.executeUpdate(query1);
			String query2 = "CREATE TABLE SCAN2 " + "(ITEMID INTEGER not NULL, " + " ITEM_NAME1 VARCHAR(15), "
					+ " ITEM_NAME2 VARCHAR(15), " + " SUPPORT_VAL INTEGER, " + " PRIMARY KEY ( ITEMID ))";
			stmt1.executeUpdate(query2);
			String query3 = "CREATE TABLE SCAN3 " + "(ITEMID INTEGER not NULL, " + " ITEM_NAME1 VARCHAR(15), "
					+ " ITEM_NAME2 VARCHAR(15), " + " ITEM_NAME3 VARCHAR(15), " + " SUPPORT_VAL INTEGER, "
					+ " PRIMARY KEY ( ITEMID ))";
			stmt1.executeUpdate(query3);
			String query4 = "CREATE TABLE SCAN4 " + "(ITEMID INTEGER not NULL, " + " ITEM_NAME1 VARCHAR(15), "
					+ " ITEM_NAME2 VARCHAR(15), " + " ITEM_NAME3 VARCHAR(15), " + " ITEM_NAME4 VARCHAR(15), "
					+ " SUPPORT_VAL INTEGER, " + " PRIMARY KEY ( ITEMID ))";
			stmt1.executeUpdate(query4);
			String query5 = "select count(IID) from Items";
			ResultSet result_set1 = stmt1.executeQuery(query5);
			result_set1.next();
			int itemcount = result_set1.getInt(1);
			String query6 = "select count(TID) from " + table_name;
			ResultSet result_set2 = stmt1.executeQuery(query6);
			result_set2.next();
			int no_of_txns = result_set2.getInt(1);
			String query7 = "select * from " + table_name;
			ResultSet result_set3 = stmt1.executeQuery(query7);
			ResultSetMetaData rsmd = result_set3.getMetaData();
			int result_set_count = rsmd.getColumnCount();
			int rule_number = 1;
			int ii = 1;
			while (ii <= itemcount) {
				float count = 0;
				String query8 = "select * from Items where IID=" + ii;
				ResultSet rst_set1 = stmt1.executeQuery(query8);
				rst_set1.next();
				String item_name = new String(rst_set1.getString(2));
				for (int j = 1; j <= no_of_txns; j++) {
					String query9 = "select * from " + table_name + " where TID=" + j;
					ResultSet rst_set2 = stmt1.executeQuery(query9);
					rst_set2.next();
					for (int it_cnt1 = 2; it_cnt1 <= result_set_count; it_cnt1++) {
						String nitem_1 = new String(rst_set2.getString(it_cnt1));
						if (item_name.equals(nitem_1))
							count += 1;
					}
				}
				support_value = calculateSupport(count, no_of_txns);
				if (support_value >= min_support) {
					String query10 = "insert into SCAN1 values" + "(" + "?,?,?" + ")";
					PreparedStatement pst1 = conn.prepareStatement(query10);
					pst1.setInt(1, rule_number);
					pst1.setString(2, item_name);
					pst1.setFloat(3, support_value);
					pst1.executeUpdate();
					rule_number++;
				}
				ii++;
			}
			String query11 = "select count(ITEMID) from SCAN1";
			ResultSet r_set1 = stmt1.executeQuery(query11);
			r_set1.next();
			int cnt_scan1 = r_set1.getInt(1);
			rule_number = 1;
			int x = 1;
			while (x < cnt_scan1) {
				String query12 = "select * from SCAN1 where ITEMID=" + x;
				ResultSet rst_set1 = stmt1.executeQuery(query12);
				rst_set1.next();
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				for (int i = x + 1; i <= cnt_scan1; i++) {
					float count = 0;
					String query13 = "select * from SCAN1 where ITEMID=" + i;
					ResultSet rst_set2 = stmt1.executeQuery(query13);
					rst_set2.next();
					String ITEM_NAME2 = new String(rst_set2.getString(2));
					for (int k = 1; k <= no_of_txns; k++) {
						for (int l = 2; l <= result_set_count; l++) {
							String query14 = "select * from " + table_name + " where TID=" + k;
							ResultSet rst_set3 = stmt1.executeQuery(query14);
							rst_set3.next();
							String nitem_1 = new String(rst_set3.getString(l));
							if (ITEM_NAME1.equals(nitem_1)) {
								for (int it_cnt1 = 2; it_cnt1 <= result_set_count; it_cnt1++) {
									String nitem_2 = new String(rst_set3.getString(it_cnt1));
									if (ITEM_NAME2.equals(nitem_2))
										count += 1;
								}
							}
						}
					}
					support_value = calculateSupport(count, no_of_txns);
					if (support_value >= min_support) {
						String query15 = "insert into SCAN2 values(?,?,?,?)";
						PreparedStatement pst1 = conn.prepareStatement(query15);
						pst1.setInt(1, rule_number);
						pst1.setString(2, ITEM_NAME1);
						pst1.setString(3, ITEM_NAME2);
						pst1.setFloat(4, support_value);
						pst1.executeUpdate();
						rule_number++;
					}
				}
				x++;
			}
			String query16 = "select count(ITEMID) from SCAN2";
			ResultSet r_set2 = stmt1.executeQuery(query16);
			r_set2.next();
			int cnt_scan2 = r_set2.getInt(1);
			rule_number = 1;
			int jj = 1;
			while (jj <= cnt_scan1 - 2) {
				String query17 = "select * from SCAN1 where ITEMID=" + jj;
				ResultSet rst_set1 = stmt1.executeQuery(query17);
				rst_set1.next();
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				for (int i = jj + 1; i <= cnt_scan1 - 1; i++) {
					String query18 = "select * from SCAN1 where ITEMID=" + i;
					ResultSet rst_set2 = stmt1.executeQuery(query18);
					rst_set2.next();
					String ITEM_NAME2 = new String(rst_set2.getString(2));
					for (int k = i + 1; k <= cnt_scan1; k++) {
						float count = 0;
						String query19 = "select * from SCAN1 where ITEMID=" + k;
						ResultSet rst_set3 = stmt1.executeQuery(query19);
						rst_set3.next();
						String ITEM_NAME3 = new String(rst_set3.getString(2));
						for (int txns_cnt = 1; txns_cnt <= no_of_txns; txns_cnt++) {
							for (int res_cnt = 2; res_cnt <= result_set_count; res_cnt++) {
								String query20 = "select * from " + table_name + " where TID=" + txns_cnt;
								ResultSet rst_set4 = stmt1.executeQuery(query20);
								rst_set4.next();
								String nitem_1 = new String(rst_set4.getString(res_cnt));
								if (ITEM_NAME1.equals(nitem_1)) {
									for (int it_cnt1 = 2; it_cnt1 <= result_set_count; it_cnt1++) {
										String nitem_2 = new String(rst_set4.getString(it_cnt1));
										if (ITEM_NAME2.equals(nitem_2)) {
											for (int it_cnt2 = 2; it_cnt2 <= result_set_count; it_cnt2++) {
												String nitem_3 = new String(rst_set4.getString(it_cnt2));
												if (ITEM_NAME3.equals(nitem_3))
													count += 1;
											}
										}
									}
								}
							}
						}
						support_value = calculateSupport(count, no_of_txns);
						if (support_value >= min_support) {
							String query21 = "insert into SCAN3 values(?,?,?,?,?)";
							PreparedStatement pst1 = conn.prepareStatement(query21);
							pst1.setInt(1, rule_number);
							pst1.setString(2, ITEM_NAME1);
							pst1.setString(3, ITEM_NAME2);
							pst1.setString(4, ITEM_NAME3);
							pst1.setFloat(5, support_value);
							pst1.executeUpdate();
							rule_number++;
						}
					}
				}
				jj++;
			}
			String query22 = "select count(ITEMID) from SCAN3";
			ResultSet r_set3 = stmt1.executeQuery(query22);
			r_set3.next();
			int cnt_scan3 = r_set3.getInt(1);
			rule_number = 1;
			int kk = 1;
			while (kk <= cnt_scan1 - 3) {
				String query23 = "select * from SCAN1 where ITEMID=" + kk;
				ResultSet rst_set1 = stmt1.executeQuery(query23);
				rst_set1.next();
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				int ik = kk + 1;
				while (ik <= cnt_scan1 - 2) {
					// for(int i=kk+1;i<=cnt_scan1-2;i++){
					String query24 = "select * from SCAN1 where ITEMID=" + ik;
					ResultSet rst_set2 = stmt1.executeQuery(query24);
					rst_set2.next();
					String ITEM_NAME2 = new String(rst_set2.getString(2));
					int jk = ik + 1;
					// for(int k=ik+1;k<=cnt_scan1-1;k++){
					while (jk <= cnt_scan1 - 1) {
						String query25 = "select * from SCAN1 where ITEMID=" + jk;
						ResultSet rst_set3 = stmt1.executeQuery(query25);
						rst_set3.next();
						String ITEM_NAME3 = new String(rst_set3.getString(2));
						for (int l = jk + 1; l <= cnt_scan1; l++) {
							float count = 0;
							String query26 = "select * from SCAN1 where ITEMID=" + l;
							ResultSet rst_set4 = stmt1.executeQuery(query26);
							rst_set4.next();
							String ITEM_NAME4 = new String(rst_set4.getString(2));
							for (int txns_cnt = 1; txns_cnt <= no_of_txns; txns_cnt++) {
								for (int res_cnt = 2; res_cnt <= result_set_count; res_cnt++) {
									String query27 = "select * from " + table_name + " where TID=" + txns_cnt;
									ResultSet rst_set5 = stmt1.executeQuery(query27);
									rst_set5.next();
									String nitem_1 = new String(rst_set5.getString(res_cnt));
									if (ITEM_NAME1.equals(nitem_1)) {
										for (int it_cnt1 = 2; it_cnt1 <= result_set_count; it_cnt1++) {
											String nitem_2 = new String(rst_set5.getString(it_cnt1));
											if (ITEM_NAME2.equals(nitem_2)) {
												for (int it_cnt2 = 2; it_cnt2 <= result_set_count; it_cnt2++) {
													String nitem_3 = new String(rst_set5.getString(it_cnt2));
													if (ITEM_NAME3.equals(nitem_3)) {
														for (int it_cnt3 = 2; it_cnt3 <= result_set_count; it_cnt3++) {
															String nitem_4 = new String(rst_set5.getString(it_cnt3));
															if (ITEM_NAME4.equals(nitem_4))
																count += 1;
														}
													}
												}
											}
										}
									}
								}
							}
							support_value = calculateSupport(count, no_of_txns);
							if (support_value >= min_support) {
								String query28 = "insert into SCAN4 values(?,?,?,?,?,?)";
								PreparedStatement pst1 = conn.prepareStatement(query28);
								pst1.setInt(1, rule_number);
								pst1.setString(2, ITEM_NAME1);
								pst1.setString(3, ITEM_NAME2);
								pst1.setString(4, ITEM_NAME3);
								pst1.setString(5, ITEM_NAME4);
								pst1.setFloat(6, support_value);
								pst1.executeUpdate();
								rule_number++;
							}
						}
						jk++;
					}
					ik++;
				}
				kk++;
			}
			System.out.println("DO YOU WANT TO PRINT FREQUENT ITEMSETS?(Y/N)");
			String option = user_ip.next();
			if (option.equalsIgnoreCase("Y")) {
				generateFrequentItemSets(stmt1);
			}
			System.out.println("\t\tASSOCIATION RULES");
			System.out.println("==============================================");
			String query29 = "select count(ITEMID) from SCAN4";
			ResultSet r_set4 = stmt1.executeQuery(query29);
			r_set4.next();
			int cnt_scan4 = r_set4.getInt(1);
			rule_number = 1;
			int mm = 1;
			while (mm < cnt_scan2) {
				String query30 = "select * from SCAN2 where ITEMID=" + mm;
				ResultSet rst_set1 = stmt1.executeQuery(query30);
				rst_set1.next();
				double support_txn = rst_set1.getDouble(4);
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				String ITEM_NAME2 = new String(rst_set1.getString(3));
				String query31 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst1 = conn.prepareStatement(query31);
				pst1.setString(1, ITEM_NAME1);
				ResultSet rst_set2 = pst1.executeQuery();
				rst_set2.next();
				double support_val1 = rst_set2.getDouble(3);
				double confidence_val1 = calculateConfidence(support_txn, support_val1);
				if (confidence_val1 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + " ===> " + ITEM_NAME2
							+ " (" + support_txn + "%," + df.format(confidence_val1) + "%)");
					rule_number++;
				}
				String query32 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst2 = conn.prepareStatement(query32);
				pst2.setString(1, ITEM_NAME2);
				ResultSet rst_set3 = pst2.executeQuery();
				rst_set3.next();
				double support_val2 = rst_set3.getDouble(3);
				double confidence_val2 = calculateConfidence(support_txn, support_val2);
				if (confidence_val2 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME2 + " ===> " + ITEM_NAME1
							+ " (" + support_txn + "%," + df.format(confidence_val2) + "%)");
					rule_number++;
				}
				mm++;
			}
			mm = 1;
			while (mm <= cnt_scan3) {
				String query33 = "select * from SCAN3 where ITEMID=" + mm;
				ResultSet rst_set1 = stmt1.executeQuery(query33);
				rst_set1.next();
				double support_txn = rst_set1.getDouble(5);
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				String ITEM_NAME2 = new String(rst_set1.getString(3));
				String ITEM_NAME3 = new String(rst_set1.getString(4));
				String query34 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst1 = conn.prepareStatement(query34);
				pst1.setString(1, ITEM_NAME1);
				ResultSet rst_set2 = pst1.executeQuery();
				rst_set2.next();
				double support_val1 = rst_set2.getDouble(3);
				double confidence_val1 = calculateConfidence(support_txn, support_val1);
				if (confidence_val1 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + " ===> " + ITEM_NAME2
							+ "," + ITEM_NAME3 + " (" + support_txn + "%," + df.format(confidence_val1) + "%)");
					rule_number++;
				}
				String query35 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst2 = conn.prepareStatement(query35);
				pst2.setString(1, ITEM_NAME2);
				ResultSet rst_set3 = pst2.executeQuery();
				rst_set3.next();
				double support_val2 = rst_set3.getDouble(3);
				double confidence_val2 = calculateConfidence(support_txn, support_val2);
				if (confidence_val2 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME2 + " ===> " + ITEM_NAME1
							+ "," + ITEM_NAME3 + " (" + support_txn + "%," + df.format(confidence_val2) + "%)");
					rule_number++;
				}
				String query36 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst3 = conn.prepareStatement(query36);
				pst3.setString(1, ITEM_NAME3);
				ResultSet rst_set4 = pst3.executeQuery();
				rst_set4.next();
				double support_val3 = rst_set4.getDouble(3);
				double confidence_val3 = calculateConfidence(support_txn, support_val3);
				if (confidence_val3 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME3 + " ===> " + ITEM_NAME1
							+ "," + ITEM_NAME2 + " (" + support_txn + "%," + df.format(confidence_val3) + "%)");
					rule_number++;
				}
				int mk = 1;
				while (mk <= cnt_scan2) {
					String query37 = "select * from SCAN2 where ITEMID=" + mk;
					ResultSet rst_set5 = stmt1.executeQuery(query37);
					rst_set5.next();
					String nitem_1 = rst_set5.getString(2);
					String nitem_2 = rst_set5.getString(3);
					if ((ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + ","
									+ ITEM_NAME2 + " ===> " + ITEM_NAME3 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					mk++;
				}
				mk = 1;
				while (mk <= cnt_scan2) {
					String query38 = "select * from SCAN2 where ITEMID=" + mk;
					ResultSet rst_set5 = stmt1.executeQuery(query38);
					rst_set5.next();
					String nitem_1 = rst_set5.getString(2);
					String nitem_2 = rst_set5.getString(3);
					if ((ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2))
							&& (ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME2 + ","
									+ ITEM_NAME3 + " ===> " + ITEM_NAME1 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					mk++;
				}
				mk = 1;
				while (mk <= cnt_scan2) {
					String query39 = "select * from SCAN2 where ITEMID=" + mk;
					ResultSet rst_set5 = stmt1.executeQuery(query39);
					rst_set5.next();
					String nitem_1 = rst_set5.getString(2);
					String nitem_2 = rst_set5.getString(3);
					if ((ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2))
							&& (ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + ","
									+ ITEM_NAME3 + " ===> " + ITEM_NAME2 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					mk++;
				}
				mm++;
			}
			for (int j = 1; j <= cnt_scan4; j++) {
				String query40 = "select * from SCAN4 where ITEMID=" + j;
				ResultSet rst_set1 = stmt1.executeQuery(query40);
				rst_set1.next();
				double support_txn = rst_set1.getDouble(6);
				String ITEM_NAME1 = new String(rst_set1.getString(2));
				String ITEM_NAME2 = new String(rst_set1.getString(3));
				String ITEM_NAME3 = new String(rst_set1.getString(4));
				String ITEM_NAME4 = new String(rst_set1.getString(5));
				String query41 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst1 = conn.prepareStatement(query41);
				pst1.setString(1, ITEM_NAME1);
				ResultSet rst_set2 = pst1.executeQuery();
				rst_set2.next();
				double support_val1 = rst_set2.getDouble(3);
				double confidence_val1 = calculateConfidence(support_txn, support_val1);
				if (confidence_val1 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + " ===> " + ITEM_NAME2
							+ "," + ITEM_NAME3 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
							+ df.format(confidence_val1) + "%)");
					rule_number++;
				}
				String query42 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst2 = conn.prepareStatement(query42);
				pst2.setString(1, ITEM_NAME2);
				ResultSet rst_set3 = pst2.executeQuery();
				rst_set3.next();
				double support_val2 = rst_set3.getDouble(3);
				double confidence_val2 = calculateConfidence(support_txn, support_val2);
				if (confidence_val2 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME2 + " ===> " + ITEM_NAME1
							+ "," + ITEM_NAME3 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
							+ df.format(confidence_val2) + "%)");
					rule_number++;
				}
				String query43 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst3 = conn.prepareStatement(query43);
				pst3.setString(1, ITEM_NAME3);
				ResultSet rst_set4 = pst3.executeQuery();
				rst_set4.next();
				double support_val3 = rst_set4.getDouble(3);
				double confidence_val3 = support_txn / support_val3 * 100;
				if (confidence_val3 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME3 + " ===> " + ITEM_NAME1
							+ "," + ITEM_NAME2 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
							+ df.format(confidence_val3) + "%)");
					rule_number++;
				}
				String query44 = "select * from SCAN1 where ITEM_NAME1=?";
				PreparedStatement pst4 = conn.prepareStatement(query44);
				pst4.setString(1, ITEM_NAME4);
				ResultSet rst_set6 = pst4.executeQuery();
				rst_set6.next();
				double support_val5 = rst_set6.getDouble(3);
				double confidence_val5 = calculateConfidence(support_txn, support_val5);
				if (confidence_val5 >= min_confidence) {
					System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + " ===> " + ITEM_NAME1
							+ "," + ITEM_NAME2 + "," + ITEM_NAME3 + " (" + support_txn + "%,"
							+ df.format(confidence_val5) + "%)");
					rule_number++;
				}
				for (int k = 1; k <= cnt_scan2; k++) {
					String query45 = "select * from SCAN2 where ITEMID=" + k;
					ResultSet rst_set5 = stmt1.executeQuery(query45);
					rst_set5.next();
					String nitem_1 = rst_set5.getString(2);
					String nitem_2 = rst_set5.getString(3);
					if ((ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + ","
									+ ITEM_NAME2 + " ===> " + ITEM_NAME3 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					if ((ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + ","
									+ ITEM_NAME2 + " ===> " + ITEM_NAME3 + "," + ITEM_NAME1 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					if ((ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2))
							&& (ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + ","
									+ ITEM_NAME1 + " ===> " + ITEM_NAME3 + "," + ITEM_NAME2 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					if ((ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2))
							&& (ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME3 + ","
									+ ITEM_NAME1 + " ===> " + ITEM_NAME2 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					if ((ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME3 + ","
									+ ITEM_NAME2 + " ===> " + ITEM_NAME1 + "," + ITEM_NAME4 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
					if ((ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2))
							&& (ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2))) {
						double support_val4 = rst_set5.getDouble(4);
						double confidence_val4 = calculateConfidence(support_txn, support_val4);
						if (confidence_val4 >= min_confidence) {
							System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME3 + ","
									+ ITEM_NAME4 + " ===> " + ITEM_NAME1 + "," + ITEM_NAME2 + " (" + support_txn + "%,"
									+ df.format(confidence_val4) + "%)");
							rule_number++;
						}
					}
				}
				for (int k = 1; k <= cnt_scan3; k++) {
					String query46 = "select * from SCAN3 where ITEMID=" + k;
					ResultSet rst_set5 = stmt1.executeQuery(query46);
					rst_set5.next();
					String nitem_1 = rst_set5.getString(2);
					String nitem_2 = rst_set5.getString(3);
					String nitem_3 = rst_set5.getString(4);
					if ((ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2) || ITEM_NAME1.equals(nitem_3))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2)
									|| ITEM_NAME2.equals(nitem_3))) {
						if (ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2) || ITEM_NAME3.equals(nitem_3)) {
							double support_val4 = rst_set5.getDouble(5);
							double confidence_val4 = calculateConfidence(support_txn, support_val4);
							if (confidence_val4 >= min_confidence) {
								System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME1 + ","
										+ ITEM_NAME2 + "," + ITEM_NAME3 + " ===> " + ITEM_NAME4 + " (" + support_txn
										+ "%," + df.format(confidence_val4) + "%)");
								rule_number++;
							}
						}
					}
					if ((ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2) || ITEM_NAME4.equals(nitem_3))
							&& (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2)
									|| ITEM_NAME2.equals(nitem_3))) {
						if (ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2) || ITEM_NAME3.equals(nitem_3)) {
							double support_val4 = rst_set5.getDouble(5);
							double confidence_val4 = calculateConfidence(support_txn, support_val4);
							if (confidence_val4 >= min_confidence) {
								System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + ","
										+ ITEM_NAME2 + "," + ITEM_NAME3 + " ===> " + ITEM_NAME1 + " (" + support_txn
										+ "%," + df.format(confidence_val4) + "%)");
								rule_number++;
							}
						}
					}
					if ((ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2) || ITEM_NAME4.equals(nitem_3))
							&& (ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2)
									|| ITEM_NAME1.equals(nitem_3))) {
						if (ITEM_NAME3.equals(nitem_1) || ITEM_NAME3.equals(nitem_2) || ITEM_NAME3.equals(nitem_3)) {
							double support_val4 = rst_set5.getDouble(5);
							double confidence_val4 = calculateConfidence(support_txn, support_val4);
							if (confidence_val4 >= min_confidence) {
								System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + ","
										+ ITEM_NAME1 + "," + ITEM_NAME3 + " ===> " + ITEM_NAME2 + " (" + support_txn
										+ "%," + df.format(confidence_val4) + "%)");
								rule_number++;
							}
						}
					}
					if ((ITEM_NAME4.equals(nitem_1) || ITEM_NAME4.equals(nitem_2) || ITEM_NAME4.equals(nitem_3))
							&& (ITEM_NAME1.equals(nitem_1) || ITEM_NAME1.equals(nitem_2)
									|| ITEM_NAME1.equals(nitem_3))) {
						if (ITEM_NAME2.equals(nitem_1) || ITEM_NAME2.equals(nitem_2) || ITEM_NAME2.equals(nitem_3)) {
							double support_val4 = rst_set5.getDouble(5);
							double confidence_val4 = calculateConfidence(support_txn, support_val4);
							if (confidence_val4 >= min_confidence) {
								System.out.println("ASSOCIATION RULE NO." + rule_number + " : " + ITEM_NAME4 + ","
										+ ITEM_NAME2 + "," + ITEM_NAME1 + " ===> " + ITEM_NAME3 + " (" + support_txn
										+ "%," + df.format(confidence_val4) + "%)");
								rule_number++;
							}
						}
					}
				}
			}
			String drop_scan1 = "drop table SCAN1";
			String drop_scan2 = "drop table SCAN2";
			String drop_scan3 = "drop table SCAN3";
			String drop_scan4 = "drop table SCAN4";
			PreparedStatement pst1 = conn.prepareStatement(drop_scan1);
			pst1.executeUpdate();
			PreparedStatement pst2 = conn.prepareStatement(drop_scan2);
			pst2.executeUpdate();
			PreparedStatement pst3 = conn.prepareStatement(drop_scan3);
			pst3.executeUpdate();
			PreparedStatement pst4 = conn.prepareStatement(drop_scan4);
			pst4.executeUpdate();
			conn.close();
		} else
			System.out.println("TABLE NOT FOUND IN DATABASE. CREATE ONE WITH THE REQURED SPECIFICATIONS.");
	}

	private static void generateFrequentItemSets(Statement stmt1) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println("\t\tFREQUENT ITEM SETS");
		System.out.println("\t===============================");
		ResultSet rs_freq2 = stmt1.executeQuery("select * from SCAN2");
		int counter = 1;
		while (rs_freq2.next()) {
			System.out.println("FREQUENT 2 ITEMSET NO. " + counter + ": {" + rs_freq2.getString(2) + ","
					+ rs_freq2.getString(3) + "}");
			counter++;
		}
		ResultSet rs_freq3 = stmt1.executeQuery("select * from SCAN3");
		int counter_1 = 1;
		while (rs_freq3.next()) {
			System.out.println("FREQUENT 3 ITEMSET NO. " + counter_1 + ": {" + rs_freq3.getString(2) + ","
					+ rs_freq3.getString(3) + "," + rs_freq3.getString(4) + "}");
			counter_1++;
		}
		ResultSet rs_freq4 = stmt1.executeQuery("select * from SCAN4");
		int counter_2 = 1;
		while (rs_freq4.next()) {
			System.out.println("FREQUENT 4 ITEMSET NO. " + counter_2 + ": {" + rs_freq4.getString(2) + ","
					+ rs_freq4.getString(3) + "," + rs_freq4.getString(4) + "," + rs_freq4.getString(5) + "}");
			counter_2++;
		}
		System.out.println("==============================================");
	}

	private static double calculateConfidence(double support_txn, double support_val2) {
		// TODO Auto-generated method stub
		return support_txn / support_val2 * 100;
	}

	private static float calculateSupport(float count, int no_of_txns) {
		// TODO Auto-generated method stub
		return count * 100 / no_of_txns;
	}
}