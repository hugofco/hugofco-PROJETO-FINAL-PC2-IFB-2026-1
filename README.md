# hugofco-PROJETO-FINAL-PC2-IFB-2026-1

# Sistema de Gestão de Clínica veterinária

Sistema simples para uma clínica veterinária de pequeno porte registrar
tutores, animais, consultas e gerar relatórios gerenciais. Roda localmente
como um servidor HTTP em Java puro (sem frameworks), com interface em
formulários HTML — sem necessidade de terminal para operar o sistema do
dia a dia.

### Como executar: no próprio vscode, ao rodar, ele irá iniciar o server local em: http://localhost:8080. Você pode acessar em um navegador ou dentro do próprio vscode.

## Estrutura do projeto
modelo/         → Pessoa, Funcionario, Tutor, Animal, Cachorro, Gato, Passaro, Porte, EspeciePassaro

comportamento/  → Vacinavel, Internavel, CarteiraVacinacao, RegistroVacina, Internacao

atendimento/    → Consulta, MotivoConsulta, ContaMedica

clinica/        → Clinica, RelatorioPeriodo

servidor/       → ServidorClinica, Paginas

App.java       → ponto de entrada

## Fluxo de uso

A aplicação é dividida em três telas, navegáveis pelo menu principal:

1. **Cadastro** (`/cadastro`) — cadastra um tutor e um animal (cão, gato ou
   pássaro), com campos que mudam conforme a espécie escolhida.
2. **Consulta** (`/consulta`) — registra uma consulta para um animal já
   cadastrado, com motivo do atendimento, vacina aplicada (se aplicável) e
   opção de internação.
3. **Relatório** (`/relatorio`) — informa um período e mostra total
   arrecadado, número de consultas e animais atendidos naquele intervalo.

---

## O que mudou entre `diagrama_v1`, e esta versão v3, e por quê?

