<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 값 넘겨받기</title>
</head>
<body>
<%@ include file="db_connection.jsp" %>
<%
	request.setCharacterEncoding("UTF-8"); //한글깨짐 방지
	
	String title = request.getParameter("title");
	String content = request.getParameter("content");
	String writer = request.getParameter("writer");
	String date = request.getParameter("date");
	String hits  = request.getParameter("hits");
	PreparedStatement pstmt = null;
	
	try {
		String sql = "insert into board(b_title,b_content,b_date,b_writer,b_hits) values(?,?,?,?,?)";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, title);
		pstmt.setString(2, content);
		pstmt.setString(3, writer);
		pstmt.setString(4, date);
		pstmt.setString(5, hits);
		pstmt.executeUpdate();
	} catch (SQLException ex) {
		System.out.println("SQLException : "+ex.getMessage());
	} finally {
		if(pstmt != null) {
			pstmt.close();
		}
		if(conn != null) {
			conn.close();
		}
	}
%>
<h3>저장 완료</h3>
</body>
</html>