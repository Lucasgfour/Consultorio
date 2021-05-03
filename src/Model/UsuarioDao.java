package Model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import Controller.Usuario;


public class UsuarioDao {
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConexaoHibernate");
	private static EntityManager em = emf.createEntityManager();
	
	public boolean verificar(Usuario u) {
		Query query = em.createQuery("SELECT p FROM Usuario p WHERE p.login = :login AND p.senha = :senha");
		@SuppressWarnings("unchecked")
		List<Usuario> lista = query
			.setParameter("login", u.getLogin())
			.setParameter("senha", u.getSenha())
			.getResultList();
		if(lista.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
