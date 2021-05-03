package Rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import Controller.Resultado;
import Controller.Usuario;
import Model.UsuarioDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@WebServlet("/Autenticacao")
public class Autenticacao extends HttpServlet {
	private static final long serialVersionUID = 2L;

	private static final String FRASE_SEGREDO = "FRASE_SEGREDO_PARA_GERAR_CHAVE_UNICA"
	+ "_PRECISA_SER_UMA_FRASE_COM_AO_MENOS_128_CARACTERES_OU_SEJA_512_BITES_NUNCA_PODE_COMPARTILHADA";
	
	private void enviaResposta(HttpServletResponse response, String json, int codigo) throws IOException {
		response.addHeader("Content-Type", "application/text; charset=UTF-8");
		response.setStatus(codigo);
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(json.getBytes("UTF-8"));
		out.close();
	}
	
    public Autenticacao() {
        super();
    }
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		try {
			String json = request.getReader().lines().collect(Collectors.joining());
			Usuario u = gson.fromJson(json, Usuario.class);
			System.out.println(u.toString());
			UsuarioDao oDao = new UsuarioDao();
			if (oDao.verificar(u)) {
				String token = gerarToken(u.getLogin(), 1);
				enviaResposta(response, gson.toJson(new Resultado(true, "Logado com sucesso.", token)), 200);
			} else {
				enviaResposta(response, gson.toJson(new Resultado(false, "Usuário ou senha inválidos.")), 200);
			}
		} catch (Exception e) {
			e.printStackTrace();
			enviaResposta(response, gson.toJson(new Resultado(false, "Internal Server Error")), 500);
		}
	}
	
	private String gerarToken(String login, Integer expiraEmDias) {
		SignatureAlgorithm algoritimoAssinatura = SignatureAlgorithm.HS512;
		Date agora = new Date();
		Calendar expira = Calendar.getInstance();
		expira.add(Calendar.DAY_OF_MONTH, expiraEmDias);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(FRASE_SEGREDO);
		SecretKeySpec key = new SecretKeySpec(apiKeySecretBytes, algoritimoAssinatura.getJcaName());
		JwtBuilder construtor = Jwts.builder().setIssuedAt(agora)
			.setIssuer(login)
			.signWith(key, algoritimoAssinatura)
			.setExpiration(expira.getTime());
		return construtor.compact();
	}
	
	public boolean validarToken(String authorizationHeader) {
		try {
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return false;
			}
			String token = authorizationHeader.substring("Bearer".length()).trim();
			Claims claims = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(FRASE_SEGREDO)).build()
					.parseClaimsJws(token).getBody();
			if (claims == null) { return false; }
			
			System.out.println("Usuário Logado : " + claims.getIssuer());
		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append(new Gson().toJson(new Resultado(false, "Ops, não esta tentando fazer coisa errada ?")));
	}

}
