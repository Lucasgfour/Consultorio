package Controller;

public class Resultado {
	
	private Boolean condicao;
	private String mensagem;
	private Object resultado;
	
	public Resultado() { 
		
	}
	
	public Resultado(Boolean condicao, String mensagem) {
		this.condicao = condicao;
		this.mensagem = mensagem;
	}
	
	public Resultado(Boolean condicao, String mensagem, Object resultado) {
		this.condicao = condicao;
		this.mensagem = mensagem;
		this.resultado = resultado;
	}
	
	public Boolean getCondicao() {
		return condicao;
	}
	public void setCondicao(Boolean condicao) {
		this.condicao = condicao;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Object getResultado() {
		return resultado;
	}
	public void setResultado(Object saida) {
		this.resultado = saida;
	}
	
	
	
	
}
