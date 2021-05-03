var CodigoConsulta = 0;

function listarConsulta() {
	var tab = document.getElementById("tabConsulta");
	for(var i = tab.rows.length -1; i >= 0 ; i--) {
		tab.deleteRow(i);
	}
	
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Consulta", {method: "GET", headers: myHeaders})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			for(const item of data.resultado) {
				var tab = document.getElementById("tabConsulta");
				var row = tab.insertRow(-1);
				row.insertCell(-1).innerHTML = item.data.day + "/" + item.data.month + "/" + item.data.year;
				row.insertCell(-1).innerHTML = item.horario.hour + ":" + item.horario.minute;
				row.insertCell(-1).innerHTML = item.paciente.nome;
				row.insertCell(-1).innerHTML = "<div class='btn-group' role='group'>\n"
					+ '<button type="button" class="btn btn-dark" onclick="openModalConsulta(' + item.codigo + ')"><span class="material-icons fs-6">edit</span></button>\n'
					+ '<button type="button" class="btn btn-danger" onclick="deletarConsulta(' + item.codigo + ')"><span class="material-icons fs-6">delete</span></button>\n'
					+ '</div>';
			}
		} else {
			alert("Erro : " + data.mensagem);
		}
	})
}

function modalListar() {
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Paciente", {method: "GET", headers: myHeaders})
    .then(Response => Response.json())
    .then(data => {
		if(data.condicao) {
			select = document.getElementById("cxCliente");
			var dados = "";
			for(const item of data.resultado) {
				dados = dados + "<option value='" + JSON.stringify(item) + "'>" + item.nome + "</option>\n"
			}
			select.innerHTML = dados;
		} else {
			alertify.error(data.mensagem);
		}
    })
}

function openModalConsulta(codigo) {
	this.CodigoConsulta = codigo;
	veriLogin();
	modalListar();
	if(codigo == 0) {
		document.getElementById("cxValor").value = 0;
		$("#cxData").datepicker('clearDates');
		document.getElementById("cxHora").value = 10;
		document.getElementById("cxMinuto").value = 30;
	} else {
		var myHeaders = new Headers();
		myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
		
		fetch("/Consultorio/API/Consulta/" + this.CodigoConsulta, {method: "GET", headers: myHeaders})
		.then(response => response.json())
		.then(data => {
			$("#cxData").datepicker('setDate', new Date(data.resultado.data.year, data.resultado.data.month - 1, data.resultado.data.day));
			document.getElementById("cxHora").value = data.resultado.horario.hour;
			document.getElementById("cxMinuto").value = data.resultado.horario.minute;
			document.getElementById("cxValor").value = data.resultado.valor;
			document.getElementById("cxCliente").value = JSON.stringify(data.resultado.paciente);
		})	
	}
	
	var myModal = new bootstrap.Modal(document.getElementById('cadConsulta'), {
		backdrop:'static',
		keyboard: false
	})
	myModal.show();
	
}

function salvarConsulta() {
	var p = ConsultaToArray();
	
	var json = JSON.stringify(p);
	var metodo = "POST";
	if(this.CodigoConsulta == 0) {
		metodo = "POST";
	} else {
		metodo = "PUT";
	}
	
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Consulta", {method: metodo, headers: myHeaders, body: json})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			alertify.success(data.mensagem);
			document.getElementById("cbFechar").click();
			listarConsulta();
		} else {
			alertify.error(data.mensagem);
		}
	})
}

function deletarConsulta(codigo) {
	var myHeaders = new Headers();
	myHeaders.append('AUTHORIZATION', "Bearer " + sessionStorage.getItem("Token"));
	
	fetch("/Consultorio/API/Consulta/" + codigo, {method: "DELETE", headers: myHeaders})
	.then(response => response.json())
	.then(data => {
		if(data.condicao) {
			alertify.success(data.mensagem);
			listarConsulta();
		} else {
			alertify.error(data.mensagem);
		}
	})
}

function ConsultaToArray() {
	var saida = {
		codigo: this.CodigoConsulta,
		data: {
			year: parseInt($("#cxData").datepicker('getDate').toLocaleDateString().substring(6, 10)),
			month: parseInt($("#cxData").datepicker('getDate').toLocaleDateString().substring(3, 5)),
			day: parseInt($("#cxData").datepicker('getDate').toLocaleDateString().substring(0, 2))
		},
		valor: parseFloat(document.getElementById("cxValor").value),
		horario: {
                "hour": parseInt(document.getElementById("cxHora").value),
                "minute": parseInt(document.getElementById("cxMinuto").value),
                "second": 0,
                "nano": 0
            },
		paciente: JSON.parse(document.getElementById("cxCliente").value)
	};
	return saida;
}
