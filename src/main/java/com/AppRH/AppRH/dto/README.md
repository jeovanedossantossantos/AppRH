# O que é DTO?

DTO, Data Transfer Object ou Objeto de Transferência de Dados é um objeto simples que contém apenas propriedades, que servem para armazenar os dados que serão distribuídos para a aplicação. Ele é frequentemente usado para representar dados recuperados de uma fonte de dados, como um banco de dados. 

A grosso modo, ele define quais atributos serão disponibilizado, por exmplo se faz um requisição para um banco e ele devolve varias informações, porém não é necessaria todas, então o um class DTO vai definir os atributos que seram disponibilzado, se passar a resposta do banco para ela, ela devolve os atributos que serão disponibilizado.

Pode se dizer que é um serializer, para quem vem do python com django.
