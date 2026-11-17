# "The Right Step"

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

Este projeto foi gerado a partir de um modelo que inclui lançadores simples de aplicativos e uma extensão `ApplicationAdapter` que exibe o logotipo da libGDX.

## Platforms

- `core`: Módulo principal com a lógica do aplicativo compartilhada por todas as plataformas.
- `lwjgl3`: Plataforma principal para desktop que utiliza LWJGL3; era chamada de “desktop” em documentos mais antigos.
- `teavm`: Back-end web compatível com a maioria das linguagens JVM.

## Gradle

Este projeto utiliza o [Gradle](https://gradle.org/) para gerenciar dependências.
O wrapper do Gradle foi incluído, de modo que você pode executar tarefas do Gradle usando os comandos `gradlew.bat` ou `./gradlew`.
Tarefas e opções úteis do Gradle:

- `--continue`: ao usar essa opção, os erros não interromperão a execução das tarefas.
- `--daemon`: graças a essa opção, o daemon do Gradle será usado para executar as tarefas selecionadas.
- `--offline`: ao usar essa opção, serão utilizados arquivos de dependências armazenados em cache.
- `--refresh-dependencies`: essa opção força a validação de todas as dependências. Útil para versões snapshot.
- `build`: compila os códigos-fonte e cria os arquivos de todos os projetos.
- `cleanEclipse`: remove os dados do projeto do Eclipse.
- `cleanIdea`: remove os dados do projeto do IntelliJ.
- `clean`: remove as pastas `build`, que armazenam classes compiladas e arquivos gerados.
- `eclipse`: gera os dados do projeto do Eclipse.
- `idea`: gera os dados do projeto do IntelliJ.
- `lwjgl3:jar`: compila o arquivo JAR executável do aplicativo, que pode ser encontrado em `lwjgl3/build/libs`.
- `lwjgl3:run`: inicia o aplicativo.
- `teavm:build`: compila a aplicação JavaScript na pasta `build/dist/webapp`.
- `teavm:run`: disponibiliza a aplicação JavaScript em http://localhost:8080 por meio de um servidor Jetty local.
- `test`: executa testes de unidade (se houver).

Observe que a maioria das tarefas que não são específicas a um único projeto pode ser executada com o prefixo `name:`, onde `name` deve ser substituído pelo ID de um projeto específico.
Por exemplo, `core:clean` remove a pasta `build` apenas do projeto `core`.
