function adicionarCliente() {
    let nome = document.getElementById('nome').value;
    let telefone = document.getElementById('telefone').value;
    let email = document.getElementById('email').value

    if (nome === '' || telefone === '' || email === '') {
        alert('Preencha todos os campos!');
        return;
    }
    if (telefone.length !== 11) {
        alert('numero excedido');
        return;
    }
    telefone = Number(telefone);

    if (typeof telefone !== 'number') {
        alert('coloque apenas numeros por favor coloque apenas 15 numeros');
        return;
    }
    

    let tabela = document.getElementById('listaClientes');
    let novaLinha = tabela.insertRow();

    novaLinha.insertCell(0).innerText = nome;
    novaLinha.insertCell(1).innerText = telefone;
    novaLinha.insertCell(2).innerText = email;

    let acaoCell = novaLinha.insertCell(3);
    let botaoExcluir = document.createElement('button');

    botaoExcluir.innerText = 'Excluir';
    botaoExcluir.classList.add('btn-excluir');

    botaoExcluir.onclick = function () {
        tabela.deleteRow(novaLinha.rowIndex - 1);
    };
    acaoCell.appendChild(botaoExcluir);

    document.getElementById('nome').value = '';
    document.getElementById('telefone').value = '';
    document.getElementById('email').value = '';
}