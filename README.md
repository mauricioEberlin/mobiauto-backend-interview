# Gestão de revendas

### Introdução

O projeto de avaliação da Mobiauto consiste em uma ferramenta de gestão de revendas de veículos.
O objetivo é tornar esse processo de revenda mais eficiente, utilizando um sistema de cadastro de usuários, revendedoras e oportunidades de revenda, que são atendidos pelos funcionários da loja. 
As oportunidades são distribuídas automaticamente aos funcionários que ficaram sem receber uma tarefa por mais tempo.
Assim que a oportunidade é concluída, ele deve fornecer o motivo de conlusão e em seguida o horário da finalização é registrada.
A aplicação utiliza **Basic Authentication**, precisando informar email e senha do usuário antes de realizar as requisições.
Foi utilizado o banco de dados integrado **H2** por ser um projeto de demonstração.

#### Vídeo de demonstração: 

### Pré-requisitos

* Java JDK 17.0.10
* IntelliJ IDEA
* Postman

### Iniciando o projeto

1. Utilizando uma IDE, abra o projeto "mobiauto-backend-interview" e aguarde a instalação de todas as dependências;
2. Após ter baixado todas as dependências, execute o "maven-clean" e em seguida o "maven-install";
3. Acesse a classe da aplicação (MobiautoBackendInterviewApplication.java) e execute a aplicação;
4. Acesse em seu navegador o link http://localhost:8080/swagger-ui/index.html para consultar a documentação da API;
5. Use uma API Client (ex. Postman) para realizar busca, cadastro, alteração e exclusão dos dados.
6. Antes de qualquer requisição, na aba *Authorization*, em *Auth Type* selecione a opção *Basic Auth* e informe email e senha dos usuários cadastrados. Por padrão, ao iniciar o projeto o administrador já é cadastrado.
* Email do administrador: admin@email.com
* Senha do administrador: 9090

### Dependências utilidadas

**Versão do Spring Boot: 3.2.5**
* H2
* Lombok
* Spring Boot Starter Test
* Spring Boot Starter Security
* Spring Boot Starter data JPA
* Springdoc Openapi Starter Webmvc Ui

### Teste unitário

Atualmente a cobertura de testes é de 95%.
Para executar o teste basta acessar a pasta raiz do projeto e clicar na opção "Run with coverage".

### Arquitetura

As responsabilidades da aplicação foram divididas entre componentes, com nomenclaturas que mais se adequa ao REST.
Os componentes são: 

* Model
Esse pacote é responsável pelas entidades existentes na aplicação. As principais são Usuario, Revenda e Oportunidade.

* Repository
Interfaces que se comunicam diretamente com o banco de dados, contendo regras de negócio como fazer consultas, alterar ou excluir dados.

* Service
Comunicando com o Repository, é responsável por processar dados, como a verificação se o email informado pelo usuário já existe no sistema.

* Controller
Responsável pela preparação dos dados e pelo direcionamento das ordens recebidas pelo usuário que utiliza o sistema. Todos os endpoins se localizam nesse pacote.

* Security
Responsável pelo serviço de autenticação do projeto. Aqui ocorre a validação do email e senha informado pelo usuário para saber se ele possuí acesso ao sistema. Após esse processo o usuário terá "Rótulos" que indicam quais funções do sistema ele tem ou não tem acesso (gerando erro status 403). Esses rótulos são, em nível de acesso, respectivamente: ADMINISTRADOR, PROPRIETARIO, GERENTE e ASSISTENTE.
Este sistema utiliza o Basic Authentication.

* Config
Aqui estão armazenados as constantes do projeto, afim de facilitar alterações futuras.

* Jobs
Pacote responsável por realizar funções logo ao iniciar o projeto, como o cadastro do administrador.

### Variáveis de ambiente

As variáveis de ambiente estão localizadas na pasta *resources* no arquivo *application.properties*. Atualmente com os valores padrão.

* `spring.datasource.username=sa` Username do banco de dados.
* `spring.datasource.password=` Senha do banco de dados.
* `spring.h2.console.enabled=true` Exibir console na rota informada.
* `spring.h2.console.path=/h2-console` Rota do console.
* `spring.jpa.show-sql=false` Exibir a criação do banco de dados no log ao iniciar a aplicação.
* `spring.jpa.properties.hibernate.format_sql=false` Formatar de forma mais compreensível o log de criação do banco.

### Contato

E-mail: mauricio.eberlin@gmail.com
[LinkedIn](https://www.linkedin.com/in/mauricioeb/)
