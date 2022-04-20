package com.lcomputerstudy.testmvc.controller;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lcomputerstudy.testmvc.service.*;
import com.lcomputerstudy.testmvc.vo.*;

import com.oreilly.servlet.MultipartRequest.*;
@WebServlet("*.do")
public class Controller extends HttpServlet{
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String view = null;
		
		int page = 1;
		int userCount = 0;
		int boardCount = 0;
		int commentCount = 0;
		//로그인
		String pw = null;
		String idx = null;
		//검색
		Search search = null;
		
		//파일 업로드
		String path = null;
		String fileUploadPath = null;
		
		HttpSession session = null;
		command = checkSession(request, response, command);
		
		User user = null;
		Board board = null;
		Comment comment = null;
		Pagination pagination = null;
		
		
		Boardservice boardService = null;
		Commentservice commentservice = null;
		
		
		boolean isRedirected = false;
		
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		switch (command) {
			//user
		//회원리스트
			case "/userlist.do":
				String reqPage = request.getParameter("page");
				if(reqPage != null && !(reqPage.equals(""))) {
					page = Integer.parseInt(reqPage);
				}
				
				UserService userService = UserService.getInstance();
				userCount = userService.getUsersCount();
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(userCount);
				pagination.init();
				List<User> list = userService.getUsers(pagination);
				
				view = "user/list";
				request.setAttribute("list", list);
				request.setAttribute("pagination", pagination);
				break;
				//회원추가
			case "/logininsert.do":
				view = "user/insert";
				break;
			case "/insert-process.do":
				user = new User();
				user.setU_id(request.getParameter("id"));
				user.setU_pw(request.getParameter("pw"));
				user.setU_name(request.getParameter("name"));
				user.setU_tel(request.getParameter("tel") + "-" + request.getParameter("tel2")+"-"+request.getParameter("tel3"));
				user.setU_age(request.getParameter("age"));
				
				userService = UserService.getInstance();
				userService.insertUser(user);
				
				view = "user/insert-result";
				break;	
				//고객 정보
			case "/userdetail.do":
				user = new User();
				user.setU_idx(Integer.parseInt(request.getParameter("u_idx")));
				userService = UserService.getInstance();
				user = userService.getUsers(user);
				view = "userDetail";
				request.setAttribute("user", user);
				break;
				
			case "/userEdit.do":
				user = new User();
				user.setU_idx(Integer.parseInt(request.getParameter("u_idx")));
				userService = UserService.getInstance();
				user = userService.getUsers(user);				
				view = "userEdit";
				request.setAttribute("user", user);
				break;
				
			case "/userEdit-process.do":
				user = new User();
				user.setU_idx(Integer.parseInt(request.getParameter("u_idx")));
				user.setU_id(request.getParameter("u_id"));
				user.setU_pw(request.getParameter("u_pw"));
				user.setU_name(request.getParameter("u_name"));
				user.setU_tel(request.getParameter("u_tel1") + "-" + request.getParameter("u_tel2")+"-"+request.getParameter("u_tel3"));
				user.setU_age(request.getParameter("u_age"));
				
				userService = UserService.getInstance();
				userService.editUser(user);
				
				view = "editProcess";
				request.setAttribute("user", user);
				break;
				
				//고객정보삭제
			case "/userDelete.do":
				user = new User();
				user.setU_idx(Integer.parseInt(request.getParameter("u_idx")));
								
				userService = UserService.getInstance();
				userService.deleteUser(user);
				
				view = "BoardDelete";
				break;
				//회원 로그인
			case "/userlogin.do":
				view = "user/login";
				break;
			case "/login-process.do":
				idx = request.getParameter("login_id");
				pw = request.getParameter("login_password");
				
				userService = UserService.getInstance();
				user = userService.loginUser(idx, pw);
				
				if(user != null) {
					session = request.getSession();
					session.setAttribute("user", user);
					
					view = "user/login-result";
				} else {
					view = "user/login-fail";
				}
				break;
				//로그아웃
			case "/logout.do":
				session = request.getSession();
				session.invalidate();
				view = "user/login";
				break;
				
			case "/access-denied.do":
				view = "user/access-denied";
				break;
			
			//Board
			//게시판 리스트
			case "/boardlist.do":
				//검색창
				search = new Search();
				search.setField(request.getParameter("field"));//select의 option value에 들어감
				search.setValue(request.getParameter("value"));//검색버튼을 누르면 field, query에 값을 넣어줌
				//파일 업로드
				
				//페이지, 리스트
				String reqPage1 = request.getParameter("page");
				if (reqPage1 != null  && !(reqPage1.equals("")) && !(reqPage1.equals("0"))) {
					page = Integer.parseInt(reqPage1);
				}
				//전체 게시판 개수 
				boardService = Boardservice.getInstance();
				boardCount = boardService.getBoardsCount(search); 
				//pagination
				pagination = new Pagination(); 
				pagination.setPage(page);
				pagination.setCount(boardCount);
				pagination.setSearch(search);
				pagination.init();
				List<Board> Boardlist = Boardservice.getBoardlist(pagination, search);
								
				view = "board/boardlist";
				request.setAttribute("board", board);
				request.setAttribute("user", user);
				request.setAttribute("boardlist", Boardlist);
				request.setAttribute("pagination", pagination);
				request.setAttribute("search", search);
				break;
				
			//상세페이지 칸
			case "/boarddetail.do":
				session = request.getSession();
				//user = (User)session.getAttribute("user");
				board = new Board();
				
				board.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				boardService = Boardservice.getInstance();
				board = boardService.getBoard(board);
						
			//	상세페이지 - 댓글 리스트
				String reqPage2 = request.getParameter("page");
				if (reqPage2 != null  && !(reqPage2.equals("")) && !(reqPage2.equals("0"))) {
					page = Integer.parseInt(reqPage2);
				}
				commentservice = Commentservice.getInstance();
				commentCount = commentservice.getCommentsCount(board);
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentlist = Commentservice.getComments(pagination, board);
				board.setCommentList(commentlist);
						
				view = "BoardDetail";
				request.setAttribute("user", user);
				request.setAttribute("board", board);
				request.setAttribute("commentList", commentlist);
				request.setAttribute("pagination", pagination);
				break;
			//수정	
			case "/boardedit.do":
				board = new Board();
				board.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				
				boardService = Boardservice.getInstance();
				board = boardService.getBoard(board);
				
				view = "BoardEdit";
				request.setAttribute("board", board);
				break;
			
			case "/boardedit-process.do":
				board = new Board();
				board.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				board.setB_title(request.getParameter("title"));
				board.setB_content(request.getParameter("content"));
				
				boardService = Boardservice.getInstance();
				boardService.editBoard(board);
				
				view = "BoardEditprocess";
				request.setAttribute("board", board);
				break;
			//삭제하기
			case "/boarddelete.do":
				board = new Board();
				board.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
								
				boardService = Boardservice.getInstance();
				boardService.deleteBoard(board);
				
				view = "BoardDelete";
				break;
			//새로 생성하기
			case "/boardinsert.do":
				view = "board/boardinsert";
				break;
				
			case "/boardinsert-process.do":
				session = request.getSession();
				user = (User)session.getAttribute("user");
				
				board = new Board();
				board.setB_title(request.getParameter("title"));
				board.setB_content(request.getParameter("content"));
				board.setFilename1(request.getParameter("filename1"));
				board.setFilename2(request.getParameter("filename2"));
				board.setU_idx(user.getU_idx());
								
				boardService = Boardservice.getInstance();
				boardService.insertBoard(board);
				
				view = "board/boardinsert-result";
				break;
				
			//답글달기
			case "/boardreply.do":
				board = new Board();
				board.setB_group(Integer.parseInt(request.getParameter("b_group")));
				board.setB_order(Integer.parseInt(request.getParameter("b_order")));
				board.setB_depth(Integer.parseInt(request.getParameter("b_depth")));
				view = "Boardreply";
				request.setAttribute("board", board);
				break;
				
			case "/boardreply-process.do":
				session = request.getSession();
				user = (User)session.getAttribute("user");
				board = new Board();
				user = new User();
				board.setB_content(request.getParameter("content"));
				board.setB_title(request.getParameter("title"));
				board.setB_date(request.getParameter("date"));
				board.setB_group(Integer.parseInt(request.getParameter("b_group")));
				board.setB_order(Integer.parseInt(request.getParameter("b_order"))+1);
				board.setB_depth(Integer.parseInt(request.getParameter("b_depth"))+1);
				board.setU_idx(user.getU_idx());
				boardService = Boardservice.getInstance();
				boardService.replyBoard(board);
				view = "Boardreply-process";
				break;
				
				//댓글달기					
			case "/commentinsert.do":
				comment = new Comment();
				comment.setC_content(request.getParameter("c_content"));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				
				commentservice = Commentservice.getInstance();
				commentservice.insertComment(comment);
				isRedirected = true;
				view = "boarddetail.do?b_idx="+comment.getB_idx();
				break;
				
				//대댓글등록
			case "/commentReply.do":
				//session은 로그인 할때 사용하는 것이므로 필요없음
//				session = request.getSession();
//				board = (Board)session.getAttribute("board");
				//board = new Board();
				comment = new Comment();
				
				comment.setC_content(request.getParameter("c_content"));
				comment.setC_group(Integer.parseInt(request.getParameter("c_group")));
				comment.setC_order(Integer.parseInt(request.getParameter("c_order"))+1);
				comment.setC_depth(Integer.parseInt(request.getParameter("c_depth"))+1);
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				
				commentservice = Commentservice.getInstance();
				commentservice.replyComment(comment);
				isRedirected = true;
				view = "boarddetail.do?b_idx="+comment.getB_idx();
				request.setAttribute("comment", comment);
				break;
				//댓글수정
			case "/commentEdit.do":
				comment = new Comment();
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				commentservice = Commentservice.getInstance();
				comment = commentservice.getComment(comment);
				
				//commentEdit-process.do
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setC_content(request.getParameter("c_content"));
				commentservice = Commentservice.getInstance();
				commentservice.editComment(comment);
				isRedirected = true;
				view = "boarddetail.do?b_idx="+comment.getB_idx();
				request.setAttribute("comment", comment);
				break;
				
				//댓글삭제
			case "/commentDelete.do":
				comment = new Comment();
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				
				commentservice = Commentservice.getInstance();
				commentservice.deleteComment(comment);
				isRedirected = true;
				view = "boarddetail.do?b_idx="+comment.getB_idx();
				break;
			
				//ajax
			case "/aj-commentReply.do":
				comment = new Comment();
				
				comment.setC_content(request.getParameter("c_content"));
				comment.setC_group(Integer.parseInt(request.getParameter("c_group")));
				comment.setC_order(Integer.parseInt(request.getParameter("c_order"))+1);
				comment.setC_depth(Integer.parseInt(request.getParameter("c_depth"))+1);
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				
				commentservice = Commentservice.getInstance();
				commentservice.replyComment(comment);
				commentCount = commentservice.getCommentsCount(comment);
				
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentList = Commentservice.getCommentss(pagination, comment);
				view = "comment/commentlist";
				request.setAttribute("comment", comment);
				request.setAttribute("commentList", commentList);
				request.setAttribute("pagination", pagination);
				break;
				
			case "/aj-commentEdit.do":
				comment = new Comment();
				
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				commentservice = Commentservice.getInstance();
				comment = commentservice.getComment(comment);
				//대댓글 갯수
				commentCount = commentservice.getCommentsCount(comment);
				//commentEdit-process.do
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setC_content(request.getParameter("c_content"));
				commentservice = Commentservice.getInstance();
				commentservice.editComment(comment);
				
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentList1 = Commentservice.getCommentss(pagination, comment);
				view = "comment/commentlist";
				request.setAttribute("comment", comment);
				request.setAttribute("commentList", commentList1);
				request.setAttribute("pagination", pagination);
				break; 
		
			case "/aj-commentDelete.do":
				comment = new Comment();
				
				comment.setC_idx(Integer.parseInt(request.getParameter("c_idx")));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				commentservice = Commentservice.getInstance();
				commentservice.deleteComment(comment);
				//댓글 개수
				commentCount = commentservice.getCommentsCount(comment);
				//Pagination, list
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentList2 = Commentservice.getCommentss(pagination, comment);
				view = "comment/commentlist";
				request.setAttribute("comment", comment);
				request.setAttribute("commentList", commentList2);
				request.setAttribute("pagination", pagination);
				break;
				
			case "/aj-commentInsert.do":
				comment = new Comment();
				
				comment.setC_content(request.getParameter("c_content"));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				commentservice = Commentservice.getInstance();
				commentservice.insertComment(comment);
				
				//댓글 개수
				commentCount = commentservice.getCommentsCount(comment);
				
				//Pagination, list, view
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentList3 = Commentservice.getCommentss(pagination, comment);
				view="comment/commentlist";
				request.setAttribute("comment", comment);
				request.setAttribute("commentList", commentList3);
				request.setAttribute("pagination", pagination);
				break;
				
			//관리자 권한 추가, 제거하기
			case "/aj-levelUpdate.do":
				user = new User();
				
				user.setU_level(request.getParameter("u_level"));
				user.setU_idx(Integer.parseInt(request.getParameter("u_idx")));
				userService = UserService.getInstance();
				userService.levelUpdate(user);
				userCount = userService.getUsersCount();
				
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(userCount);
				pagination.init();
				
				List<User> list1 = userService.getUsers(pagination);
				view="user/list";
				request.setAttribute("user", user);
				request.setAttribute("list", list1);
				request.setAttribute("pagination", pagination);
				break;
				
			case "/aj-commentList.do":
				comment = new Comment();
				board = new Board();
				
				board.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				comment.setB_idx(Integer.parseInt(request.getParameter("b_idx")));
				String reqPage3 = request.getParameter("page");
				if (reqPage3 != null  && !(reqPage3.equals("")) && !(reqPage3.equals("0"))) {
					page = Integer.parseInt(reqPage3);
				}
				//댓글 개수
				commentservice = Commentservice.getInstance();
				commentCount = commentservice.getCommentsCount(comment);
				
				//Pagination, list, view
				pagination = new Pagination();
				pagination.setPage(page);
				pagination.setCount(commentCount);
				pagination.init();
				List<Comment> commentList4 = Commentservice.getCommentss(pagination, comment);
				view="comment/commentlist";
				request.setAttribute("board", board);
				request.setAttribute("comment", comment);
				request.setAttribute("commentList", commentList4);
				request.setAttribute("pagination", pagination);
				break;
		}
		
		if (isRedirected) {
			response.sendRedirect(view);
		} else {
			RequestDispatcher rd = request.getRequestDispatcher(view+".jsp");
			rd.forward(request, response);
		}
	}

	String checkSession(HttpServletRequest request, HttpServletResponse response, String command) {
		HttpSession session = request.getSession();
		
		String[] authList = {
				"/logininsert.do"
				,"/insert-process.do"
				,"/userdetail.do"
				,"/userEdit.do"
				,"/userEdit-process.do"
				,"/logout.do"
			};
		
		for (String item : authList) {
			if (item.equals(command)) {
				if (session.getAttribute("user") == null) {
					command = "/access-denied.do";
				}
			}
		}
		return command;
	}
}