# 🍕 Dev Food - Sistema de Gestão de Pedidos

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

Este é um sistema de gestão para o setor alimentício desenvolvido em **Java SE (Puro)**. O foco principal deste projeto é a aplicação prática de **Padrões de Projeto (Design Patterns)** clássicos para criar uma solução de software organizada e fácil de manter, sem a dependência de frameworks.

---

## 🛠️ Tecnologias e Ferramentas
* **Linguagem:** Java 17
* **Gerenciador de Build:** Apache Maven
* **Armazenamento:** Persistência em arquivos locais (pasta /data)
---

## 🧠 Design Patterns Implementados

A arquitetura do **Dev Food** demonstra maturidade técnica através do uso de:

1. **Singleton:** Garante instâncias únicas para classes de gerenciamento de dados e configurações globais.
2. **Strategy Pattern:** Permite que diferentes algoritmos de negócio (como tipos de desconto ou taxas de entrega) sejam selecionados dinamicamente.
3. **Facade Pattern:** Proporciona uma interface simplificada para o sistema, escondendo a complexidade das interações entre as classes de pedidos e estoque.

---

💡 Desafios e Aprendizados
Implementação Manual de Patterns: O maior desafio foi estruturar os padrões de projeto manualmente, garantindo o baixo acoplamento entre as classes.

Manipulação de Dados: Gerenciamento da leitura e escrita de informações na pasta /data.

---
Desenvolvido por [Ramon Ferreira](https://github.com/RamonFerreira1).
