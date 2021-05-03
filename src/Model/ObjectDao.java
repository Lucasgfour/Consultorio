package Model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class ObjectDao {
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConexaoHibernate");
	private static EntityManager em = emf.createEntityManager();
	
	public List<Object> listar(String tabela) {
		Query query = em.createQuery("SELECT a FROM " + tabela + " a");
		List<Object> lista =  (List<Object>) query.getResultList();
		return lista;
	}
	
	public void inserir(Object p) {
		em.getTransaction().begin();
		em.persist(p);
		em.getTransaction().commit();
	}
	
	public void alterar(Object p) {
		em.getTransaction().begin();
		em.merge(p);
		em.getTransaction().commit();
	}
	
	public void excluir(Object p) {
		em.getTransaction().begin();
		em.remove(em.merge(p));
		em.getTransaction().commit();
	}
	
	public Object consultar(int codigo, Object obj) {
		Object p = em.find(obj.getClass(), codigo);
		return p;
	}
}
