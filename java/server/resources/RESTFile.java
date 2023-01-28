package server.resources;

import java.io.*;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import com.sun.jersey.core.header.FormDataContentDisposition;
//import com.sun.jersey.multipart.FormDataParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AttachmentDao;
import dao.AuthenticationDao;
import dao.MessageDao;
import dao.SessionTokenDao;
import server.model.AttachmentNames;
import server.model.MessageServer;
import server.model.User;

@Path("/file")
public class RESTFile {
	private AuthenticationDao authDao = new AuthenticationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private AttachmentDao attDao = new AttachmentDao();


	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@QueryParam("token") String token, String jsonBody) {
		System.out.println("Add attachment called!\n" + jsonBody);
		if(token == null || token.isBlank() || sessTokenDao.getByToken(token) == null) { // Unnathorized
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null || !user.isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		AttachmentNames at = null;
		try {
			at = new ObjectMapper().readValue(jsonBody, AttachmentNames.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		attDao.create(at);
		return Response.status(Response.Status.OK).build();
	}

	/*@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadMelin(@FormDataParam("file") InputStream in,
						   @FormDataParam("file") FormDataContentDisposition dis) {
		saveToServer(in, dis);
		System.out.println("");
		return "Ura";
	}

	private void saveToServer(InputStream in, FormDataContentDisposition dis) {
		String uploadTo = uploadFolder + dis.getFileName();
		try {
			OutputStream out = new FileOutputStream(uploadTo);
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
  	}
	/*
	private void saveToServer(InputStream uploadStram) {
		String uploadLocation = "/Kick-in/src/main/webapp/files" + fileDisposition.getName();
		try {
			OutputStream out = new FileOutputStream(new File(uploadLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = uploadStram.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}