Esta seção documenta as decisões de design tomadas ao revisar a modelagem
original, conforme pedido no enunciado ("o que será avaliado é a qualidade
das decisões e a capacidade de justificá-las").

### 1. `Usuario` → `Pessoa`

Nos dois diagramas anteriores, a superclasse abstrata se chamava `Usuario`.
O nome sugere "usuário do sistema" (alguém que faz login), mas na prática a
classe só representa "alguém com nome, CPF, telefone e e-mail" — tanto o
funcionário quanto o tutor. Renomeamos para `Pessoa`, que descreve melhor o
papel real da classe, e `Profissional` passou a se chamar `Funcionario`
(mais claro: é quem trabalha na clínica).

### 2. `Animal` deixou de carregar atributos de espécie específica

Em `diagrama_v1`, a classe `Animal` (abstrata) tinha atributos como
`Tamanho`, `Porte` e `Espécie` todos juntos na superclasse — mas porte só
faz sentido para cão/gato, e espécie de ave só faz sentido para pássaro. Em
`diagrama_v2`, esse problema foi parcialmente resolvido ao criar as
subclasses `Cachorro`, `Gato` e `Pássaro`, mas a classe `Consulta` (antes
`Laudo`) ainda carregava atributos como `Peso`, `Tamanho` e `Porte` — que
são do **animal**, não da consulta. Isso indicava que esses campos não
tinham encontrado o lugar certo na refatoração.

Na versão atual, `Animal` guarda **apenas** o que é universal a qualquer
animal atendido (nome, RGA, peso, tutor, histórico de consultas).
Tudo que é específico de espécie vive exclusivamente na subclasse certa:
`porte` em `Cachorro`/`Gato`, `especie` em `Passaro`.

### 3. Duplicação entre `Cachorro` e `Gato` foi resolvida por comportamento, não por herança de atributo

Em `diagrama_v2`, `Cachorro` e `Gato` tinham exatamente os mesmos atributos
(`Raça`, `Porte`) — o tipo de duplicação que o enunciado avisa para se
atentar. A solução mais "óbvia" seria criar uma superclasse intermediária
(`MamiferoDomestico`, por exemplo) só para hospedar esses dois campos.

Decidimos **não** fazer isso. Os atributos serem iguais não significa que
exista uma relação real de domínio ali — é coincidência. O que de fato é
comum e repetido entre as espécies é o **comportamento de calcular o valor
da consulta**: cada subclasse fazia (ou faria) esse cálculo do zero. Por
isso a duplicação foi eliminada com um método abstrato em `Animal`
(`calcularValorBaseConsulta()`), que cada subclasse implementa com sua
própria regra. Isso resolve a pista do enunciado sobre "quem sabe calcular
o valor de uma consulta" e, de brinde, resolve também a pista de
extensibilidade: para a clínica atender coelhos amanhã, basta criar a
classe `Coelho extends Animal` com seu próprio cálculo — nenhuma outra
classe do sistema precisa mudar.

### 4. Vacinação e internação deixaram de ser atributos e passaram a ser interfaces

Este foi o problema mais importante encontrado em ambos os diagramas
anteriores. O enunciado é explícito: *"alguns animais têm histórico de
vacinação... outros tipos de animal não recebem vacinas pelo protocolo da
clínica"* e *"alguns animais podem precisar ficar internados... outros não
têm estrutura de internação disponível"*. Nos diagramas v1 e v2, porém,
campos como "Status Vacinação" e atributos de internação apareciam soltos
em `Laudo`/`Consulta`, presentes para **qualquer** animal — inclusive
pássaros, que pelo enunciado não vacinam.

Essa é exatamente a situação descrita na dica de modelagem do projeto:
*"quando vocês quiserem adicionar um comportamento a uma classe, mas ele
'não faz sentido' para todas as subclasses, é sinal de que pertence a uma
interface."* Criamos as interfaces `Vacinavel` e `Internavel`.
`Cachorro` e `Gato` implementam ambas; `Passaro` implementa só
`Internavel` (decisão confirmada com a dupla: a clínica também interna
aves, mas não as vacina). Um código que tenta vacinar um `Passaro` agora
nem compila — o erro de modelagem se torna impossível de cometer por
acidente, em vez de só "não fazer sentido" em tempo de execução.

### 5. Carteira de vacinação e internação ganharam classes próprias

O enunciado pede para a clínica "consultar e **atualizar**" o histórico de
vacinação — isso pede uma lista de registros, não um único campo de texto
como existia nos dois diagramas anteriores (`Status Vacinação: char`).
Criamos `RegistroVacina` (nome da vacina + data) e `CarteiraVacinacao`
(lista de registros), permitindo que cada vacina aplicada seja registrada
sem sobrescrever a anterior.

Pelo mesmo motivo, a internação ganhou a classe `Internacao`, com data de
entrada, data de saída e cálculo de valor por dia — atendendo
diretamente ao requisito de que a internação "gera uma cobrança separada da
consulta, calculada por dia de internação".

### 6. `Laudo`/`Consulta` foi limpa e ganhou responsabilidade clara

Nos diagramas anteriores, a classe que registrava o atendimento (`Laudo`
em v1, `Consulta` em v2) acumulava informações de origens diferentes:
dados do animal, dados de vacinação, prioridade de agendamento, etc. Na
versão atual, `Consulta` guarda só o que de fato é dela: data, veterinário
responsável, animal atendido, motivo do atendimento e resultado. O cálculo
de valor final combina o valor base do animal com o multiplicador do
motivo — cada classe contribuindo apenas com a informação que ela mesma
possui.

### 7. Nova classe `ContaMedica` para fechar a cobrança

Nenhum dos diagramas anteriores modelava explicitamente como o valor da
consulta e o valor da internação se somam para o tutor pagar. Criamos
`ContaMedica`, que recebe a consulta e (opcionalmente) a internação, aplica
desconto e devolve o valor final — sem misturar essa responsabilidade
dentro de `Consulta` ou `Animal`.

### 8. Nova classe `Clinica` para gestão e relatório

Para atender ao requisito de relatório por período (total arrecadado,
número de consultas, animais atendidos), criamos uma classe de gestão
central, responsável por manter as listas de tutores, funcionários e
consultas, e por calcular o relatório — em vez de espalhar essa lógica
pelas telas do servidor web.

---

## Limitações conhecidas (fora do escopo deste projeto)

- Os dados ficam apenas em memória; ao reiniciar o servidor, tudo se perde
  (não há persistência em arquivo ou banco de dados).
- Não há autenticação de usuário — qualquer pessoa com acesso ao
  `localhost:8080` pode usar todas as telas.
- O veterinário responsável pelas consultas é fixo, para simplificar o
  fluxo de telas; um sistema de login completo está fora do escopo do
  enunciado.
