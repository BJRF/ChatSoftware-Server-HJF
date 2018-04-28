package com.wjy.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Link implements Runnable {
	Socket socket = null;
	// String name = null;
	ArrayList<Link> list = null;
	OutputStream out = null;
	BufferedReader read = null;
	InputStream in = null;
	int targetid;
	String name = null;
	String password = null;

	public Link(Socket socket, ArrayList<Link> list) {
		this.socket = socket;
		try {
			this.out = socket.getOutputStream();
			in = socket.getInputStream();
			this.read = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.list = list;
	}

	final String d = "com.mysql.jdbc.Driver";
	final String url = "jdbc:mysql://localhost/Dinner";
	final String user = "root";
	final String password1 = "123456";
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet rs = null;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String str = null;
		// ��ȡ��Ϣ
		try {
			str = read.readLine();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("�������յ���" + str);
		// ������ֻ�������
		if (str.equals("longin")) {
			System.out.println("�ֻ��ͻ��������¼");
			String str1 = null;
			try {
				str1 = read.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			char[] c = str1.toCharArray();
			int one = -1;
			// �ҵ���һ����ʶ��
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '|') {
					one = i;
					break;
				}
			}
			System.out.println(one);
			name = new String(c, 0, one);
			System.out.println("�յ��ֻ�������name:" + name);
			password = new String(c, one + 1, c.length - one - 1);
			System.out.println("�յ��ֻ������password:" + password);
			try {
				Class.forName(d);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(url, user, password1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				// �ҵ���id��Ӧ��password��name
				System.out.println("���ݿ�");
				// System.out.println(id);
				state = conn
						.prepareStatement("select password,name from userinfo where name="
								+ name);
				rs = state
						.executeQuery("select password,name from userinfo where name="
								+ name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				// ���rs��password�����û�����password
				// System.out.println(rs.getString("password"));
				if (rs.next() && password.equals(rs.getString("password"))) {
					System.out.println(name + "��¼success");
					// ��ͻ��˷��ͳɹ�
					out.write("OK\r".getBytes());
					// ���ݿ��id��name��������������id��name
				} else {
					System.out.print("fail");
					out.write("fail\r".getBytes());
					return;
				}

			} catch (SQLException | IOException e) {
				e.printStackTrace();

			}

		}
		if (str.equals("Register")) {
			System.out.println("�ֻ��ͻ�������ע��");
			String str1 = null;
			try {
				str1 = read.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			char[] c = str1.toCharArray();
			int one = -1;
			// �ҵ���һ����ʶ��
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '|') {
					one = i;
					break;
				}
			}
			System.out.println(one);
			name = new String(c, 0, one);
			System.out.println("�յ��ֻ�������name:" + name);
			password = new String(c, one + 1, c.length - one - 1);
			System.out.println("�յ��ֻ������password:" + password);
			try {
				Class.forName(d);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(url, user, password1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				// ������˺��Ѵ��ڣ�������ע��
				PreparedStatement state1 = null;
				state = conn.prepareStatement("select password,name from userinfo where name=" + name);
				rs = state.executeQuery("select password,name from userinfo where name=" + name);
				
				
				if (!rs.next()) {
					state = conn
							.prepareStatement("insert into userinfo(name,password) values('"
									+ name + "','" + password + "')");
					int result = state.executeUpdate();
					if (result == 1) {
						out.write("OK\r".getBytes());
						return;
					} else {
						out.write("fail\r".getBytes());
						return;
					}
				}else{
					out.write("fail\r".getBytes());
					return;
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					out.write("fail\r".getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
		}

	}
}
