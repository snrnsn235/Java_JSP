<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록2</title>
</head>
<style>
	h1 {
		text-align:center;
	}
	table {
		border-collapse:collapse;
		margin:40px auto;
	}
	table tr th {
		font-weight:700;
	}
	table tr td, table tr th {
		border:1px solid #818181;
		width:200px;
		text-align:center;
	}
	
	a {
		text-decoration:none;
		color:#000;
		font-weight:700;
	}
	
	ul {
		width:600px;
		height:50px;
		margin:10px auto;
	}
	li {
		list-style:none;
		width:50px;
		line-height:50px;
		border:1px solid #ededed;
		float:left;
		text-align:center;
		margin:0 5px;
		border-radius:5px;
	}
</style>
<body>
	<h1>게시판 목록</h1>
	<table>
		<tr>
			<td colspan="3">전체 게시판 개수 : ${pagination.count }</td>
		</tr>
		<tr>
			<th>번호</th>
			<th>ID</th>
			<th>이름</th>
			<th>내용</th>
		</tr>
		<c:forEach items="${boardlist}" var="board" varStatus = "status">
			<tr>
				<td><a href="BoardDetail.jsp?b_idx=${board.b_idx}">${board.rownum}</a></td>
				<td>${board.b_id }</td>
				<td>${board.b_writer }</td>
				<td>${board.b_content }</td>
			</tr>
		</c:forEach>
	</table>
	<div>
		<ul>
			<c:choose>
					<c:when test="${pagination.startPage-1 != 0}">
						<li style="">
							<a href="board-boardlist.do?page=${pagination.prevPage}">◀</a>
						</li>
					</c:when>
			</c:choose>
			<c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}" step="1">
					<c:choose>
						<c:when test="${pagination.page eq i }">
							<li style="background-color:#ededed;">
								<span>${i}</span>
							</li>
						</c:when>
						<c:when test="${ pagination.page ne i }">
							<li>
								<a href="board-boardlist.do?page=${i}">${i}</a>
							</li>
						</c:when>
					</c:choose>
				</c:forEach>
			<c:choose>
					<c:when test = "${pagination.nextPage eq pagination.lastPage }">
						<li style="">
							<a href="board-boardlist.do?page=${pagination.nextPage}">▶</a>
						</li>
					</c:when>
				</c:choose>	
		</ul>
	</div>
</body>
</html>