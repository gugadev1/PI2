# Projeto Integrador I - Documentacao Tecnica

Responsavel: Gusthavo Gassen Tassinari  
Nome do sistema: TechNova Solucoes Desktop  
Versao: 1.0

## 1. Apresentacao
O sistema TechNova Solucoes Desktop foi idealizado para modernizar o gerenciamento de lojas de informatica em ambiente local (computador unico ou rede local), substituindo planilhas e controles manuais por uma aplicacao integrada em Java com banco de dados MySQL.

A proposta central e unificar o cadastro de produtos, clientes e pedidos em um unico sistema, reduzindo erros de estoque, acelerando o atendimento e melhorando o rastreamento das vendas.

## 2. Descricao do Projeto
A aplicacao sera um sistema desktop com interface grafica (Java Swing) e persistencia em banco relacional MySQL. O sistema seguira organizacao em camadas simples:
- Camada de apresentacao (telas)
- Camada de regras de negocio (servicos)
- Camada de acesso a dados (DAO)

Registros principais armazenados no banco:
- Produtos
- Clientes
- Pedidos (com itens do pedido)

Telas previstas (minimo de 3 funcionalidades):
- Tela de login
- Tela de cadastro/consulta de produtos
- Tela de cadastro/consulta de clientes
- Tela de criacao e acompanhamento de pedidos

## 3. Descricao dos Usuarios
- Administrador:
  - Cadastra e remove produtos e usuarios
  - Visualiza relatorios e historico geral de pedidos
  - Altera status de pedidos
- Funcionario (vendas):
  - Cadastra clientes
  - Registra pedidos
  - Atualiza status de atendimento e entrega
- Cliente (atendimento presencial ou remoto):
  - Possui cadastro para vinculo de compras e historico
  - Consulta status de pedidos por meio da equipe

## 4. Necessidades Observadas e Regras de Negocio
- O estoque deve ser atualizado automaticamente quando um pedido for confirmado.
- Nao e permitido finalizar pedido com item sem estoque suficiente.
- Todo pedido precisa estar vinculado a um cliente cadastrado.
- Cada pedido deve possuir ao menos um item.
- O status do pedido deve seguir fluxo controlado:
  - EM_PROCESSAMENTO -> APROVADO -> ENVIADO -> ENTREGUE
- Somente Administrador e Funcionario podem alterar status de pedido.

## 5. Requisitos Funcionais
- RF001 - Autenticacao de Usuario:
  - O sistema deve permitir login com email e senha para Administrador e Funcionario.
- RF002 - Gerenciamento de Produtos:
  - O sistema deve permitir cadastrar, editar, remover e listar produtos.
- RF003 - Gerenciamento de Clientes:
  - O sistema deve permitir cadastrar, editar e listar clientes.
- RF004 - Registro de Pedidos:
  - O sistema deve permitir criar pedido, incluir itens e calcular valor total.
- RF005 - Validacao de Estoque:
  - O sistema deve bloquear a finalizacao de pedido sem estoque disponivel.
- RF006 - Atualizacao de Status do Pedido:
  - O sistema deve permitir alterar o status do pedido conforme permissao.
- RF007 - Consulta de Historico:
  - O sistema deve permitir consultar pedidos por cliente e periodo.

## 6. Requisitos Nao Funcionais
- RNF001 - Plataforma:
  - O sistema deve ser desenvolvido em Java e executado em computadores Windows, Linux ou macOS com JRE instalada.
- RNF002 - Banco de Dados:
  - O sistema deve usar MySQL para armazenamento persistente.
- RNF003 - Desempenho:
  - Operacoes de listagem e busca local devem responder em ate 3 segundos em rede local estavel.
- RNF004 - Seguranca:
  - Senhas devem ser armazenadas de forma criptografada (hash) no banco de dados.
- RNF005 - Usabilidade:
  - As telas devem ter navegacao simples, validacao de campos obrigatorios e mensagens claras de erro/sucesso.
- RNF006 - Confiabilidade:
  - O banco deve ter rotina de backup diario.

## 7. Observacoes Tecnicas
- Linguagem: Java
- Banco de dados: MySQL
- Conectividade: JDBC
- Arquitetura inicial: Desktop com separacao em Entidades, DAO e Servicos
