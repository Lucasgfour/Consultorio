var CodigoPaciente = 0;

function listarPaciente() {
	var tab = document.getElementById("tabPaciente");
	for(var i = tab.rows.length -1; i >= 0 ; i--) {
		tab.deleteRow(i);
	}
	
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Paciente", {method: "GET", headers: myHeaders})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			for(const item of data.resultado) {
				var tab = document.getElementById("tabPaciente");
				var row = tab.insertRow(-1);
				row.insertCell(-1).innerHTML = item.codigo;
				row.insertCell(-1).innerHTML = item.nome;
				row.insertCell(-1).innerHTML = item.telefone;
				row.insertCell(-1).innerHTML = "<div class='btn-group' role='group'>\n"
					+ '<button type="button" class="btn btn-dark" onclick="openModalPaciente(' + item.codigo + ')"><span class="material-icons fs-6">edit</span></button>\n'
					+ '<button type="button" class="btn btn-danger" onclick="deletarPaciente(' + item.codigo + ')"><span class="material-icons fs-6">delete</span></button>\n'
					+ '</div>';
			}
		} else {
			alert("Erro : " + data.mensagem);
		}
	})
}

function openModalPaciente(codigo) {
	this.CodigoPaciente = codigo;
	veriLogin();
	if(codigo == 0) {
		nome: document.getElementById("pxNome").value = "";
		telefone: document.getElementById("pxTelefone").value = "";
	} else {
		var myHeaders = new Headers();
		myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
		
		fetch("/Consultorio/API/Paciente/" + this.CodigoPaciente, {method: "GET", headers: myHeaders})
		.then(response => response.json())
		.then(data => {
			if(data.condicao) {
				document.getElementById("pxNome").value = data.resultado.nome;
				document.getElementById("pxTelefone").value = data.resultado.telefone;
			} else {
				alert("Erro : " + data.mensagem);
			}
		})	
	}
	
	var myModal = new bootstrap.Modal(document.getElementById('cadPaciente'), {
		backdrop:'static',
		keyboard: false
	})
	myModal.show();
}

function salvarPaciente(){
	var p = pacienteToArray();
	
	var json = JSON.stringify(p);
	var metodo = "POST";
	if(this.CodigoPaciente == 0) {
		metodo = "POST";
	} else {
		metodo = "PUT";
	}
	
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Paciente", {method: metodo, headers: myHeaders, body: json})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			alertify.success(data.mensagem);
			document.getElementById("pbFechar").click();
			listarPaciente();
		} else {
			alertify.error(data.mensagem);
		}
	})
}

function deletarPaciente(codigo) {
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Paciente/" + codigo, {method: "DELETE", headers: myHeaders})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			alertify.success(data.mensagem);
			listarPaciente();
		} else {
			alertify.error(data.mensagem);
		}
	})
}

function pacienteToArray() {
	var saida = {
		codigo: this.CodigoPaciente,
		nome: document.getElementById("pxNome").value,
		telefone: document.getElementById("pxTelefone").value
	};
	return saida;
}