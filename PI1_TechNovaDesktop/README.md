# PI1 - TechNova Solucoes Desktop

Projeto base para o Projeto Integrador I, com documentacao, diagrama de classes e interface grafica em Java Swing.

## Tecnologias
- Java
- MySQL com persistencia via JDBC
- Estrutura compativel com NetBeans/Maven

## Estrutura
- `docs/documentacao_tecnica_pi1.md`: Documento de projeto com requisitos
- `docs/diagrama_classes.md`: Diagrama simples de classes
- `src/`: Classes de dominio, DAO, servicos e interface Swing

## Telas implementadas (Etapa 3)
- Login de usuario (Administrador e Funcionario)
- Cadastro/consulta/edicao/remocao de produtos
- Criacao de pedidos com validacao de estoque e alteracao de status

## Credenciais de teste
- `admin@technova.com` / `admin123`
- `vendas@technova.com` / `vendas123`

## Banco de dados
1. Execute o script `../bd_technova.sql` no MySQL Workbench para criar o schema `technova_db` e popular dados iniciais.
2. Ajuste as credenciais de conexao em `src/ConexaoMySQL.java` conforme seu ambiente local.
3. Garanta que o MySQL esteja ativo antes de executar o projeto.

## Executar no Apache NetBeans
1. Abra o Apache NetBeans.
2. Clique em `File > Open Project` e selecione a pasta `PI1_TechNovaDesktop` (o NetBeans deve reconhecer como projeto Maven).
3. Aguarde o download das dependencias Maven na primeira abertura.
4. Confirme o JDK em `Project Properties > Build > Compile` (recomendado: Java 17).
5. Execute com `Run Project` (`F6`).
6. A aplicacao iniciara na tela `Login`.


