function onLoad() {
	$('#cxData').datepicker({
		format: "mm/dd/yyyy",
		maxViewMode: 2,
		todayBtn: "linked",
		language: "pt-BR",
		todayHighlight: true
	});
	
	$(function() {
		$('#cxValor').maskMoney();
	})
	
	var sHora = "";
	for(var i = 0;i < 24; i++) {
		sHora = sHora + "<option value='" + i + "' selected>" + i + "</option>\n"
	}
	$("#cxHora").html(sHora);
	
	var sMinuto = "";
	for(var i = 00;i <= 50; i+=10) {
		sMinuto = sMinuto + "<option value='" + i + "' selected>" + i + "</option>\n"
	}
	$("#cxMinuto").html(sMinuto);
	
	veriLogin();
}

function veriLogin() {
	if(sessionStorage.getItem("Token") == undefined) {
		var myModal = new bootstrap.Modal(document.getElementById('loginScreen'), {
			backdrop:'static',
			keyboard: false
		})
		myModal.show();
	}
}

function sair() {
	sessionStorage.removeItem("Token");
	onLoad();
}

function logar() {
	var login = {
		codigo: 0,
		login: document.getElementById("txUsuario").value,
		senha: document.getElementById("txSenha").value
	};
	var loginJson = JSON.stringify(login);
	
	fetch("/Consultorio/Autenticacao", {method: "POST", body: loginJson})
	.then(response => response.json())
	.then(result => {
		if(result.condicao) {
			sessionStorage.setItem("Token", result.resultado);
			alertify.success("Logado com sucesso !");
			document.getElementById("fecharLogin").click();
		} else {
			alertify.error(result.mensagem);	
		}
	})
	.catch(error => {
		alertify.error("Usuário ou senha inválidos " + error);
	});
}