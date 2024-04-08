# SpaceTime-WikiMaps

# Criação de Uma Base de Dados no IntelliJ

## Ir a Tab de Database e adicionar um Data Source do tipo H2

![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/1782760c-65ae-4bb4-a39c-71e998d54c6f)

## Nas propriedades da base de dados, definir o nome "demo", alterar o connection Type para Embedded, alterar o caminho para o /data/demo.mv.db para evitar conflitos futuros, colocar o user como root e a password como root, save=forever, testar a conectividade e confirmar a criação da base de dados.

![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/2fbfcfed-fc62-47e8-97a2-f08042b3b436)

## Na classe User é necessário dar assign de uma Data Source
![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/5424f536-44bd-498c-8bf4-be3109f18dde)

## Definir o Data Source como a DB Demo
![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/8a2044bf-62f6-47c7-881c-579038ddb847)

## Correr a aplicação pelo menos 1 vez para criar as tabelas necessárias, a aplicação cria automaticamente um Admin na base de dados
## Confirmar a criação dos dados na Base de Dados
![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/eb909d28-8148-4ac0-a98d-548ab7492c75)
![image](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/ae87a03f-97da-4af1-a7eb-5f68e95d943e)





