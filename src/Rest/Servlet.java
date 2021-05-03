package Rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import Controller.Consulta;
import Controller.Paciente;
import Controller.Resultado;
import Model.ObjectDao;

@WebServlet(urlPatterns = {"/API/*"}, name = "objectServlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Servlet() { super(); }
    
    private void enviaResposta(HttpServletResponse response, String json, int codigo) throws IOException {
		response.addHeader("Content-Type", "application/json; charset=UTF-8");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
		
		response.setStatus(codigo);
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(json.getBytes("UTF-8"));
		out.close();
	}
    
    private Object getObject(String objeto) {
    	if(objeto.equals("Paciente")) {
    		return new Paciente();
    	} else if(objeto.equals("Consulta")) {
    		return new Consulta();
    	} else {
    		return null;
    	}
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson saida = new Gson();
		ObjectDao oDao = new ObjectDao();
		String authorizationHeader = request.getHeader("AUTHORIZATION");
	    Autenticacao autenticacao = new Autenticacao();
		if(request.getPathInfo() == null) {
			enviaResposta(response, saida.toJson(new Resultado(false, "Object não especificado !")), 200);
		} else if (!autenticacao.validarToken(authorizationHeader)) {
	    	enviaResposta(response, saida.toJson(new Resultado(false, "Token inválido !")), 200);
	    	return;
		} else {
			String[] Objeto = request.getPathInfo().substring(1).split("/");
			if(getObject(Objeto[0]) == null) {
				enviaResposta(response, saida.toJson(new Resultado(false, "Object inválido !")), 200);
			} else {
				if(Objeto.length == 1) {
					enviaResposta(response, saida.toJson(new Resultado(true, "Sucesso",oDao.listar(getObject(Objeto[0]).getClass().getTypeName()))), 200);
				} else {
					try {
						int id = Integer.parseInt(Objeto[1]);
						Object objSaida = oDao.consultar(id, getObject(Objeto[0]));
						
						if(objSaida == null) {
							enviaResposta(response, saida.toJson(new Resultado(false, "Favor informar um ID válido !")), 200);
						} else {
							enviaResposta(response, saida.toJson(new Resultado(true, "Consulta ao ID : " + id, objSaida)), 200);
						}
						
					} catch (Exception eGet) {
						enviaResposta(response, saida.toJson(new Resultado(false, "erro, detalhes :" + eGet.getMessage(), oDao.listar(Objeto[0]))), 500);
						eGet.printStackTrace();
					}
				}
			}
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson saida = new Gson();
		ObjectDao oDao = new ObjectDao();
		String authorizationHeader = request.getHeader("AUTHORIZATION");
	    Autenticacao autenticacao = new Autenticacao();
		if(request.getPathInfo() == null) {
			enviaResposta(response, saida.toJson(new Resultado(false, "Object não especificado !")), 200);
		} else if (!autenticacao.validarToken(authorizationHeader)) {
	    	enviaResposta(response, saida.toJson(new Resultado(false, "Token inválido !")), 401);
	    	return;
		} else {
			String[] Objeto = request.getPathInfo().substring(1).split("/");
			if(getObject(Objeto[0]) == null) {
				enviaResposta(response, saida.toJson(new Resultado(false, "Object inválido !")), 200);
			} else {
				Object novo = saida.fromJson(request.getReader().lines().collect(Collectors.joining()), getObject(Objeto[0]).getClass());
				
				try {
					oDao.inserir(novo);
					enviaResposta(response, saida.toJson(new Resultado(true, "Inserido com sucesso !", saida.toJson(novo))), 200);
				} catch(Exception ePost) {
					enviaResposta(response, saida.toJson(new Resultado(false, "Erro, detalhes : " + ePost.getMessage())), 200);
					ePost.printStackTrace();
				}
			}
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson saida = new Gson();
		ObjectDao oDao = new ObjectDao();
		String authorizationHeader = request.getHeader("AUTHORIZATION");
	    Autenticacao autenticacao = new Autenticacao();
		if(request.getPathInfo() == null) {
			enviaResposta(response, saida.toJson(new Resultado(false, "Object não especificado !")), 200);
		} else if (!autenticacao.validarToken(authorizationHeader)) {
	    	enviaResposta(response, saida.toJson(new Resultado(false, "Token inválido !")), 401);
	    	return;
		} else {
			String[] Objeto = request.getPathInfo().substring(1).split("/");
			if(getObject(Objeto[0]) == null) {
				enviaResposta(response, saida.toJson(new Resultado(false, "Object inválido !")), 200);
			} else {
				if(Objeto.length == 1) {
					enviaResposta(response, saida.toJson(new Resultado(false, "Código não especificado !")), 200);
				} else {
					try {
						int id = Integer.parseInt(Objeto[1]);
						Object del = getObject(Objeto[0]);
						
						Method metodo = del.getClass().getMethod("setCodigo", int.class);
						metodo.invoke(del, id);
						
						oDao.excluir(del);
						enviaResposta(response, saida.toJson(new Resultado(true, "Deletado com sucesso (" + id + ").")), 200);
					} catch (Exception eDelete) {
						enviaResposta(response, saida.toJson(new Resultado(false, "Erro, detalhes :" + eDelete.getMessage())), 200);
						eDelete.printStackTrace();
					}
				}
			}
		}
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson saida = new Gson();
		ObjectDao oDao = new ObjectDao();
		String authorizationHeader = request.getHeader("AUTHORIZATION");
	    Autenticacao autenticacao = new Autenticacao();
		if(request.getPathInfo() == null) {
			enviaResposta(response, saida.toJson(new Resultado(false, "Object não especificado !")), 200);
		} else if (!autenticacao.validarToken(authorizationHeader)) {
	    	enviaResposta(response, saida.toJson(new Resultado(false, "Token inválido !")), 401);
	    	return;
		} else {
			String[] Objeto = request.getPathInfo().substring(1).split("/");
			if(getObject(Objeto[0]) == null) {
				enviaResposta(response, saida.toJson(new Resultado(false, "Object inválido !")), 200);
			} else {
				Object obj = saida.fromJson(request.getReader().lines().collect(Collectors.joining()), getObject(Objeto[0]).getClass());
				try {
					oDao.alterar(obj);
					enviaResposta(response, saida.toJson(new Resultado(true, "Alterado com sucesso !")), 200);
				} catch(Exception ePut) {
					enviaResposta(response, saida.toJson(new Resultado(false, "Erro, detalhes : " + ePut.getMessage())), 200);
					ePut.printStackTrace();
				}
			}
		}
	}
	
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		enviaResposta(response, gson.toJson(new Resultado(true, "Options")), 200);
	}

}
