# Olá 👋 !

Trabalho referente ao projeto final do módulo de Selenium/Webdriver (Java) da turma de Automação de Testes da Let´s Code by Ada #SantanderCoders.
O projeto está baseado no repositório do professor William Cesar Santos e pode ser acessado aqui: https://github.com/WilliamCesarSantos/schedule


# Regras
## Usuário
### Nas páginas Web teremos o cadastro/lista de usuários na url http://localhost:8080/app/users onde é possível criar, listar e atualizar os usuário.

- Não deve ser possível cadastrar dois usuário com o mesmo username.
- Não deve ser permitido alterar o username do usuário após o cadastro.
- Não deve ser permitido alterar o username do usuário após o cadastro.
- A listagem deve exibir todos os usuários já cadastrados, sem nenhum tipo de filtro.
- Na listagem deve ser possível ver o name, username e ter acesso a página de edição.
- A senha antiga não deve ser carregada na edição do usuário, para evitar que seja exposta.
- É permitido atualizar name e password do usuário.
- Não deve ser possível apagar usuário já cadastrados.

## Tarefas
### A parte de tarefas ainda não temos uma tela, teremos apenas uma API no endereço http://localhost:8080/api/tasks onde é possível criar, listar e atualizar as task.
- Todas as tarefas recém cadastradas devem ter o status igual a OPEN.
- Não deve ser possível cadastrar uma tarefa com status igual a CLOSE.
- A tarefa deve estar vinculada a um usuário. Esse vinculo acontece no payload de criação.
- Toda tarefa cadastrada deve ter a informação de title preenchida, sendo essa uma informação obrigatória.
- A descrição da tarefa é opcional e pode ser preenchida em um segundo momento utilizando o verbo PUT.
- O usuário, a qual destina-se a atividade, também pode ser alterado.
- Após a conclusão de uma tarefa, essa pode ser encerrada chamando o PUT da API e trocando o status para CLOSE.
- Ao encerrar uma terafa deve ser registrado a data em "closedAt"
- Nenhuma tarefa encerrada pode sofrer qualquer alteração.
- Não deve ser possível apagar uma atividade já cadastrada. Permanecerá para histórico.

