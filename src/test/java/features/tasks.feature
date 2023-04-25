Feature: Tarefas
  Scenario: Todas as tarefas recém cadastradas devem ter o status igual a open
    Given que eu não tenho uma tarefa cadastrada
      | title | description  | userId |
      | task1 | Minha tarefa | 1      |
    When eu registro a tarefa
    Then eu encontro a tarefa cadastrada com status como OPEN
    And e a resposta deve ter o status code igual a 201

  Scenario: Não deve ser possível cadastrar uma tarefa com status igual a CLOSE.
    Given que eu não tenho uma tarefa cadastrada
      | title     | description    | userId | status |
      | TaskClose | Minha tarefa 2 | 1      | CLOSE  |
    When eu registro a tarefa
    Then eu não encontro a tarefa cadastrada
    And e a resposta deve ter o status code igual a 422

  Scenario: A tarefa deve estar vinculada a um usuário. Esse vinculo acontece no payload de criação.
    Given que eu não tenho uma tarefa cadastrada
      | title           | description    |
      | TaskSemUsuario  | Minha tarefa 3 |
    When eu registro a tarefa
    Then eu não encontro a tarefa cadastrada
    And e a resposta deve ter o status code igual a 400

  Scenario: Toda tarefa cadastrada deve ter a informação de title preenchida, sendo essa uma informação obrigatória.
    Given que eu não tenho uma tarefa cadastrada
      | userId          | description    |
      | 1               | Minha tarefa 4 |
    When eu registro a tarefa
    Then eu não encontro a tarefa cadastrada
    And e a resposta deve ter o status code igual a 400

  Scenario: A descrição da tarefa é opcional e pode ser preenchida em um segundo momento utilizando o verbo PUT.
    Given que eu não tenho uma tarefa cadastrada
      | title                    | userId |
      | taskAtualizaDescricao    | 1      |
    When eu registro a tarefa
    And eu atualizo a tarefa com uma nova descrição
    Then eu encontro a tarefa atualizada com nova descrição
    And e a resposta deve ter o status code igual a 200

  Scenario: O usuário, a qual destina-se a atividade, também pode ser alterado.
    Given que eu não tenho uma tarefa cadastrada
      | title               |
      | taskAtualizaUsuario |
    When eu registro a tarefa
    And eu atualizo a tarefa com um novo usuário de id 1
    Then eu encontro a tarefa atualizada com vinculo de usuário
    And e a resposta deve ter o status code igual a 200

  Scenario: Após a conclusão de uma tarefa, essa pode ser encerrada chamando o PUT da API e trocando o status para CLOSE.
    Given que eu não tenho uma tarefa cadastrada
      | title              | description    | userId | status |
      | TaskAtualizaStatus | Minha tarefa 7 | 1      | OPEN   |
    When eu registro a tarefa
    And eu atualizo a tarefa para status "CLOSE"
    Then eu encontro a tarefa atualizada com novo status
    And e a resposta deve ter o status code igual a 200

  Scenario: Ao encerrar uma terafa deve ser registrado a data em closedAt
    Given que eu não tenho uma tarefa cadastrada
      | title                     | description    | userId | status |
      | TaskAtualizaDatFechamento | Minha tarefa 8 | 1      | OPEN   |
    When eu registro a tarefa
    And eu atualizo a tarefa para status "CLOSE"
    Then eu encontro a tarefa atualizada com nova data de fechamento
    And e a resposta deve ter o status code igual a 200

  Scenario: Nenhuma tarefa encerrada pode sofrer qualquer alteração.
    Given que eu já tenho uma tarefa cadastrada
      | title                     | userId |
      | TaskAtualizaDatFechamento | 1      |
    When eu atualizo a tarefa com um novo título
    Then não é possível atualizar a tarefa
    And e a resposta deve ter o status code igual a 422

  Scenario: Não deve ser possível apagar uma atividade já cadastrada. Permanecerá para histórico.
    Given que eu já tenho uma tarefa cadastrada
      | title | userId |
      | task1 | 1      |
    When eu solicito para deletar a tarefa
    Then não é possível deletar a tarefa
    And e a resposta deve ter o status code igual a 405